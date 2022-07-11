package codebaou.selectjava;
import codebaou.interfaces.I_Plugin;
import codebaou.sistema.pluginsimple.ExceptionPlugins;
import codebaou.sistema.pluginsimple.Plugins;
import codebaou.sistema.pluginsimple.Plugin;
import codebaou.sistema.consola.Windows_System;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/** 
* Esta aplicacion se basa en modificar la configuracion del jdk o jre en el sistema o
* en la configuracion de otras aplicaciones, para esto se incluye una carpeta donde se
* almacenan las distintas versiones java que necesite el usuario para selecionar la version desde
* la interfaz y ejecutar la configuracion en el sistema o a traves de un plugin de forma grafica. 
* 
* Funcionalidades Implementadas:
*   Funcionalidad configuracion java en el sistema:
*       windows
*   Funcionalidad para incluir plugins que contengan los pasos para configurar otras aplicaciones, por ejemplo netbeans.
*   
* Funcionalidades que se implementaran:
*   Funcionalidad para la instalacion de la maquina virtual de java en Linux. 
*/
public class Init {
    
    final static String SISTEMA = System.getProperty( "os.name" ).toLowerCase();
  
    public static Plugins plugins;
    
    public static String dataJDKJRE; //Seleccion Version Java 
    public static String dataAccion; //Que se ejecuta (configuracion sistema, Plugin) ?  values [plugin.artifactId o "system"]
    public static String dataError; // null ningun error o mensaje de error para mostrar
    
    public  final String VALUESYSTEM           = "system";
    private final static String separator      = System.getProperty("file.separator");
    private final static String carpetaPlugins = System.getProperty("user.dir") + separator + "plugins" + separator;

    public static void main(String[] args) {
        
        if( Windows_System.Get_permisoAdministrador() ){
            //CON PERMISOS DE ADMINISTRADOR
            Init init          = new Init();
            Interfaz interfaz  = new Interfaz( init );

            dataJDKJRE         = null;
            dataAccion         = null;
            dataError          = null;

            File javas = new File("javas");
            File pluginsf = new File("plugins");

            if( javas.exists() == false ){
                javas.mkdir();
            }

            if( pluginsf.exists() == false ){
                pluginsf.mkdir();
            }

            javas    = null;
            pluginsf = null;

            Plugin[] pluginAll = init.Get_PluginsArr(); //Carga de Plugins

            //Carga de plugins
            if( pluginAll != null )
            {
                plugins = new Plugins(pluginAll);
                interfaz.Set_Buttons_Lista_Plugins( plugins.Get_PluginArr() );
                interfaz.Reload_Lista_Plugins();  
            }
            else
            {
                plugins = new Plugins();
            }  

            Execute_Get_Info( init, interfaz );

            interfaz.setVisible( true );
        }
        else
        {
           
            //SIN PERMISOS DE ADMINISTRADOR
            int input = JOptionPane.showOptionDialog(null, "Se necesitan permisos de administrador para que la aplicacion funcione", "Info ",JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            if(input == JOptionPane.OK_OPTION ){
                System.exit(0);//Fin Programa
            }
            
        }
    }
   
    //Se le pasa la version de java que se ha seleccionado y la accion que se debe ejecutar
    public void Set_Select_Accions( String JDKJRE, String ACCION ){
        dataJDKJRE = JDKJRE; //Seleccion Version Java 
        dataAccion = ACCION; //Que se ejecuta (configuracion sistema, Plugin) ?  values [plugin.artifactId o "system"]
    }
    /** */
    /** Devuelve un array  Plugin[] con los plugins (Jars) encontrados en la carpeta plugins del programa 
    * @return Plugin[]
    */
    private Plugin[] Get_PluginsArr(){
        
        Plugin[] pluginArr = null;
        
        try {   
            Vector<String> paths = Plugins.Busca_Plugins(carpetaPlugins);
            pluginArr            = new Plugin[paths.size()];
            
            if( paths != null ){
                
                int i = 0;
                
                for(Enumeration<String> path = paths.elements(); path.hasMoreElements(); ){
                    String pathStr           = path.nextElement();
                    pluginArr[i]             = new Plugin(pathStr);
                    i++;
                }
            }
           
        } catch (ExceptionPlugins ex) {
            Logger.getLogger(Init.class.getName()).log(Level.SEVERE, null, ex);
            return new Plugin[]{};
        }
     
        return pluginArr;
    }
   
    /** Llama a todas la funciones para obtener informacion sobre el sistema utilizando la clase
    * que corresponda al sistema operativo en ejecucion
    * @return void 
    */
    public static void Execute_Get_Info( Init init, Interfaz interfaz){
        
        /** Llama a la clase que corresponda al sistema operativo en ejecucion */
        if( init.SISTEMA.contains( "windows" ) )
        {
            SetInterfaz_InfoJava( interfaz );
            SetInterfaz_InfoAssocANDJarFile( interfaz ); 
        }
        else if( SISTEMA.contains( "linux" ) )
        {
               interfaz.Set_Alerta("Sin soporte para sistemas linux");
        }
        else if( SISTEMA.contains( "mac" ) )
        {
               interfaz.Set_Alerta("Sin soporte para sistemas mac");
        }
        else if( SISTEMA.contains( "free" ) )
        {
               interfaz.Set_Alerta("Sin soporte para sistemas freeBSD");
        }  
    }
    
    /** Ejecuta la accion configurada por el usuario llamando a la clase que 
     * que corresponda al sistema operativo en ejecucion
     * @return String - mensaje respuesta usuario (Estado)
     */
    public String Execute(){
       
        if( dataJDKJRE == null || dataAccion == null )
        {
           //Gestion de errores por falta de datos
           if( dataJDKJRE == null )
           {
               return " No se ha seleccionado un version de java \n";
           }
           else if( dataAccion == null )
           {
               return  " No se ha indicado la accion que se debe realizar \n";
           }
        }
        else
        {
            //Decidir que se ejecuta...
            if( dataAccion.toLowerCase().equals( "sistema" ) )
            {
                return "" + ExecSystemMethod()[0];
            }
            else
            {
                //Se llama a un plugin
                Plugin pluginAccion = plugins.Get_Plugin( dataAccion );
                int err = 0;

                if( pluginAccion == null )
                {
                    return "No se encontro el plugin";
                }
                else
                {
                    try 
                    {
                        pluginAccion.NewInstance();
                    } 
                    catch (ExceptionPlugins ex) 
                    {
                        return ex.getMessage();
                    }
                    return pluginAccion.PLUGIN.MensajeCode( ExecPluginMethod( pluginAccion ) );
                }
            } 
         
        }
        
        return "error no identificado";
    }

    /** Ejecuta el metodo correcto de un plugin teniendo en cuenta los soportes del plugin
    * y el sistema operativo en el que se ejecuta esta aplicacion
    * @return true si no se detectaron errores
    */
    private int ExecPluginMethod(Plugin plugin ){
        
        final String path    = codebaou.sistema.consola.Windows_System.Get_JAVAPATH( dataJDKJRE );
        
        try{
            int err = 0;
            plugin.NewInstance();
            
            /**
             * Determina el metodo del plugin correcto que se debe utilizar segun el sistema operativo
             * que se este usando
             */
            if( SISTEMA.contains( "windows" ) )
            {
                err = plugin.PLUGIN.mainWin( path );
                
            }
            else if( SISTEMA.contains( "linux" ) )
            {
                err = plugin.PLUGIN.mainLinux( path );
            }
            else if( SISTEMA.contains( "mac" ) )
            {
                err = plugin.PLUGIN.mainMac( path );
            }
            else if( SISTEMA.contains( "free" ) )
            {
                err = plugin.PLUGIN.mainFreeBSD( path );
            }
            else if( err == 0)
            {
                err = plugin.PLUGIN.mainAll( path );
            }
            
            return err;
            
        } catch (ExceptionPlugins ex) {
            return 0;
        }
    }
    
    /** Configura el jdk o jre en el sistema operativo 
    * @return Object[String msg, int error] 0 = no error
    */
    private static Object[] ExecSystemMethod(  ){
        
        Object[] res    = new Object[2];
        String javaPath = codebaou.sistema.consola.Windows_System.Get_JAVAPATH( dataJDKJRE ); //Object[ String[ nombreJava, absolutePath ] ]
        res[0]          = "\n" + Windows_System.Configurar_Variables_Entorno( javaPath )[0];
        res[0]         += "\n" + Windows_System.Configurar_Jarfile(javaPath)[0];
        res[0]         += "\n" + Windows_System.Configurar_assoc()[0];  
        res[1]          = 0;
        
        return res;
    }
    
    /** Obtener datos del sistema operativo y mostrarlo en el apartado de la interfaz
    * configuracion del sistema
    */
    
    public static int SetInterfaz_InfoJava( Interfaz interfaz ){
        
        interfaz.Limpia_Mensajes_Conf();
        interfaz.Set_MensajesUsuario_Conf( System.getProperty( "os.name" ) );
        ArrayList<String> jdkinfo = Windows_System.Get_Version_Java_System();
        
        if( jdkinfo.size() > 0 ){
            for( int i=0; i<jdkinfo.size(); i++ )
            {
                interfaz.Set_MensajesUsuario_Conf( jdkinfo.get(i) );
            }
        }else
        {
            interfaz.Set_MensajesUsuario_Conf( "No se detecto ninguna version de java en el equipo o java esta mal configurado" );
        }
 
        return 0;
    }
    
    /** Muestra los valores de assoc y jarfile en la interfaz */
    public static void SetInterfaz_InfoAssocANDJarFile( Interfaz interfaz ){
        
        interfaz.Set_MensajesUsuario_Conf("\n assoc_");
        interfaz.Set_MensajesUsuario_Conf( "" + Windows_System.Get_Info_Assoc().replace("\n","") );
       
        interfaz.Set_MensajesUsuario_Conf("\n jarfile_");
        interfaz.Set_MensajesUsuario_Conf( ""+Windows_System.Get_Info_JarFile().replace("jarfile=","").replace(" ","") );
    }
}
