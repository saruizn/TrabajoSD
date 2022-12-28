import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
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
            String opcion="";
            do{
                System.out.println("Elige una opcion");
                System.out.println("1-Ver la lista de mis archivos.");
                System.out.println("2-Descargar un archivo de mi lista de archivos.");
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
                        }catch (NumberFormatException nfe){}
                        if(num!=0){
                            writer.writeBytes("nombres"+"\n");
                            for(int i=1;i<num;i++){
                                reader.readLine();
                            }
                            arch=reader.readLine();
                            while(!reader.readLine().equals("fin"));
                        }
                        if(arch==null) arch="";
                        writer.writeBytes("bajar"+"\n");
                        writer.writeBytes("yes"+"\n");
                        writer.writeBytes(arch+"\n");
                        if(reader.readLine().equals("OK")){
                            File f=new File(System.getProperty("user.dir")+"\\"+arch);
                            if(!f.exists()){
                                f.createNewFile();
                            }
                            try(FileOutputStream fos=new FileOutputStream(f)){
                                DataInputStream dis=new DataInputStream(con.getInputStream());
                                long size=dis.readLong();
                                System.out.println(size);  // Aqui si se manda 2 o 3 veces el mismo archivo recibe 7310314640786158368
                                for(long l=0;l<size;l++){
                                    fos.write(dis.readByte());
                                }
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }else{
                            System.out.println("Archivo no encontrado");
                        }
                    }
                }
            }while(!opcion.equals("3"));
            writer.writeBytes("salir"+"\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}