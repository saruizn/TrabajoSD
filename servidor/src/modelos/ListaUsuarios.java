package modelos;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ListaUsuarios {
    private ArrayList<Usuario> usuarios;
    private String path;

    public ListaUsuarios(String path) throws Exception {
        this.path=path;
        this.usuarios=new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(this.path)); //path
        Element raiz=doc.getDocumentElement();
        NodeList nodos=raiz.getElementsByTagName("user");
        int tam=nodos.getLength();
        for(int i=0;i<tam;i++) {
            Element e=(Element)nodos.item(i);
            Usuario u=new Usuario(e.getAttribute("nombre"),e.getAttribute("email"),e.getAttribute("contrasena"));
            System.out.println(e.getAttribute("nombre")+"|"+e.getAttribute("email")+"|"+e.getAttribute("contrasena"));
            this.usuarios.add(u);
        }
    }

    public boolean anadirUsuario(Usuario u){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(this.path));
            Element raiz=doc.getDocumentElement();
            Element e=doc.createElement("user");
            e.setAttribute("nombre",u.getNombre());
            e.setAttribute("email",u.getEmail());
            e.setAttribute("contrasena",u.getContrasena());
            raiz.appendChild(e);
            TransformerFactory transformerFactory= TransformerFactory.newInstance();
            Transformer transformer= transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source= new DOMSource(doc);
            StreamResult result= new StreamResult(new File(path));
            transformer.transform(source, result);
            this.usuarios.add(u);
            return true;
        } catch(Exception ex) {return false;}
    }

    public boolean actualizarSesion(String email,String contrasena,boolean estado){
        for(Usuario u:this.usuarios){
            if(u.getEmail().equals(email) && u.getContrasena().equals(contrasena)){
                if (u.getLinea() != estado) {
                    u.setLinea(estado);
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
    }
}
