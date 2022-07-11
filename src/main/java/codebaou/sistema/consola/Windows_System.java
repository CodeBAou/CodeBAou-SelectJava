package codebaou.sistema.consola;
import java.awt.List;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import java.lang.Process;
import java.lang.Runtime;
import java.lang.ProcessBuilder;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase proporciona las funciones que interacturan con el sistema
 * operativo windows
 *
 * @author CodeBAou
 */
public class Windows_System {
    

    /**
     * Busca en la carpeta \JAVAS del programa y crea una lista con los nombre
     * de las carpeta de java
     *
     * @return String[]
     */
    public static String[] Get_ListaNombre_Java_Disponibles_JRE() {
        File f = new File("./JAVAS");
        return f.list();
    }
    
    /** Devuelve un array con los nombre y su path de todas las versiones
    *   java que estan instaladas en la carpeta JAVAS 
    *   @return Object[]   
    *       Object[ String[ clave, valor ] ]
    *       clave - nombre jar del jdk jre de java
    *       valor - path del jdk jre para configuracion
    */
    public static Object[] Get_NombrePath_All_Java_Disponibles(){
        
        Object[] resultado = new Object[Get_ListaNombre_Java_Disponibles_JRE().length];
        File carpeta       = new File("./JAVAS");
        File[] javas       = carpeta.listFiles();
        
        for( int i=0; i<javas.length; i++ )
        {
            resultado[i] = new String[]{ javas[i].getName(), javas[i].getAbsolutePath() };
        }
        
        return resultado;
    }
    
    /** Se le pasa el nombre completo de una version de java disponible ( carpeta './JAVAS' ) y 
    * y devuelve su ruta absoluta, si no encuetra la version java devuelve null
    * @retur String o null
    */
    public static String Get_JAVAPATH(String nombre){
        
        File carpeta = new File("./JAVAS");
        File[] javas = carpeta.listFiles();
        
        for( int i=0; i<javas.length; i++ )
        {
           if( javas[i].getName().toLowerCase().equals( nombre.toLowerCase() ) ){
               return javas[i].getAbsolutePath();
           }
        }
        return null;
    }
    
    /** 
     * Retorna la version java del sistema 
     * @return String
    */
    public static ArrayList<String> Get_Version_Java_System(){
        return Filtro_Res_terminal ( Set_Comando_Terminal( new String[]{ "java --version"} ) );
    }
    
    /** Obtiene el valor jarFile 
    *  @return String
    */
    public static String Get_Info_JarFile(){
        
        String res              = "";
        ArrayList<String> valor = Set_Comando_Terminal( new String[]{ "ftype jarfile"} );
        
        if(valor.get(0).toLowerCase().contains("jarfile"))
        {
            res = valor.get(0);
        }
        else if( valor.size() > 1 )
        {
            for( int i=1; i<valor.size(); i++ )
            {
                if( valor.get(i).toLowerCase().contains("jarfile") )
                {
                    res = valor.get(i);
                }
            }
        }
        
        return res;
    }
    
    /** Otiene assoc del sistema y filtra para obtener solo el valor .jar 
    * @return String
    */
    public static String Get_Info_Assoc(){
        
        String res = "";
        ArrayList<String> valores = Set_Comando_Terminal( new String[]{ "assoc"} );
        
        if(valores.size()>0)
        {
            for( int i=0; i<valores.size(); i++ )
            {
                if( valores.get(i).toLowerCase().contains(".jar") )
                {
                    res = valores.get(i);
                    break;
                }
            }
        }else
        {
            res = "Assoc .jar = no configurado";
        }

        return res;
    }
    
    /** Descarta algunas respuestas de terminal */
    public static ArrayList<String> Filtro_Res_terminal( ArrayList<String> res ){
        
        ArrayList<String> filtrado = new ArrayList();
        
        for( int i=2; i<res.size(); i++ )
        {
            if
                    
            ( 
                res.get(i).toLowerCase().contains(">") == false
                &&
                res.get(i).equals("") == false
                && 
                res.get(i).toLowerCase().contains("microsoft windows [" ) == false
                &&
                res.get(i).toLowerCase().contains("Microsoft Windows") == false
            )
                
            {
                String aux = res.get(i).replace( '"'+";", "" );
                filtrado.add( res.get(i) );
            }
        }
        
        return filtrado;
    }
    
    /** 
    * Crea un nuevo proceso con los argumentos que se le pase como parametro guarda la respuestas (lineas)
    * en un ArrayList y mata el proceso
    * @return ArrayList<String> respuestas
    */
    public static ArrayList<String> Set_Comando_Terminal( String[] COMANDOS ){
        
        ArrayList<String> respuestas = new ArrayList();
        Process process              = null;
   
        try {

           process  = Runtime.getRuntime().exec(new String[]{"cmd"});

            //Escritura
            /*
            OutputStream out = process.getOutputStream();
            out.write( "dir".getBytes() );
            out.flush();
            out.close();
            */
            
            /*Bucle por cada comando del array comandos pasado por parametro
             se escribe el comando y se guarda las respuesta en respuestas ArrayList
            */
            for( int i=0; i<COMANDOS.length; i++ )
            {
                PrintWriter stdin          = new PrintWriter( process.getOutputStream() );
                stdin.println(COMANDOS[i]);
                stdin.flush();
                stdin.close();

                //Lectura
                InputStream is             = process.getInputStream();
                InputStreamReader isr      = new InputStreamReader(is);
                BufferedReader br          = new BufferedReader(isr);

                InputStream iserror        = process.getErrorStream();
                InputStreamReader isrerror = new InputStreamReader(is);
                BufferedReader brError     = new BufferedReader(isr);

                String res            = "";

                while( (res = br.readLine() ) != null )
                {
                    String aux = res;
                    if( res.equals("") == false )
                    {
                        respuestas.add(res.replace("“;",""));
                    }
                   
                }

                while( (res = brError.readLine() ) !=null )
                {
                    String aux = res;
                    respuestas.add(res);
                }
            }
            
            return respuestas;
            
        } catch (IOException ex) {
            
            Logger.getLogger( Windows_System.class.getName()).log(Level.SEVERE, null, ex );  
            
            if( process.isAlive() )
            {
                process.destroyForcibly();
            }
        }
        
        if(process.isAlive())
        {   
            process.destroy();
        }
        
        return respuestas;
    }
    
    /** Configura las variables JAVA_HOME - JRE_HOME y path 
    * @return Object[] = { String msg, int error } - 0 no hay errores
    */
    public static Object[] Configurar_Variables_Entorno( String path ){
        
        Object[] res                 = new Object[2];
        res[0]                       = "";
        // crear/modificar variable JAVA_HOME ,JRE_HOME y path:
        final String java_Home_c     = "setx /M JAVA_HOME "; // '"' RUTA '"'
        final String java_JRE        = "setx /M JRE_HOME "; // '"' RUTA '"'
        final String java_path       = "setx /M path ";  // '"' %JRE_BIN%\bin ; %JAVA_HOME%\bin; '"'
        
        //Variable de entorno path
        ArrayList<String> paths      = Filtro_Res_terminal( Set_Comando_Terminal( new String[]{"echo %path%"} ) );
        String[] valoresPaths        = paths.get(0).split(";");
        ArrayList<String> pathsNuevo = new ArrayList();
        
        //Se añaden rutas que no pertenezcan a java en el array
        for( int i=0; i<valoresPaths.length ;i++ )
        {
           if 
            ( 
                valoresPaths[i].toLowerCase().contains("jre") == false 
                &&
                valoresPaths[i].toLowerCase().contains("jdk") == false 
                &&
                valoresPaths[i].toLowerCase().contains("java_home") == false 
                &&
                valoresPaths[i].toLowerCase().contains("jre_home") == false
            )
            { //Se guardan todas las lineas del terminal que no pertenezca a configuracion de java
                if( valoresPaths[i] != "" && valoresPaths[i] != null &&  valoresPaths[i].equals( '"') == false && valoresPaths[i].length() > 1 ){      
                    pathsNuevo.add( valoresPaths[i] );
                }
            }
        }
        
        /** MODIFICAMOS LAS VARIABLES DE ENTORNO <JAVA_JRE> O <JAVA_HOME> DEPENDE SI EL PARAMETRO path
        * CONTIENE UNA JDK O JRE EN LA RUTA  Y SE CREA LA RUTA PARA LA VARIABLE DE ENTORNO path */     
        if( path.toLowerCase().contains( "jre" ) )              //JRE_HOME
        {
            String varEJRE        = "setx /M JRE_HOME " + '"' + path + '"';
            //Ejecucion comando ( variable JAVA_JRE )
            ArrayList<String> aux = Filtro_Res_terminal( Set_Comando_Terminal( new String[]{ varEJRE } ) ) ;
            String auxresult      =  "No se ha podido modificar la variable JRE_HOME";
            
            for(int i=0;i<aux.size();i++)
            {
                if( aux.get(i).contains("CORRECTO") )
                {
                    auxresult = "Varaible JRE_HOME a sido modificada.";
                    break;
                }
            }

            res[0]        += auxresult;
            
        }
        
        else if( path.toLowerCase().contains( "jdk" ) )         //JAVA_HOME
            
        {
            String varEJDK        = "setx /M JAVA_HOME "+ '"' + path + '"';
            // Ejecucion comando ( variable JAVA_HOME )
            ArrayList<String> aux = Filtro_Res_terminal(  Set_Comando_Terminal( new String[]{ varEJDK } )  );
            String auxResult      = "No se ha podido modificar la variable JAVA_HOME";
            
            for(int i=0; i<aux.size(); i++ )
            {
                if( aux.get(i).contains("CORRECTO") )
                {
                    auxResult = "Se ha modificado la variable JAVA_HOME";
                }
            }
            res[0]        += auxResult;
        }
        
        
        //Se crea el comando para configurar la variable path
        String comando = "setx /M path " + '"';
        
        for( int i=0 ; i < pathsNuevo.size() ; i++ )
        {
            comando += pathsNuevo.get(i)+";";    
        }
        
        comando += path+"\\bin;";  //Path de java
        comando += '"';
        
        //Ejecucion comando
        ArrayList<String> respuestaComando = Filtro_Res_terminal( Set_Comando_Terminal( new String[]{ comando } ) ) ;
        String aux                         = " \n No se ha podido modificar la variable path";
                
        for( int i=0; i<respuestaComando.size(); i++ )
        {
            if( respuestaComando.get(i).contains( "CORRECTO" ) ) //Ejecucion comando ( variable path )
            {
                aux    = "\nSe ha modificado la variable path ";
                res[1] = 0;
                break;
            }
        }
        
        res[0]        += aux;
        String resAux  = ""+res[0];
        resAux.replace(";", "\n");
        res[0]         = resAux;
        resAux         = null;
        return res;
    }
    
    /** configuracion ftype jarFile = ruta 
    * para evitar errores, ejecutar esta funcion despues de configurar las variables de entorno
    * @return Object[]{ String msg, int err } err = 0 (No hay error)
    */
    public static Object[] Configurar_Jarfile( String javapath ){
       
        //Debug jarfile = version 18 siempre
        //Modifica el ftype jarfile
        Object[] res                 = new Object[2];  
        //String comando             = "ftype jarfile =" + '"' + variableEntornoaux + "\\bin\\javaw.exe" + '"' + " -jar " + '"' + "%1" + '"' + " %*";
        String comando2              = "ftype jarfile="+'\"'+javapath+"\\bin\\javaw.exe"+'"'+" -jar \"%1\" %*";
        ArrayList<String> respuestas = Filtro_Res_terminal( Set_Comando_Terminal( new String[]{ comando2 } ) ); 
        
        if( respuestas.get(0).split("=")[1].contains(javapath) ){
            res[0] = "Se a modifica jarfile";
        }else{
            res[0] = "jarfile no se ha modificado";
        }
        
        return res;
    }
    
    /** Assoc .jar = jarfile
    * @return Object[ String msg, int err]  err 0 = No hay error
    */
    public static Object[] Configurar_assoc(){         
        
        Object[] res                 = new Object[2];
        res[0]                       = "No se ha configurado jarfile"; //Revisar error -----------------------DEBUG
        res[1]                       = 1;       
        ArrayList<String> respuestas = Filtro_Res_terminal( Set_Comando_Terminal( new String[]{ "ASSOC .jar=jarfile" } ) );
        
        for( int i=0; i<respuestas.size(); i++ )
        {
            if( respuestas.get(i).toLowerCase().contains(".jar=jarfile") ){
                res[0] = "Se ha modificado assoc";
                res[1] = 0;
            }
        }      
        return res;
    }
    
    //Devuelve true si la aplicacion tiene permiso de administrador
    public static Boolean Get_permisoAdministrador(){
        
        boolean permiso = false;
        File f          = new File( "C:\\" + "javaSelectAux.cmd" );
        
        if( f.exists() == false ){ 
            try {
                if( f.createNewFile() ) {
                    System.out.println("Se creo el fichero");
                    permiso = true;
                    f.delete();
                }else{
                     System.out.println("No se ha podido crear el fichero ");
                     permiso = false;
                }
            } catch (IOException ex) {
                permiso = false;
            }
        }
        
       f = null;
        
        return permiso;
    }
}
