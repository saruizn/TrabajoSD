
import modelos.*;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        File f=new File("C:\\Users\\samu2\\Desktop\\prueba.xml");
        ListaUsuarios lu=new ListaUsuarios(f);
        Usuario u=new Usuario("Juan el bot","juanito@xd.com","9876");
        lu.anadirUsuario(u);
    }
}