package codebaou.sistema.pluginsimple;
import codebaou.interfaces.I_Plugin;
import codebaou.sistema.pluginsimple.Plugin;
import codebaou.sistema.pluginsimple.ExceptionPlugins;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta Clase proporciona metodos para cargar plugins en una aplicacion. 
 * @see Plugins()
 * @see Plugins(Plugin plugin)
 * @see Plugins(Plugin[] plugins)
 * @author: codebaou
 * @version: 1.0
 */
public class Plugins {

    public Vector<Plugin> plugins;
    private URLClassLoader classLoaderPlugins;

    /** @see #Constructor() */
    public Plugins() {
        this.plugins       = new Vector();
        classLoaderPlugins = null;
        this.CargaClases();
    }
    
    /** @see #Constructor(Plugin plugin) */
    public Plugins(Plugin plugin) {
        this.plugins       = new Vector();
        this.plugins.add( plugin );
        classLoaderPlugins = null;
        this.CargaClases();
    }
    
    /** @see #Constructor(Plugin[] plugins) */
    public Plugins(Plugin[] pluginsARR) {
       this.plugins       = new Vector();
       for( int i=0; i<pluginsARR.length;i++){
           this.plugins.add(pluginsARR[i]);
       }
        classLoaderPlugins = null;
        this.CargaClases();
    }
 
    /* CARGA CLASES ***********************************************************
    * Carga las URL[] en la clase padre de URLClassLoader y inserta el objecto class<?> como propiedad
    * dentro de cada objecto Plugin
    * @return Boolean , true si se realizo la carga correctamente
    *@see codebaou.sistema.pluginsimple.Plugin
    */ 
    private void CargaClases() {
        
        I_Plugin pluginInstance = null; 
        URL[] urlplu            = this.Get_Urls_Plugins();
        this.classLoaderPlugins = new URLClassLoader( urlplu );
        
        //Recorremos los plugins y los cargamos
        for(Enumeration<Plugin> p = plugins.elements() ; p.hasMoreElements();){
            CrearCargarClazzInPlugin( p.nextElement() ); 
        }
    }
    
    /**
     * Carga el plugin en el cargador de clases y guarda un objecto Class<?> dentro del objeto plugin
     */
    private void CrearCargarClazzInPlugin(Plugin plugin){
        
        try {
            
            Class<?> clazzplu                           = classLoaderPlugins.loadClass( plugin.classpath);
            Constructor<? extends I_Plugin> constructor = (Constructor<? extends I_Plugin>) clazzplu.getConstructor();
            plugin.SetClazz(clazzplu);
 
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Plugins.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Plugins.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Plugins.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Plugins.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** SET PLUGIN ************************************************************
    * Añade un nuevo Plugin al vector (Vector<Plugin> plugins) de objectos Plugin
    * @param Plugin plugin
    */
    public void Set_Plugin( Plugin plugin ){
        this.plugins.add(  plugin );
    }
    
    /* _GETTER_  - GET_PLUGIN *************************************************
    * Retorna un objecto Plugin a partir del nombre plugin.artifactId
    * @param String nombre del plugin
    * @return Vector
    * @see java.util.Vector
    */
    public Plugin Get_Plugin( String nombre ) {
       
       Plugin plugin = null;
       
       for( Enumeration<Plugin> p = this.plugins.elements() ; p.hasMoreElements(); ){
           plugin = p.nextElement();
           if( plugin.artifactId.equals( nombre.toLowerCase() ) ){
               return plugin;
           }
       }
       
       return plugin;
    }
    
    /** Devuelve un array de plugin[] con los plugins cargados
    * @Return Plugin[]
    */
    public Plugin[] Get_PluginArr(){
        
        Plugin[] pluginsResult = new Plugin[plugins.size()];
        int i                  = 0;
        
        for(Enumeration<Plugin> p = plugins.elements(); p.hasMoreElements(); ){
            pluginsResult[i] = p.nextElement();
            i++;
        }
        
        return pluginsResult;
    }
    
    /** GET URL PLUGINS - *****************************************************
    * Devuelve un URL[] de todos los objectos plugin guardados en la propiedad this.plugins
    */
    public URL[] Get_Urls_Plugins(){
        
       URL[] urls = new URL[]{};  
       
       if( this.plugins.isEmpty() ){
           
           return urls;
           
       }else{
           urls = new URL[this.plugins.size()];
           int i = 0;
           
           for( Enumeration<Plugin> p = this.plugins.elements(); p.hasMoreElements(); ){
               urls[i] = p.nextElement().url_jar;
           }
           
       }
       return urls;
    }
   
    /** BUSCA PLUGINS ********************************************************
    * Devuelve un Vector<String> con las rutas absolutas carpetas de Plugin ( Contienen los .jar que corresponden al plugin ).
    * Los Jar deben implementar la interfaz I_Plugins
    * @param String ,ruta absoluta de la carpeta donde se realizara la busqueda
    * @return Vector<String> rutas absolutas de los Jars validos
    * @see java.util.Vector
    */
    public static Vector<String> Busca_Plugins( String carpetaPlugins ) throws ExceptionPlugins {
       
       Vector<String> paths_carpeta_plugin = new Vector();
       File carpeta                        = new File( carpetaPlugins );

       if( carpeta.isDirectory() ){
           
            for( File carpetaPlugin: carpeta.listFiles() ){
                if(carpetaPlugin.isDirectory()){
                    paths_carpeta_plugin.add(carpetaPlugin.getAbsolutePath());
                }else{
                    throw new ExceptionPlugins( ExceptionPlugins.No_ESTRUCT_D );
                }
            }
            
       } else{
          throw new ExceptionPlugins( ExceptionPlugins.No_ESTRUCT_D );
       } 
       
       return paths_carpeta_plugin;
    }
}
