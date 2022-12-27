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
                opcion=sc.nextLine();
                switch(opcion){
                    case "1":{
                        writer.writeBytes("nombres"+"\n");
                        String line;
                        while(!(line=reader.readLine()).equals("fin")) System.out.println(line);
                    }
                }
            }while(!opcion.equals("2"));
            writer.writeBytes("salir"+"\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}