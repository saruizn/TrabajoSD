
import modelos.*;

public class Main {
    public static void main(String[] args) throws Exception {
        ListaUsuarios lu=new ListaUsuarios("C:\\Users\\samu2\\Desktop\\prueba.xml");
        Usuario u=new Usuario("Juan el bot","juanito@xd.com","9876");
        lu.anadirUsuario(u);
    }
}