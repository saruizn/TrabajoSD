import modelos.Archivo;
import modelos.Usuario;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class AtenderPeticion implements Runnable{

    private Socket client;

    private Usuario cliente;

    public AtenderPeticion(Socket client){
        this.client=client;
    }

    public void run() {
        String username=null;
        String pass=null;
        try(BufferedReader reader=new BufferedReader(new InputStreamReader(client.getInputStream()));
            DataOutputStream writer=new DataOutputStream(client.getOutputStream())){
            username=reader.readLine();
            if(Servidor.usuariosSistema.getUsuario(username)==null){
                Usuario nuevo=new Usuario(username,null,null);
                writer.writeBytes("nuevo"+"\n");
                nuevo.setEmail(reader.readLine());
                pass=reader.readLine();
                nuevo.setContrasena(pass);
                nuevo.setLinea(true);
                Servidor.usuariosSistema.anadirUsuario(nuevo);
                File carpetaPersonal = new File(Servidor.carpetaRecursos.getAbsolutePath()+"\\"+nuevo.getNombre());
                if(!carpetaPersonal.exists()){
                    carpetaPersonal.mkdir();
                }else{
                    for(File f:carpetaPersonal.listFiles()){
                        nuevo.anadirArchivo(f);
                    }
                }
            }else{
                writer.writeBytes("bienvenido"+"\n");
                pass=reader.readLine();
                while(!Servidor.usuariosSistema.actualizarSesion(username,pass,true)){
                    writer.writeBytes("denegado"+"\n");
                    pass=reader.readLine();
                }
            }
            this.cliente=Servidor.usuariosSistema.getUsuario(username);
            writer.writeBytes("inicio"+"\n");
            String opcion;
            while(!(opcion=reader.readLine()).equals("salir")){
                switch(opcion){
                    case "nombres":{
                        this.nombresArchivos(true,this.cliente);
                    }break;
                    case "subir":{
                        subirArchivo(reader.readLine());
                    }break;
                    case "bajar":{
                        String usuario=reader.readLine();
                        if(usuario.equals("yes")){
                            try{
                                bajarArchivo(reader.readLine(), true);
                            }catch(IOException e){
                                writer.writeBytes("Error"+"\n");
                            }

                        }else{
                            Usuario u=Servidor.usuariosSistema.getUsuario(reader.readLine());
                            String arch=reader.readLine();
                            boolean found=false;
                            for(Archivo a:u.getArchivosCompartidos(cliente.getNombre())){
                                if(a.tienePermisos(cliente.getNombre()) && a.getPath().equals(u.getNombre()+"\\"+arch)){
                                    try{
                                        bajarArchivo(a.getPath(), false);
                                        a.descarga();
                                        found=true;
                                    }catch(IOException ignored){}
                                }
                            }
                            if(!found) writer.writeBytes("Error"+"\n");
                        }
                    }break;
                    case "buscar":{
                        String user=reader.readLine();
                        Usuario u=Servidor.usuariosSistema.getUsuario(user);
                        if(u!=null){
                            writer.writeBytes("OK"+"\n");
                            this.nombresArchivos(false,u);
                        }else{
                            writer.writeBytes("Error"+"\n");
                        }
                    }break;
                    case "eliminar":{
                        String fileName=reader.readLine();
                        Archivo eliminar=null;
                        for(Archivo a:this.cliente.getArchivos()){
                            if(a.getPath().equals(fileName)){
                                eliminar=a;
                            }
                        }
                        if(eliminar!=null){
                            this.cliente.getArchivos().remove(eliminar);
                            eliminar.borrar();
                            writer.writeBytes("OK"+"\n");
                        }else{
                            writer.writeBytes("Error"+"\n");
                        }
                    }break;
                    case "publicar":{
                        String fileName=reader.readLine();
                        Archivo publicar=null;
                        for(Archivo a:this.cliente.getArchivos()){
                            if(a.getPath().equals(fileName)){
                                publicar=a;
                            }
                        }
                        if(publicar!=null){
                            publicar.setPublico(!publicar.esPublico());
                            writer.writeBytes("OK"+"\n");
                            if(publicar.esPublico()){
                                writer.writeBytes("Publico"+"\n");
                            }else{
                                writer.writeBytes("Privado"+"\n");
                            }
                        }else{
                            writer.writeBytes("Error"+"\n");
                        }
                    }break;
                    case "compartir":{
                        String file= reader.readLine();
                        String name= reader.readLine();
                        File f=new File(Servidor.carpetaRecursos.getPath()+"\\"+cliente.getNombre()+"\\"+file);
                        if(f.exists() && f.isFile() && Servidor.usuariosSistema.getUsuario(name)!=null){
                            boolean anadido=false;
                            for(Archivo a:cliente.getArchivos()){
                                if(a.getPath().equals(username+"\\"+file)){
                                    if(a.tienePermisos(name)){
                                        a.eliminarUsuario(name);
                                    }else{
                                        a.anadirUsuario(name);
                                        anadido=true;
                                    }
                                }
                            }
                            if(anadido){
                                writer.writeBytes("anadido"+"\n");
                            }else{
                                writer.writeBytes("eliminado"+"\n");
                            }
                        }else{
                            writer.writeBytes("Error"+"\n");
                        }
                    }break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally{
            if(pass!=null){
                Servidor.usuariosSistema.actualizarSesion(username,pass,false);
            }
            System.out.println("Cliente desconectado");
        }
    }

    public void nombresArchivos(boolean esElCliente,Usuario user) throws IOException {
        DataOutputStream writer=new DataOutputStream(this.client.getOutputStream());
        List<Archivo> archs;
        if(esElCliente){
            archs=user.getArchivos();
        }else{
            archs=user.getArchivosCompartidos(cliente.getNombre());
        }
        int i=1;
        for(Archivo a:archs){
            String descargas="";
            if(a.esPublico()) descargas=" | Descargas: "+a.getDescargas();
            writer.writeBytes(i+": "+a.getPath()+descargas+"\n");
            i++;
        }
        writer.writeBytes("fin"+"\n");
    }

    public void subirArchivo(String nombre) throws IOException{
        DataInputStream dis=new DataInputStream(client.getInputStream());
        String path=Servidor.carpetaRecursos.getPath()+"\\"+this.cliente.getNombre()+"\\"+nombre;
        File f=new File(path);
        if(!f.exists()){
            f.createNewFile();
        }
        try(FileOutputStream fos=new FileOutputStream(f)){
            long size=dis.readLong();
            for(long l=0;l<size;l++){
                fos.write(dis.readByte());
            }
            cliente.anadirArchivo(f);
        }catch(IOException e){
            dis.readLong();
            e.printStackTrace();
        }
    }

    public void bajarArchivo(String nombre,boolean esElCliente) throws IOException{
        DataOutputStream dos=new DataOutputStream(client.getOutputStream());
        BufferedReader reader=new BufferedReader(new InputStreamReader(client.getInputStream()));
        String path=Servidor.carpetaRecursos.getPath()+"\\";
        if(esElCliente){
            path=path+this.cliente.getNombre()+"\\";
        }
        path=path+nombre;
        File f=new File(path);
        if(f.exists()){
            FileInputStream fis=new FileInputStream(f);
            long size= Files.size(Paths.get(path));
            dos.writeBytes("OK"+"\n");
            if(reader.readLine().equals("OK")){
                dos.writeLong(size);
            }
            byte[] buff=new byte[1024];
            int leidos= fis.read(buff);
            while(leidos!= -1) {
                dos.write(buff, 0, leidos);
                leidos = fis.read(buff);
            }
            fis.close();
        }else{
            dos.writeBytes("Error"+"\n");
        }

    }
}
