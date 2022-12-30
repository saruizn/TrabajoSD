import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try(Socket con=new Socket("localhost",55555);
            BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream()));
            DataOutputStream writer=new DataOutputStream((con.getOutputStream()))){
            Scanner sc=new Scanner(System.in);
            System.out.println("Bienvenido, introduce tu nombre de usuario.");
            String username=sc.nextLine();
            writer.writeBytes(username+"\n");
            String res= reader.readLine();
            if(res.equals("nuevo")){
                System.out.println("Introduce tu email.");
                writer.writeBytes(sc.nextLine()+"\n");
            }
            System.out.println("Introduce tu contraseña.");
            String pass=sc.nextLine();
            writer.writeBytes(pass+"\n");
            while(reader.readLine().equals("denegado")){
                System.out.println("Contraseña incorrecta.");
                pass=sc.nextLine();
                writer.writeBytes(pass+"\n");
            }
            System.out.println("Bienvenido al MiNube, "+username);
            System.out.println("---------------------------------------------------------");
            String opcion;
            String rutaDefecto;
            /*
            do{
                System.out.println("Eescriba una ruta por defecto para guardar archivos descargados:");
                rutaDefecto= sc.nextLine();
            }while(!isValidPath(rutaDefecto));

             */
            rutaDefecto="C:\\Users\\samu2\\Desktop"; // C:\Users\samu2\Desktop\ejemploArchivo.txt
            do{
                System.out.println("Elige una opcion:");
                System.out.println("1-Ver la lista de mis archivos.");
                System.out.println("2-Descargar un archivo de mi lista de archivos.");
                System.out.println("3-Subir un archivo.");
                System.out.println("4-Ver archivos de un usuario.");
                System.out.println("5-Salir.");
                System.out.println("---------------------------------------------------------");
                opcion=sc.nextLine();
                switch(opcion){
                    case "1":{
                        writer.writeBytes("nombres"+"\n");
                        String line;
                        while(!(line=reader.readLine()).equals("fin")) System.out.println(line);
                    }break;
                    case "2":{
                        System.out.println("Escriba el nombre del archivo o su numero en la lista.");
                        String arch=sc.nextLine();
                        int num=0;
                        try{
                            num=Integer.parseInt(arch);
                        }catch (NumberFormatException ignored){}
                        if(num!=0){
                            writer.writeBytes("nombres"+"\n");
                            boolean acabado=false;
                            int i=0;
                            while(i<num && !acabado){
                                arch=reader.readLine();
                                if(arch.equals("fin")) acabado=true;
                                i++;
                            }
                            if (!acabado) {
                                arch=arch.substring(username.length()+4);
                                if(arch.contains("| Descargas:")){
                                    arch=arch.substring(0,arch.indexOf(" | "));
                                }
                                while(!reader.readLine().equals("fin"));
                            }
                        }
                        if(arch==null) arch="";
                        writer.writeBytes("bajar"+"\n");
                        writer.writeBytes("yes"+"\n");
                        writer.writeBytes(arch+"\n");
                        if(reader.readLine().equals("OK")){
                            String route;
                            do{
                                System.out.println("Especifique la carpeta destino para el archivo (o Enter para usar la ruta por defecto).");
                                route=sc.nextLine();
                                if(route.equals("")){
                                    route=rutaDefecto;
                                }
                            }while(!isValidPath(route));
                            File f=new File(route+"\\"+arch);
                            if(!f.exists()){
                                f.createNewFile();
                            }
                            try(FileOutputStream fos=new FileOutputStream(f)){
                                DataInputStream dis=new DataInputStream(con.getInputStream());
                                writer.writeBytes("OK"+"\n");
                                long size=dis.readLong();
                                for(long l=0;l<size;l++){
                                    fos.write(dis.readByte());
                                }
                                System.out.println("Archivo descargado. Tamaño: "+size+" bytes.");
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }else{
                            System.out.println("Archivo no encontrado");
                        }
                    }break;
                    case "3":{
                        System.out.println("Escribe la ruta del archivo.");  // C:\Users\samu2\IdeaProjects\TrabajoSD\prueba.txt
                        String path=sc.nextLine();
                        File f=new File(path);
                        if(f.exists()){
                            writer.writeBytes("subir"+"\n");
                            writer.writeBytes(path.substring(path.lastIndexOf("\\")+1)+"\n");
                            long size= Files.size(Paths.get(path));
                            writer.writeLong(size);
                            try(FileInputStream fis=new FileInputStream(f)){
                                byte[] buff=new byte[1024];
                                int leidos= fis.read(buff);
                                while(leidos!= -1) {
                                    writer.write(buff, 0, leidos);
                                    leidos = fis.read(buff);
                                }
                                System.out.println("Archivo subido.");
                            }catch(IOException e){
                                System.out.println("Error al leer el archivo.");
                            }
                        }else{
                            System.out.println("Archivo no encontrado.");
                        }
                    }break;
                    case "4":{
                        writer.writeBytes("buscar"+"\n");
                        System.out.println("Introduce el nombre del usuario a buscar:");
                        String name=sc.nextLine();
                        writer.writeBytes(name+"\n");
                        if(reader.readLine().equals("OK")){
                            String line;
                            while(!(line=reader.readLine()).equals("fin")) System.out.println(line);
                            System.out.println("Escribe el nombre de un archivo para descargarlo, pulsa Enter para volver.");
                            String arch=sc.nextLine();
                            if(!arch.equals("")){
                                writer.writeBytes("bajar"+"\n");
                                writer.writeBytes("no"+"\n");
                                writer.writeBytes(name+"\n");
                                writer.writeBytes(arch+"\n");
                                if(reader.readLine().equals("OK")){
                                    String route;
                                    do{
                                        System.out.println("Especifique la carpeta destino para el archivo (o Enter para usar la ruta por defecto).");
                                        route=sc.nextLine();
                                        if(route.equals("")){
                                            route=rutaDefecto;
                                        }
                                    }while(!isValidPath(route));
                                    File f=new File(route+"\\"+arch);
                                    if(!f.exists()){
                                        f.createNewFile();
                                    }
                                    try(FileOutputStream fos=new FileOutputStream(f)){
                                        DataInputStream dis=new DataInputStream(con.getInputStream());
                                        writer.writeBytes("OK"+"\n");
                                        long size=dis.readLong();
                                        for(long l=0;l<size;l++){
                                            fos.write(dis.readByte());
                                        }
                                        System.out.println("Archivo descargado. Tamaño: "+size+" bytes.");
                                    }catch(IOException e){
                                        e.printStackTrace();
                                    }
                                }else{
                                    System.out.println("Error al recuperar el archivo.");
                                }
                            }
                        }else{
                            System.out.println("Usuario no encontrado");
                        }
                    }break;
                }
                System.out.println("---------------------------------------------------------");
            }while(!opcion.equals("5"));
            writer.writeBytes("salir"+"\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }
}