package codebaou.sistema.pluginsimple;
import java.lang.Exception;
/**
 * Excepcion que tenga que ver con los plugins y su manejo
 * @author CodeBAou
 */
public class ExceptionPlugins extends Exception{
        
        public static final String NO_PLUGIN        = "El jar que se ha indicado no es valido. El jar debe implementar la interfaz I_Plugin.";
        public static final String NO_FOLDER        = "El path debe hacer referencia a una carpeta.";
        public static final String No_CONSTRUCTOR   = "Se ha intentado instanciar una clase que no se ha cargado.";
        public static final String No_JAR           = "Se ha detectado un fichero en la carpeta plugin diferente aun .jar .";
        public static final String No_ESTRUCT_D     = "La estructura de carpetas en \\plugins esta mal formada, revise la documentacion"; 
        public static final String No_File_Ext      = "Se ha detectado un fichero en la carpeta \\plugins con formato no conocido";
        
        public ExceptionPlugins (){
            super();
        }
        
        public ExceptionPlugins(String mensaje){
            super(mensaje);
        }
}
