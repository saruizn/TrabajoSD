import modelos.ListaUsuarios;
import modelos.Usuario;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Servidor {
    public static void main(String[] args){
        ListaUsuarios listaUsuarios=iniciarUsuarios();
        iniciarRecursos(listaUsuarios);
    }

    public static ListaUsuarios iniciarUsuarios(){
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
            listaUsuarios=new ListaUsuarios(usuarios);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        listaUsuarios.anadirUsuario(new Usuario("samu","samu222rn@gmail.com","1234"));
        return listaUsuarios;
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
        for(Usuario u: listaUsuarios.getUsuarios()){
            File carpetaPersonal = new File(recursos.getAbsolutePath()+"\\"+u.getNombre());
            if(!carpetaPersonal.exists()){
                carpetaPersonal.mkdir();
            }
        }
    }
}
