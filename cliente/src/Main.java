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
                System.out.println("Denegado.");
                pass=sc.nextLine();
                writer.writeBytes(pass+"\n");
            }
            System.out.println("Bienvenido al MiNube, "+username);
            System.out.println("---------------------------------------------------------");
            String opcion;
            String rutaDefecto;
            do{
                System.out.println("Escriba una ruta por defecto para guardar archivos descargados:");
                rutaDefecto= sc.nextLine();
            }while(!isValidPath(rutaDefecto));
            //rutaDefecto="C:\\Users\\samu2\\Desktop"; // C:\Users\samu2\Desktop\ejemploArchivo.txt
            do{
                System.out.println("Elige una opcion:");
                System.out.println("1-Ver la lista de mis archivos.");
                System.out.println("2-Descargar un archivo de mi lista de archivos.");
                System.out.println("3-Subir un archivo.");
                System.out.println("4-Ver archivos de un usuario.");
                System.out.println("5-Eliminar un archivo de la nube.");
                System.out.println("6-Publicar/despublicar un archivo.");
                System.out.println("7-Compartir/descomaprtir un archivo con un usuario.");
                System.out.println("8-Salir.");
                opcion=sc.nextLine();
                System.out.println("---------------------------------------------------------");
                switch(opcion){
                    case "1":{
                        writer.writeBytes("nombres"+"\n");
                        String line=reader.readLine();
                        if(line.equals("fin")){
                            System.out.println("No existen archivos.");
                        }else{
                            while(!line.equals("fin")){
                                System.out.println(line);
                                line=reader.readLine();
                            }
                        }
                    }break;
                    case "2":{
                        System.out.println("Escriba el nombre del archivo a descargar (o su numero en la lista).");
                        String arch=getArchivo(sc,reader,writer,username,true);
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
                            File c=new File(route);
                            if(!c.exists()){
                                c.mkdirs();
                            }
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
                            boolean tiene=false;
                            String line=reader.readLine();
                            if(!line.equals("fin")){
                                tiene=true;
                                while(!line.equals("fin")){
                                    System.out.println(line);
                                    line=reader.readLine();
                                }
                            }
                            if(tiene){
                                System.out.println("Escribe el nombre de un archivo para descargarlo (o su numero en la lista), pulsa Enter para volver.");
                                String arch=getArchivo(sc,reader,writer,name,false);
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
                                        File c=new File(route);
                                        if(!c.exists()){
                                            c.mkdirs();
                                        }
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
                                System.out.println("El usuario no tiene archivos publicos.");
                            }
                        }else{
                            System.out.println("Usuario no encontrado");
                        }
                    }break;
                    case "5":{
                        System.out.println("Introduzca el nombre del archivo a eliminar (o su numero en la lista).");
                        String fileName=getArchivo(sc,reader,writer,username,true);
                        fileName=username+"\\"+fileName;
                        writer.writeBytes("eliminar"+"\n");
                        writer.writeBytes(fileName+"\n");
                        if(reader.readLine().equals("OK")){
                            System.out.println("Archivo eliminado.");
                        }else{
                            System.out.println("Archivo no encontrado.");
                        }
                    }break;
                    case "6":{
                        System.out.println("Introduzca el nombre del archivo a publicar/despublicar (o su numero en la lista).");
                        String fileName=getArchivo(sc,reader,writer,username,true);
                        fileName=username+"\\"+fileName;
                        writer.writeBytes("publicar"+"\n");
                        writer.writeBytes(fileName+"\n");
                        if(reader.readLine().equals("OK")){
                            if(reader.readLine().equals("Publico")){
                                System.out.println("El archivo ahora es público.");
                            }else{
                                System.out.println("El archivo ahora es privado.");
                            }
                        }else{
                            System.out.println("Archivo no encontrado.");
                        }
                    }break;
                    case "7":{
                        System.out.println("Escribe el nombre del archivo a compartir (o su numero en la lista)");
                        String arch=getArchivo(sc,reader,writer,username,true);
                        System.out.println("Escribe el nombre del usuario a compartir/descompartir.");
                        String name=sc.nextLine();
                        writer.writeBytes("compartir"+"\n");
                        writer.writeBytes(arch+"\n");
                        writer.writeBytes(name+"\n");
                        String result=reader.readLine();
                        if(result.equals("Error")) System.out.println("Hubo un error, puede que no exista el archivo o el usuario introducidos.");
                        if(result.equals("anadido")) System.out.println("El usuario "+name+" ahora tiene acceso al archivo "+arch);
                        if(result.equals("eliminado")) System.out.println("El usuario "+name+" ya no tiene acceso al archivo "+arch);
                    }break;
                }
                System.out.println("---------------------------------------------------------");
            }while(!opcion.equals("8"));
            writer.writeBytes("salir"+"\n");
            System.out.println("Hasta la próxima.");
        } catch (IOException e) {
            System.out.println("Error, servidor fuera de linea.");
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

    public static String getArchivo(Scanner sc, BufferedReader reader,DataOutputStream writer,String username,boolean isUser) throws IOException{
        String arch=sc.nextLine();
        int num=0;
        try{
            num=Integer.parseInt(arch);
        }catch (NumberFormatException ignored){}
        boolean acabado=false;
        if(num>0){
            if(isUser){
                writer.writeBytes("nombres"+"\n");
            }else{
                writer.writeBytes("buscar"+"\n");
                writer.writeBytes(username+"\n");
                reader.readLine();
            }
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
        if(acabado) arch="";
        return arch;
    }
}