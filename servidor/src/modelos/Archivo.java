package modelos;

import java.io.File;
import java.util.List;

public class Archivo {
    private File file;
    private List<String> usuariosCompartido;

    private boolean publico=false;

    private int descargas=0;

    public Archivo(File file, List<String> usuariosCompartido){
        this.file=file;
        this.usuariosCompartido=usuariosCompartido;
    }

    public String getPath(){
        String path=this.file.getAbsolutePath();
        return path.substring(path.indexOf("\\recursos\\")+10);
    }

    public boolean tienePermisos(String user){
        if(this.publico) return true;
        if(!this.usuariosCompartido.isEmpty()){
            for(String s: this.usuariosCompartido){
                if(s.equals(user)){
                    return true;
                }
            }
        }
        return false;
    }

    public void anadirUsuario(String user){
        this.usuariosCompartido.add(user);
    }

    public void descarga(){
        this.descargas++;
    }

    public int getDescargas(){
        return this.descargas;
    }

    public void setPublico(boolean estado){
        this.publico=estado;
    }

    public boolean esPublico(){
        return this.publico;
    }

    public boolean borrar(){
        return this.file.delete();
    }

    public boolean equals(Object o){
        Archivo a=(Archivo)o;
        return this.file.getAbsolutePath().equals(a.file.getAbsolutePath());
    }

}
