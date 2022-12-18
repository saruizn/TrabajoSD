package modelos;

import java.io.File;
import java.util.List;

public class Archivo {
    private File file;
    private List<String> usuariosCompartido;

    public Archivo(File file, List<String> usuariosCompartido){
        this.file=file;
        this.usuariosCompartido=usuariosCompartido;
    }

    public String getPath(){
        String path=this.file.getAbsolutePath();
        return path.substring(path.indexOf("\\recursos\\"+1));
    }

    public boolean tienePermisos(String user){
        if(!usuariosCompartido.isEmpty()){
            for(String s: usuariosCompartido){
                if(s.equals(user)){
                    return true;
                }
            }
        }
        return false;
    }

}
