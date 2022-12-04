package modelos;

public class Usuario {
    private String nombre;
    private String email;
    private String contrasena;
    private boolean enLinea;

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
    }

}
