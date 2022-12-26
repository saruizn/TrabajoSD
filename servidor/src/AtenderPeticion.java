import modelos.Archivo;
import modelos.Usuario;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class AtenderPeticion implements Runnable{

    private Socket client;

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
            Usuario usuario=Servidor.usuariosSistema.getUsuario(username);
            writer.write("inicio");
            String opcion;
            while(!(opcion=reader.readLine()).equals("salir")){
                switch(opcion){
                    case "nombres":{
                        this.nombresArchivos(usuario,true,"");
                    }break;
                    case "subir":{
                        // crear funcion que cree el archivo en la nube
                    }break;
                    case "bajar":{
                        // crear funcion que descargue el archivo de la nube
                    }break;
                    case "buscar":{
                        String queryName=reader.readLine();
                        this.nombresArchivos(Servidor.usuariosSistema.getUsuario(queryName),false,usuario.getNombre());
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

    public void nombresArchivos(Usuario usuario,boolean esElCliente,String username) throws IOException {
        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        List<Archivo> archs;
        if(esElCliente){
            archs=usuario.getArchivos();
        }else{
            archs=usuario.getArchivosCompartidos(username);
        }
        int i=1;
        for(Archivo a:archs){
            String descargas="";
            if(a.esPublico()) descargas=" | Descargas: "+a.getDescargas();
            writer.write(i+": "+a.getPath()+descargas);
            i++;
        }

    }
}
