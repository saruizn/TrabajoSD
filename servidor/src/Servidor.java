import modelos.ListaUsuarios;
import modelos.Usuario;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Servidor {
    public static void main(String[] args){
        System.out.println("Especifique la ruta del directorio \"recursos\" (Deje vacío para usar el predefinido en la carpeta del proyecto.)");
        Scanner sc=new Scanner(System.in);
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
        System.out.println("Especifique la ruta del archivo \"usuarios.xml\" (Deje vacío para usar el predefinido en la carpeta del proyecto.)");
        String usuariosPath=sc.nextLine();
        File usuarios=null;
        if(usuariosPath.equals("")){
            usuarios = new File(System.getProperty("user.dir")+"\\usuarios.xml");
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
        }else{
            recursos = new File(recursosPath);
            if (!recursos.exists()){
                try {
                    recursos.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        ListaUsuarios listaUsuarios=null;
        try {
            listaUsuarios=new ListaUsuarios(usuarios);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        listaUsuarios.anadirUsuario(new Usuario("samuel","samu@gmail.com","xddd"));
        for(Usuario u: listaUsuarios.getUsuarios()){
            File carpetaPersonal = new File(recursos.getAbsolutePath()+"\\"+u.getNombre());
            if(!carpetaPersonal.exists()){
                carpetaPersonal.mkdir();
            }
        }
    }
}
