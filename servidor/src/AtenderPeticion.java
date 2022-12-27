import modelos.Archivo;
import modelos.Usuario;

import java.io.*;
import java.net.Socket;
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
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))){
            username=reader.readLine();
            if(Servidor.usuariosSistema.getUsuario(username)==null){
                Usuario nuevo=new Usuario(username,null,null);
                writer.write("nuevo");
                nuevo.setEmail(reader.readLine());
                pass=reader.readLine();
                nuevo.setContrasena(pass);
                nuevo.setLinea(true);
                Servidor.usuariosSistema.anadirUsuario(nuevo);
            }else{
                writer.write("bienvenido");
                pass=reader.readLine();
                while(!Servidor.usuariosSistema.actualizarSesion(username,pass,true)){
                    writer.write("denegado");
                    pass=reader.readLine();
                }
            }
            this.cliente=Servidor.usuariosSistema.getUsuario(username);
            writer.write("inicio");
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
                        bajarArchivo(reader.readLine(),usuario.equals("yes"));
                    }break;
                    case "buscar":{
                        String queryName=reader.readLine();
                        this.nombresArchivos(false,Servidor.usuariosSistema.getUsuario(queryName));
                    }break;
                    case "eliminar":{
                        // crear una funcion que elimine de la nube un archivo
                    }break;
                    case "publicar":{
                        // crear una funcion que establezca un archivo del usuario como publico
                    }break;
                    case "compartir":{
                        // crear una funcion que comparta un archivo con x usuario
                    }break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally{
            if(pass!=null){
                Servidor.usuariosSistema.actualizarSesion(username,pass,false);
            }
        }
    }

    public void nombresArchivos(boolean esElCliente,Usuario user) throws IOException {
        String username=user.getNombre();
        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(this.client.getOutputStream()));
        List<Archivo> archs;
        if(esElCliente){
            archs=user.getArchivos();
        }else{
            archs=user.getArchivosCompartidos(username);
        }
        int i=1;
        for(Archivo a:archs){
            String descargas="";
            if(a.esPublico()) descargas=" | Descargas: "+a.getDescargas();
            writer.write(i+": "+a.getPath()+descargas);
            i++;
        }
        writer.write("fin");
    }

    public void subirArchivo(String nombre) throws IOException{
        DataInputStream dis=new DataInputStream(client.getInputStream());
        String path=Servidor.carpetaRecursos.getPath()+"\\"+this.cliente.getNombre()+"\\"+nombre;
        File f=new File(path);
        if(!f.exists()){
            f.createNewFile();
        }
        FileOutputStream fos=new FileOutputStream(f);
        byte[] buff=new byte[1024];
        int leidos= dis.read(buff);
        while(leidos!= -1) {
            fos.write(buff, 0, leidos);
            leidos = dis.read(buff);
        }
    }

    public void bajarArchivo(String nombre,boolean esElCliente) throws IOException{
        DataOutputStream dos=new DataOutputStream(client.getOutputStream());
        String path=Servidor.carpetaRecursos.getPath()+"\\";
        if(esElCliente){
            path=path+this.cliente.getNombre();
        }
        path=path+"\\"+nombre;
        File f=new File(path);
        if(f.exists()){
            FileInputStream fis=new FileInputStream(f);
            byte[] buff=new byte[1024];
            int leidos= fis.read(buff);
            while(leidos!= -1) {
                dos.write(buff, 0, leidos);
                leidos = fis.read(buff);
            }
        }else{
            dos.writeBytes("Error");
        }

    }
}
