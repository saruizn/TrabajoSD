import modelos.ListaUsuarios;
import modelos.Usuario;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {

    private static ListaUsuarios usuariosSistema;

    private static File carpetaRecursos;

    public static void main(String[] args){
        iniciarUsuarios();
        iniciarRecursos(usuariosSistema);
        try{
            ServerSocket ss=new ServerSocket(55555);
            ExecutorService pool = Executors.newCachedThreadPool();
            while(true){
                try{
                    Socket client=ss.accept();
                    //pool.execute(new AtenderPeticion());  en atenderPetición estará to el meollo, no he creado la clase

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void iniciarUsuarios(){
        Scanner sc=new Scanner(System.in);
        System.out.println("Especifique la ruta del archivo \"usuarios.xml\" (Deje vacío para usar el predefinido en la carpeta del proyecto.)");
        String usuariosPath=sc.nextLine();
        File usuarios=null;
        if(usuariosPath.equals("")){
            usuarios = new File(System.getProperty("user.dir")+"\\usuarios.xml");
        }else{
            usuarios = new File(usuariosPath);
        }
        if (!usuarios.exists()){
            try {
                usuarios.createNewFile();
                FileOutputStream fos=new FileOutputStream(usuarios);
                fos.write("<raiz/>".getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        ListaUsuarios listaUsuarios=null;
        try {
            usuariosSistema=new ListaUsuarios(usuarios);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        usuariosSistema.anadirUsuario(new Usuario("samu","samu222rn@gmail.com","1234"));
    }

    public static void iniciarRecursos(ListaUsuarios listaUsuarios){
        Scanner sc=new Scanner(System.in);
        System.out.println("Especifique la ruta del directorio \"recursos\" (Deje vacío para usar el predefinido en la carpeta del proyecto.)");
        String recursosPath=sc.nextLine();
        File recursos=null;
        if(recursosPath.equals("")){
            recursos = new File(System.getProperty("user.dir")+"\\recursos");
            if (!recursos.exists()){
                recursos.mkdir();
            }
        }else{
            recursos = new File(recursosPath);
            if (!recursos.exists()){
                recursos.mkdirs();
            }
        }
        carpetaRecursos=recursos;
        for(Usuario u: listaUsuarios.getUsuarios()){
            File carpetaPersonal = new File(recursos.getAbsolutePath()+"\\"+u.getNombre());
            if(!carpetaPersonal.exists()){
                carpetaPersonal.mkdir();
            }
        }
    }

}
