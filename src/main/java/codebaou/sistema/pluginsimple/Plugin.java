package codebaou.sistema.pluginsimple;

import codebaou.sistema.pluginsimple.ExceptionPlugins;
import codebaou.interfaces.I_Plugin;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;//https://docs.oracle.com/en/java/javase/18/docs/api/java.xml/javax/xml/xpath/package-summary.html
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * Clase Plugin
 * Esta clase representa a un plugin, un plugin es una carpeta que contiene como mínimo un fichero .jar. Se le pasa la ruta absoluta de la carpeta
 * del plugin a partir de esta ruta la clase Plugin realiza de forma automatica las siguientes funciones:
 * <pre>
 *      - Obtiene y guarda las siguientes propiedades:
 *          .   ruta absoluta del Jar
 *          .   URI del Jar
 *          .   URL del Jar
 * 
 *      - Lee y guarda como propiedades los siguientes attributos del pom.xml del Jar.
 *          .   exec.mainClass (classpath)
 *          .   modelVersion
 *          .   groupId
 *          .   artifactId
 *          .   version
 *      - 
 * </pre>
 * @see Plugin(String path_absolute_folder)
 * @see codebaou.sistema.pluginsimple.Plugins
 */
public class Plugin {

    final String PATH_ABSOLUTE_FOLDER;//Folder path
    //path jar
    public String path_absolute_Jar;
    public URI uri_jar;
    public URL url_jar;
    //xml
    private File tempPomXML;
    public String path_absolute_PomXML;
    //propiedades
    public String nombre;
    public String modelVersion;
    public String groupId;
    public String artifactId;
    public String version;
    //classpath  (pluginnuevo.nombre) - nombre binario 
    public String classpath;
    //metodos I_Plugin 
    public I_Plugin PLUGIN;
    private Constructor<? extends I_Plugin> constructor;
    private Class<?> clazz;
    /** Constructor 
    * @param String  ruta absoluta de la carpeta del plugin
    */
    public Plugin(String path_absolute_folder) {
        
        this.PATH_ABSOLUTE_FOLDER = Set_Sanalize_path( path_absolute_folder );
        this.PLUGIN               = null;
        this.constructor          = null;
        this.clazz                = null;
        this.tempPomXML           = null;
        this.ResuelvePaths();
        this.Carga_XMLPOM();
        this.Lee_Classpath_Pom();
    }
    
     /** _new Instance_ *****************************************************
      * Crea una nueva instancia de Class<? extends I_Plugin> que se almacenara en this.methods
      */
    public void NewInstance() throws ExceptionPlugins{
        
        if(this.clazz != null){
            
            try {
                this.constructor = (Constructor<? extends I_Plugin> ) this.clazz.getConstructor();
                this.PLUGIN      = this.constructor.newInstance();
                
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    /** _SETTER_ Class I_Plugin ************************************************
     * Se le pasa un Class<? extends I_Plugin> y lo guarda como propiedad
     */
    public void SetClazz(Class<?> classplu){ 
        this.clazz = classplu;
    }
 
    /** _GETTER_ - File xml ***************************************************
    * Devuelve el fichero pom.xml de tipo File
    * @param String 
    * @return {@link java.io.File}
    */
    private File GetFilePomXML(String path) {
        File f = new File(path);
        return f;
    }

    /**_GETTER_  - File Jar ***************************************************
    * Retorna un objeto File que representa el jar de este plugin
    * @return File {@link java.io.File}
    */
    private File Get_File_Jar() {
        File jar = new File(this.path_absolute_Jar);
        return jar;
    }

    /** _GETTER_  - JARFILE ***************************************************
    * Retorna un objeto JarFile que representa a este plugin
    * @return JarFile
    */
    private JarFile Get_JarFile() {

        JarFile jarfile = null;
        
        try {
            jarfile = new JarFile(Get_File_Jar());
        } catch (IOException ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jarfile;
    }
    
    /** SANALIZE PATH *********************************************************
    * reemplaza '\' y '/'  por 'File.separator' para utilizar el separador del sistema de archivos
    * @param String 
    * @return String
    */
    private String Set_Sanalize_path(String path){
        
        String sano = path;
        sano        = sano.replace("/",File.separator);
        sano        = sano.replace("\\",File.separator);
        return sano;
    }
    
    /** LEE CLASS PATH*********************************************************
    * Lee el fichero info.xml y llena las propiedades classpath, nombre,
    * version, descripcion de este objecto
    * @return void
    */
    private void Lee_Classpath_Pom() {
        
        if(this.tempPomXML == null){ this.Carga_XMLPOM(); }
            
        try {
            DocumentBuilderFactory factoryXML   = DocumentBuilderFactory.newInstance();//Cramos una instancia builder
            DocumentBuilder builder             = factoryXML.newDocumentBuilder();// Objecto que permite leer archivo xml
            Document documento                  = builder.parse(this.tempPomXML);//Le pasamos archivo xml (File)
            documento.getDocumentElement().normalize(); //Ordena los elmentos para una lectura mas rapida (Mejora el rendimiento)
            //Procesador xml  XPath
            XPath xpath                         = XPathFactory.newInstance().newXPath();
            Node classpathNode                  = (Node) xpath.evaluate("/project/properties/exec.mainClass", documento , XPathConstants.NODE);
            this.classpath                      = classpathNode.getFirstChild().getTextContent();
            Node modelVersionNode               = (Node) xpath.evaluate("/project/modelVersion", documento, XPathConstants.NODE);
            this.modelVersion                   = modelVersionNode.getFirstChild().getTextContent();
            Node groupIdNode                    = (Node) xpath.evaluate("/project/groupId", documento, XPathConstants.NODE);
            this.groupId                        = groupIdNode.getFirstChild().getTextContent();
            Node artifactIdNode                 = (Node) xpath.evaluate("/project/artifactId", documento, XPathConstants.NODE);
            this.artifactId                     = artifactIdNode.getFirstChild().getTextContent();
            Node versionNode                    = (Node) xpath.evaluate("/project/version", documento, XPathConstants.NODE);
            this.version                        = versionNode.getFirstChild().getTextContent();

        } catch (SAXException ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    /** RESUELVE PATH *********************************************************
    * Busca en el fichero Jar del plugin en la carpeta del plugin (this.PATH_ABSOLUTE_FOLDER)
    * y crea :
    *    - ruta absoluta del jar (this.path_absolute_Jar)
    *    - ruta URI del Jar (this.uri_jar ) 
    *    - ruta URL del Jar (this.url_jar)
    * @return void
    */
    private void ResuelvePaths() {

        this.path_absolute_Jar  = null;
        File pluginFolder       = new File(this.PATH_ABSOLUTE_FOLDER);
        //ABSOLUTE PATH JAR URI Y URL
        for (File f : pluginFolder.listFiles()) {
            
            if (f.isDirectory() == false && f.getName().contains(".jar")) {
                
                this.path_absolute_Jar = f.getAbsolutePath();
                this.uri_jar           = f.toURI();
                
                try {
                    this.url_jar = f.toURI().toURL();
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /** CARGA XMLPOM **********************************************************
    * Lee el flujo del pom.xml dentro del jar y escribe el flujo en un nuevo File objecto y lo guarda en this.tempPomXML
    * @return void
    */
    private void Carga_XMLPOM() {
        
        JarFile filejar = this.Get_JarFile();
        InputStream is  = null;

        for ( Enumeration<JarEntry> je = filejar.entries(); je.hasMoreElements(); ) {

            JarEntry entry = je.nextElement();
            //Se obtienen los byte[] del pom.xml del jar y se escribe en un nuevo fichero temporal tipo File
            if ( entry.getRealName().contains( "pom.xml" ) ){
                try {
                    //Lectura
                    is                   = filejar.getInputStream( entry );
                    byte[] b             = is.readAllBytes();
                    //Escritura
                    File tempf           = File.createTempFile( "javaselectPomTemp", ".xml" );//almacena la informacion obtenida como byte[] del jar
                    FileOutputStream fos = new FileOutputStream( tempf );
                    fos.write(b);
                    this.tempPomXML      = tempf; 
                    
                } catch ( IOException ex ) {
                    Logger.getLogger( Plugin.class.getName() ).log( Level.SEVERE, null, ex );
                } finally {
                    try {
                        is.close();
                    } catch ( IOException ex ) {
                        Logger.getLogger( Plugin.class.getName() ).log( Level.SEVERE, null, ex );
                    }
                }
            }
        }
    }

    
}
