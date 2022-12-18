package modelos;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String nombre;
    private String email;
    private String contrasena;
    private boolean enLinea;

    private List<Archivo> archivos;

    public String getNombre() {return this.nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getEmail() {return this.email;}

    public void setEmail(String email) {this.email = email;}

    public String getContrasena() {return this.contrasena;}

    public void setContrasena(String contrasena) {this.contrasena = contrasena;}

    public boolean getLinea(){return this.enLinea;}

    public void setLinea(boolean enLinea){this.enLinea=enLinea;}
    public Usuario(String nombre, String email, String contrasena){
        this.contrasena=contrasena;
        this.enLinea=false;
        this.email=email;
        this.nombre=nombre;
        this.archivos=java.util.Collections.synchronizedList(new ArrayList<Archivo>());
    }

    public boolean equals(Object o) {
        Usuario u=(Usuario)o;
        return this.nombre.equals(u.nombre);
    }

    public Archivo getByPath(String path){
        for(Archivo a:this.archivos){
            if(a.getPath().equals(path)) return a;
        }
        return null;
    }

    public List<Archivo> getArchivosCompartidos(String user){
        List<Archivo> l=new ArrayList<>();
        for(Archivo a:this.archivos){
            if(a.tienePermisos(user)) l.add(a);
        }
        return l;
    }
}
