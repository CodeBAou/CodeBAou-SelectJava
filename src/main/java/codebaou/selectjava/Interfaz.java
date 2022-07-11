package codebaou.selectjava;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import static java.awt.Frame.ICONIFIED;
import java.awt.Graphics;
import java.awt.List;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import codebaou.sistema.consola.Windows_System;
import codebaou.sistema.pluginsimple.ExceptionPlugins;
import java.util.Objects;
import javax.swing.BorderFactory;
import static javax.swing.SwingConstants.CENTER;
import codebaou.sistema.pluginsimple.Plugin;
import FontsJavaSelect.Fonts;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/** @author CodeBAou */
public class Interfaz extends javax.swing.JFrame {   
    
    private boolean aplicacion_maximizada;
    private int pos_mouse_x;//Posicion Inicial raton x
    private int pos_mouse_y;//Posicion IniciaL raton y
    private int pos_frame_x; //Posicion Inicial Frame x
    private int pos_frame_y; // Posicion Iniial Frame y

    private ArrayList<String> Mensajes_Conf_Sistema;
    private ArrayList<String> Mensajes_Resultado;
    private ArrayList<JButton> listaBotonesPlugins;
    private Dimension resolucionPorDefecto;//Tamaño aplicacion cuando no esta en modo pantalla completa, el modo pantalla completa se hace de forma automática.
    //Actions
    // se guarda una referencia a la funcion o plugin que se debe ejecutar
    private String accion_seleccionada;
    //nombre del jdk que el usuario a selecionad
    private String jdkjre_seleccionado;
    private String jdkjre_aux;
    private String[] listaVersionesJava;
    
    private Plugin plugin;
    private Fonts fuente;
    private Init init;
    
    public Interfaz( Init INIT ) { 
        
        this.init                   = INIT;
        this.Mensajes_Conf_Sistema  = new ArrayList();
        this.Mensajes_Resultado     = new ArrayList();
        this.fuente                 = new Fonts();
        this.plugin                 = null;
        this.aplicacion_maximizada  = false;
        this.listaBotonesPlugins    = null;
        
        this.accion_seleccionada    = null;
        this.jdkjre_seleccionado    = null;

        this.setUndecorated(true);
        this.initComponents();
            
        this.Estilo_ScrollBar( jScrollPane2 );
        this.Estilo_ScrollBar( jScrollPane4 );
        this.Estilo_ScrollBar( jScrollPane5 );
        this.Estilo_ScrollBar( jScrollPane3 );
        
        //Rellenar informacion del sistema
        this.Reload_Lista_Java_Disponibles();  
        this.Reload_MensajesUsuario_Conf();
        this.Set_FontsJavaSelect();   
    }
    
    /** Alerta mensaje error  */
    public void Set_Alerta( String Alerta ){
        this.jLabel_alerta.setText( Alerta );
        this.revalidate();
        this.repaint();
    }
    
    /* Añade estilo de seleccion al boton pasado como parametro */
    public void Set_Marca_Seleccion_BTN(JButton btn){
        btn.setForeground(new Color(201,201,201));      
    }
    
    /* Añade una linea de texto en el apartado configuracion sistema */
    public void Set_MensajesUsuario_Conf(String Mensaje){
        if( Mensaje != null && Mensaje != "" ){
            this.Mensajes_Conf_Sistema.add( Mensaje );
        }
        this.Reload_MensajesUsuario_Conf();
    }
    
    public void SetVersionesJava( String[] lista ){
        this.listaVersionesJava = lista;
        this.Reload_Lista_Java_Disponibles(); 
    }
    
    public void SetVersionPrograma( String  version ){
        this.jLabel_VersionPrograma.setText( version );
    }
    
    public void Set_MensajesUsuario_Resultado( String MSG ){
        
        if( MSG != null && MSG != "" )
        {
            this.Mensajes_Resultado.add(MSG);
        }  
    }
    
    /** Limpia el panel de mensajes "configuracion de sistema*/
    public void Limpia_Mensajes_Conf()
    {
        this.Mensajes_Conf_Sistema = new ArrayList();
    }
    
    /* Pasar la accion determinada por el usuario al lanzador  */
    public void Callback_SetACCION( String VersionJDKJRE, String Accion ){
       
       this.jdkjre_seleccionado = VersionJDKJRE;
       this.accion_seleccionada = Accion;        
       this.init.Set_Select_Accions( this.jdkjre_seleccionado, this.accion_seleccionada );
       this.Reload_MensajesUsuario_lista_cambios();
    }

    /* Carga los nombres de la versiones java disponibles en versiones java disponibles */
    private void Reload_Lista_Java_Disponibles(  ){
        
        if( this.listaVersionesJava != null ){
            for( int i = 0; i<this.listaVersionesJava.length; i++ ){
                this.jPanel_Lista_Versiones_Java.add( Create_BTN_Lista_JavasVersions( this.listaVersionesJava[i] ) );
            }
        } else{   
            
            int input = JOptionPane.showOptionDialog(null, "No hay niguna version de java disponibles", "Info ",JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
            
        }
    }
    
    /* Carga los mensajes de Mensajes_Conf_Sistema en el apartado de la interfaz "Datos configuracion sistema" */
    private void Reload_MensajesUsuario_Conf(){
        
        String aux = "";
      
        for(int i=0;i<this.Mensajes_Conf_Sistema.size();i++){
           aux += this.Mensajes_Conf_Sistema.get(i) + "\n";
        }
        
        this.jTextArea_configuracionSistema.setText( aux );
    }
    
    /* Carga los mensajes de Mensajes_jTextPane_Lista_Cambios en el apartado de la interfaz de "Lista de cambios a realizar" */
    private void Reload_MensajesUsuario_lista_cambios(){
 
        String cadena = "";
        
        if( this.jdkjre_seleccionado != "" && this.jdkjre_seleccionado != null)
        {
            cadena += "  \n Nueva version Java: " + this.jdkjre_seleccionado + "\n \n";
        }
        
        if( this.accion_seleccionada != "" && this.accion_seleccionada != null)
        {
            cadena += " Se configurara : " + this.accion_seleccionada +"\n";
        }
        
        this.JTextPanel_ListaCambios.setText( cadena );
        cadena = null;
    } 
    
    /* Carga los mensajes de Mensajes_jTextPane_resultado en el apartado de la interfaz "Resultado Ejecucion" */
    public void Reload_MensajesUsuario_resultado(){
        
        String cadena = "";
        
        if( this.Mensajes_Resultado.isEmpty() == false )
        {
            for( int i= 0; i<this.Mensajes_Resultado.size() ; i++ )
            {
                cadena += "\n" + this.Mensajes_Resultado.get(i);
            }
        }
        
        this.jTextPane_Resultado.setText( cadena );
    }
   
    /** Construye un nuevo arbol de botones (Plugin) en la interfaz grafica, utiliza
    * la propiedad this.ListaBotonesPlugins para obtener los JButton que tiene que listar
    */
    public void Reload_Lista_Plugins(){
        
        this.jPanel_ListaPlugins.removeAll();
        
        if( this.listaBotonesPlugins != null ){
            for( int i=0; i<this.listaBotonesPlugins.size(); i++){
                this.jPanel_ListaPlugins.add( this.listaBotonesPlugins.get(i) );
            }
        }
        
        this.jPanel_ListaPlugins.revalidate();
        this.jPanel_ListaPlugins.repaint();
    }
   
    
    /* Devuelve un JButton con el nombre pasado como parametro 
    * @param String nombre version Java
    * @return JButton
    */
    private JButton Create_BTN_Lista_JavasVersions( String NOMBRE ){
        
        //TEXT es igual al nombre de la carpeta
        JButton btn = new JButton();
        btn.setText(NOMBRE);
        btn.setForeground(new Color(102,102,102));
        btn.setSize(200, 40);
        btn.setMinimumSize(new Dimension(200,50));
        btn.setMaximumSize(new Dimension(250,20));
        btn.setBackground(new Color(51,51,51));
        btn.setFocusPainted(false);
        btn.setBorder( BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(102,102,102)));
        btn.setHorizontalTextPosition(CENTER);
        btn.setFont(this.fuente.fOxygenMonoRegularFont.deriveFont(Font.PLAIN,11));
        
        btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {              
                Elimina_Marcas_Seleccion_botones();
                Set_Marca_Seleccion_BTN(btn);
                jdkjre_aux = btn.getText();   
            }
        });
        
        return btn;
    }
    
    /** 
    * Crea botones a partir de un array del objecto plugin (Plugin[]) y los guarda en la propiedad this.listaBotonesPlugins (ArrayList<JButton>)
    * @param Plugin
    * @return JButton 
    * @see codebaou.sistema.pluginsimple.Plugin
    */
    public void Set_Buttons_Lista_Plugins(Plugin[] plugins){
        
        Init init = this.init;
        
        for( int i=0; i<plugins.length; i++ ){
            
            JButton btn = new JButton();
            this.plugin = plugins[i];
            
            btn.setText( plugin.artifactId );
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setForeground( new Color(51,51,51) );
            btn.setSize(105, 16);
            btn.setMinimumSize( new Dimension(105,50) );
            btn.setMaximumSize( new Dimension(105,20) );
            btn.setBackground( new Color(102,102,102) );
            btn.setFocusPainted( false );
            btn.setBorder( BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(51,51,51)) );
            btn.setHorizontalTextPosition( CENTER );
            btn.setFont( this.fuente.fOxygenMonoRegularFont.deriveFont( Font.BOLD, 11 ) );
            
            btn.addActionListener( new java.awt.event.ActionListener() {
                public void actionPerformed( java.awt.event.ActionEvent evt  ) {                  
                    Elimina_Marcas_Seleccion_botones();
                    Set_Marca_Seleccion_BTN( btn );
                    accion_seleccionada = btn.getText();
                    init.Set_Select_Accions( jdkjre_seleccionado , accion_seleccionada );
                    Reload_MensajesUsuario_lista_cambios();
                }
            });

            if( this.listaBotonesPlugins == null ){   this.listaBotonesPlugins = new ArrayList(); }  
            this.listaBotonesPlugins.add( btn );
        }
        
    }

    /* FUNCIONES ESTILOS */
    //Quita los estilos de seleccion de todos los botones de la lista de versiones de java
    private void Elimina_Marcas_Seleccion_botones(){
        for(int i = 0;i<this.jPanel_Lista_Versiones_Java.getComponents().length;i++){
            this.jPanel_Lista_Versiones_Java.getComponent(i).setForeground(new Color(102,102,102));
        }
    }
 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Header = new javax.swing.JPanel();
        Header_title = new javax.swing.JPanel();
        SELECTJAVA = new javax.swing.JLabel();
        jLabel_VersionPrograma = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(80, 0), new java.awt.Dimension(32767, 0));
        Header_window = new javax.swing.JPanel();
        jButton_Minimiza = new javax.swing.JButton();
        jButton_maximiza = new javax.swing.JButton();
        jButton_close = new javax.swing.JButton();
        Body_head = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel_alerta = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(10, 32767));
        BTN_DO = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        jPanel_body = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btn_conf_añadir = new javax.swing.JButton();
        btn_conf_quitar = new javax.swing.JButton();
        btn_conf_limpiar = new javax.swing.JButton();
        jPanel_Lista_Versiones_Java = new javax.swing.JPanel();
        title_versionesJava = new javax.swing.JLabel();
        title_ListaCambios = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPane_Conf_Sistema = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        JTextPanel_ListaCambios = new javax.swing.JTextArea();
        title_ResultadoEjecucion = new javax.swing.JLabel();
        title_configuracionSistema = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btn_conf_sistema = new javax.swing.JButton();
        jPanel_ListaPlugins = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea_configuracionSistema = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane_Resultado = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(800, 700));
        setMinimumSize(new java.awt.Dimension(800, 700));
        setPreferredSize(new java.awt.Dimension(813, 700));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        Header.setBackground(new java.awt.Color(255, 255, 255));
        Header.setMaximumSize(new java.awt.Dimension(32767, 20));
        Header.setMinimumSize(new java.awt.Dimension(0, 20));
        Header.setPreferredSize(new java.awt.Dimension(813, 20));
        Header.setLayout(new javax.swing.BoxLayout(Header, javax.swing.BoxLayout.X_AXIS));

        Header_title.setBackground(new java.awt.Color(255, 255, 255));
        Header_title.setMaximumSize(new java.awt.Dimension(32767, 16));
        Header_title.setMinimumSize(new java.awt.Dimension(0, 16));
        Header_title.setOpaque(false);
        Header_title.setPreferredSize(new java.awt.Dimension(202, 17));
        Header_title.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));

        SELECTJAVA.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        SELECTJAVA.setForeground(new java.awt.Color(102, 102, 102));
        SELECTJAVA.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        SELECTJAVA.setText("SELECTJAVA");
        Header_title.add(SELECTJAVA);

        jLabel_VersionPrograma.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel_VersionPrograma.setForeground(new java.awt.Color(102, 102, 102));
        jLabel_VersionPrograma.setText(" ");
        jLabel_VersionPrograma.setMinimumSize(new java.awt.Dimension(30, 16));
        jLabel_VersionPrograma.setPreferredSize(new java.awt.Dimension(30, 20));
        Header_title.add(jLabel_VersionPrograma);
        Header_title.add(filler1);

        Header.add(Header_title);

        Header_window.setMaximumSize(new java.awt.Dimension(130, 32767));
        Header_window.setMinimumSize(new java.awt.Dimension(130, 22));
        Header_window.setOpaque(false);
        Header_window.setPreferredSize(new java.awt.Dimension(130, 22));
        Header_window.setLayout(new java.awt.GridLayout(1, 0));

        jButton_Minimiza.setBackground(new java.awt.Color(255, 255, 254));
        jButton_Minimiza.setText("-");
        jButton_Minimiza.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton_Minimiza.setFocusPainted(false);
        jButton_Minimiza.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_MinimizaMouseClicked(evt);
            }
        });
        Header_window.add(jButton_Minimiza);

        jButton_maximiza.setBackground(new java.awt.Color(255, 255, 254));
        jButton_maximiza.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jButton_maximiza.setText("o");
        jButton_maximiza.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton_maximiza.setEnabled(false);
        jButton_maximiza.setFocusPainted(false);
        Header_window.add(jButton_maximiza);

        jButton_close.setBackground(new java.awt.Color(255, 255, 254));
        jButton_close.setText("X");
        jButton_close.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton_close.setFocusPainted(false);
        jButton_close.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_closeMouseClicked(evt);
            }
        });
        jButton_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_closeActionPerformed(evt);
            }
        });
        Header_window.add(jButton_close);

        Header.add(Header_window);

        getContentPane().add(Header);

        Body_head.setBackground(new java.awt.Color(255, 255, 255));
        Body_head.setLayout(new javax.swing.BoxLayout(Body_head, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 20));
        jPanel2.setMinimumSize(new java.awt.Dimension(0, 20));
        jPanel2.setPreferredSize(new java.awt.Dimension(813, 20));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 801, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        Body_head.add(jPanel2);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanel1.setMinimumSize(new java.awt.Dimension(100, 30));
        jPanel1.setPreferredSize(new java.awt.Dimension(813, 30));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jPanel4.setBackground(new java.awt.Color(102, 102, 102));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
        jPanel4.setPreferredSize(new java.awt.Dimension(677, 30));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel_alerta.setForeground(new java.awt.Color(102, 0, 0));
        jLabel_alerta.setText(" ");
        jPanel4.add(jLabel_alerta);

        jPanel1.add(jPanel4);
        jPanel1.add(filler5);

        BTN_DO.setBackground(new java.awt.Color(102, 102, 102));
        BTN_DO.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        BTN_DO.setForeground(new java.awt.Color(51, 51, 51));
        BTN_DO.setText("DO");
        BTN_DO.setAlignmentX(0.5F);
        BTN_DO.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 102), 5, true));
        BTN_DO.setFocusable(false);
        BTN_DO.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BTN_DO.setMaximumSize(new java.awt.Dimension(50, 30));
        BTN_DO.setMinimumSize(new java.awt.Dimension(50, 30));
        BTN_DO.setPreferredSize(new java.awt.Dimension(80, 30));
        BTN_DO.setVerifyInputWhenFocusTarget(false);
        BTN_DO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTN_GO(evt);
            }
        });
        jPanel1.add(BTN_DO);
        jPanel1.add(filler6);

        Body_head.add(jPanel1);

        getContentPane().add(Body_head);

        jPanel_body.setBackground(new java.awt.Color(51, 51, 51));
        jPanel_body.setMinimumSize(new java.awt.Dimension(800, 0));

        jPanel5.setBackground(new java.awt.Color(102, 102, 102));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        btn_conf_añadir.setBackground(new java.awt.Color(51, 51, 51));
        btn_conf_añadir.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btn_conf_añadir.setForeground(new java.awt.Color(153, 153, 153));
        btn_conf_añadir.setText(">>");
        btn_conf_añadir.setToolTipText("");
        btn_conf_añadir.setBorder(null);
        btn_conf_añadir.setBorderPainted(false);
        btn_conf_añadir.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_conf_añadir.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        btn_conf_añadir.setDefaultCapable(false);
        btn_conf_añadir.setDoubleBuffered(true);
        btn_conf_añadir.setFocusable(false);
        btn_conf_añadir.setRequestFocusEnabled(false);
        btn_conf_añadir.setRolloverEnabled(false);
        btn_conf_añadir.setVerifyInputWhenFocusTarget(false);
        btn_conf_añadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_conf_añadirActionPerformed(evt);
            }
        });

        btn_conf_quitar.setBackground(new java.awt.Color(51, 51, 51));
        btn_conf_quitar.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btn_conf_quitar.setForeground(new java.awt.Color(153, 153, 153));
        btn_conf_quitar.setText("<<");
        btn_conf_quitar.setBorder(null);
        btn_conf_quitar.setBorderPainted(false);
        btn_conf_quitar.setFocusPainted(false);
        btn_conf_quitar.setFocusable(false);
        btn_conf_quitar.setRolloverEnabled(false);
        btn_conf_quitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_conf_quitarActionPerformed(evt);
            }
        });

        btn_conf_limpiar.setBackground(new java.awt.Color(51, 51, 51));
        btn_conf_limpiar.setForeground(new java.awt.Color(153, 153, 153));
        btn_conf_limpiar.setText("LIMPIAR");
        btn_conf_limpiar.setBorder(null);
        btn_conf_limpiar.setBorderPainted(false);
        btn_conf_limpiar.setFocusPainted(false);
        btn_conf_limpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_conf_limpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_conf_añadir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_conf_quitar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_conf_limpiar, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_conf_añadir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_conf_quitar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_conf_limpiar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_Lista_Versiones_Java.setBackground(new java.awt.Color(51, 51, 51));
        jPanel_Lista_Versiones_Java.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jPanel_Lista_Versiones_Java.setMaximumSize(new java.awt.Dimension(200, 32767));
        jPanel_Lista_Versiones_Java.setPreferredSize(new java.awt.Dimension(200, 2));
        jPanel_Lista_Versiones_Java.setLayout(new javax.swing.BoxLayout(jPanel_Lista_Versiones_Java, javax.swing.BoxLayout.Y_AXIS));

        title_versionesJava.setBackground(new java.awt.Color(204, 204, 204));
        title_versionesJava.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        title_versionesJava.setForeground(new java.awt.Color(153, 153, 153));
        title_versionesJava.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title_versionesJava.setText("Versiones Java Disponibles");

        title_ListaCambios.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        title_ListaCambios.setForeground(new java.awt.Color(153, 153, 153));
        title_ListaCambios.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title_ListaCambios.setText("Lista de Cambios a realizar");

        jScrollPane4.setBorder(null);
        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextPane_Conf_Sistema.setEditable(false);
        jTextPane_Conf_Sistema.setBackground(new java.awt.Color(51, 51, 51));
        jTextPane_Conf_Sistema.setBorder(null);
        jTextPane_Conf_Sistema.setFont(new java.awt.Font("Noto Mono", 0, 8)); // NOI18N
        jTextPane_Conf_Sistema.setForeground(new java.awt.Color(153, 153, 153));
        jTextPane_Conf_Sistema.setMargin(new java.awt.Insets(2, 40, 2, 6));
        jTextPane_Conf_Sistema.setMaximumSize(new java.awt.Dimension(421, 10000));
        jTextPane_Conf_Sistema.setMinimumSize(new java.awt.Dimension(421, 13));
        jTextPane_Conf_Sistema.setPreferredSize(new java.awt.Dimension(421, 13));
        jScrollPane4.setViewportView(jTextPane_Conf_Sistema);

        jScrollPane5.setBorder(null);

        JTextPanel_ListaCambios.setBackground(new java.awt.Color(51, 51, 51));
        JTextPanel_ListaCambios.setColumns(20);
        JTextPanel_ListaCambios.setForeground(new java.awt.Color(102, 102, 102));
        JTextPanel_ListaCambios.setLineWrap(true);
        JTextPanel_ListaCambios.setRows(5);
        jScrollPane5.setViewportView(JTextPanel_ListaCambios);

        title_ResultadoEjecucion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        title_ResultadoEjecucion.setForeground(new java.awt.Color(153, 153, 153));
        title_ResultadoEjecucion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title_ResultadoEjecucion.setText("Resultado Ejecucion");

        title_configuracionSistema.setBackground(new java.awt.Color(204, 204, 204));
        title_configuracionSistema.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        title_configuracionSistema.setForeground(new java.awt.Color(153, 153, 153));
        title_configuracionSistema.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title_configuracionSistema.setText("Configuracion actual del sistema");

        jPanel3.setBackground(new java.awt.Color(102, 102, 102));
        jPanel3.setMaximumSize(new java.awt.Dimension(236, 32767));
        jPanel3.setMinimumSize(new java.awt.Dimension(236, 0));

        btn_conf_sistema.setBackground(new java.awt.Color(51, 51, 51));
        btn_conf_sistema.setForeground(new java.awt.Color(153, 153, 153));
        btn_conf_sistema.setText("SISTEMA");
        btn_conf_sistema.setBorder(null);
        btn_conf_sistema.setBorderPainted(false);
        btn_conf_sistema.setFocusPainted(false);
        btn_conf_sistema.setMaximumSize(new java.awt.Dimension(105, 16));
        btn_conf_sistema.setMinimumSize(new java.awt.Dimension(105, 16));
        btn_conf_sistema.setPreferredSize(new java.awt.Dimension(105, 16));
        btn_conf_sistema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_conf_sistemaActionPerformed(evt);
            }
        });

        jPanel_ListaPlugins.setBackground(new java.awt.Color(51, 51, 51));
        jPanel_ListaPlugins.setLayout(new javax.swing.BoxLayout(jPanel_ListaPlugins, javax.swing.BoxLayout.Y_AXIS));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_conf_sistema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_ListaPlugins, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_conf_sistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_ListaPlugins, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane3.setBorder(null);
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextArea_configuracionSistema.setBackground(new java.awt.Color(51, 51, 51));
        jTextArea_configuracionSistema.setColumns(20);
        jTextArea_configuracionSistema.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jTextArea_configuracionSistema.setForeground(new java.awt.Color(102, 102, 102));
        jTextArea_configuracionSistema.setLineWrap(true);
        jTextArea_configuracionSistema.setRows(5);
        jTextArea_configuracionSistema.setBorder(null);
        jScrollPane3.setViewportView(jTextArea_configuracionSistema);

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextPane_Resultado.setBackground(new java.awt.Color(51, 51, 51));
        jTextPane_Resultado.setColumns(20);
        jTextPane_Resultado.setForeground(new java.awt.Color(102, 102, 102));
        jTextPane_Resultado.setLineWrap(true);
        jTextPane_Resultado.setRows(5);
        jTextPane_Resultado.setMaximumSize(new java.awt.Dimension(421, 2147483647));
        jTextPane_Resultado.setMinimumSize(new java.awt.Dimension(421, 20));
        jTextPane_Resultado.setPreferredSize(new java.awt.Dimension(421, 84));
        jScrollPane2.setViewportView(jTextPane_Resultado);

        javax.swing.GroupLayout jPanel_bodyLayout = new javax.swing.GroupLayout(jPanel_body);
        jPanel_body.setLayout(jPanel_bodyLayout);
        jPanel_bodyLayout.setHorizontalGroup(
            jPanel_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bodyLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(title_versionesJava, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_Lista_Versiones_Java, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, Short.MAX_VALUE))
                .addGroup(jPanel_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(title_configuracionSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_bodyLayout.createSequentialGroup()
                        .addGroup(jPanel_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(title_ListaCambios, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_bodyLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel_bodyLayout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(title_ResultadoEjecucion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_bodyLayout.setVerticalGroup(
            jPanel_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bodyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(title_versionesJava)
                    .addComponent(title_configuracionSistema))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_bodyLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel_bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(title_ListaCambios)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(title_ResultadoEjecucion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2))
                    .addComponent(jPanel_Lista_Versiones_Java, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_bodyLayout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(36, 36, 36))
        );

        getContentPane().add(jPanel_body);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_MinimizaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_MinimizaMouseClicked
        this.Minimiza_Ventana();
    }//GEN-LAST:event_jButton_MinimizaMouseClicked

    private void jButton_closeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_closeMouseClicked
        
    }//GEN-LAST:event_jButton_closeMouseClicked

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        this.Movimiento_Raton_Jframe();
    }//GEN-LAST:event_formMouseDragged

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        PointerInfo a    = MouseInfo.getPointerInfo(); Point b = a.getLocation(); 
        this.pos_mouse_x = (int) b.getX(); 
        this.pos_mouse_y = (int) b.getY();
        this.pos_frame_x = (int) this.getLocation().getX();
        this.pos_frame_y = (int) this.getLocation().getY();
    }//GEN-LAST:event_formMousePressed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        
        if(evt.getClickCount() == 2 && !evt.isConsumed() ){
            if(this.aplicacion_maximizada){
                this.DefaultSize_Centra_Aplicacion();
                this.aplicacion_maximizada = false;
            }else{
                this.Maximizar_Ventana();
                this.aplicacion_maximizada = true;
            }
        }
        this.Mensajes_Conf_Sistema = new ArrayList();
        
    }//GEN-LAST:event_formMouseClicked

    private void jButton_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_closeActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton_closeActionPerformed

    private void BTN_GO(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTN_GO
        
        this.Mensajes_Resultado = new ArrayList();
        
        //Evento
        if
                
        (
            this.jdkjre_seleccionado == null || this.jdkjre_seleccionado.equals("")
            ||
            this.accion_seleccionada == null || this.accion_seleccionada.equals("")
        )
            
        {
            
            if( this.jdkjre_seleccionado == null || this.jdkjre_seleccionado.equals("") )
            {
                this.Mensajes_Resultado.add( "No has seleccionado ninguna version de jdk o jre " );
            }
            else if( this.accion_seleccionada == null || this.accion_seleccionada.equals("") )
            {
                this.Mensajes_Resultado.add( " No has indicado que hay que hacer. Pulsa en el boton sistema o en algun boton de plugin " );
            }
            else
            {
                this.Mensajes_Resultado.add( "No se ha realizado ninguna accion, selecciona una version de java y indica donde se aplica la configuracion" );
            }
            
        }
        
        else
            
        {
            this.Mensajes_Resultado.add( "Se ha cambiado a " + this.jdkjre_seleccionado + " para " + this.accion_seleccionada );
            
            String resCom = this.init.Execute();
            String res    = ( resCom != null && resCom != "" )? resCom : "\n sin respuesta";
 
            this.Mensajes_Resultado.add( res );
            
            resCom        = null;
            res           = null;
        }
        
        codebaou.selectjava.Init.Execute_Get_Info( this.init, this ); 
        this.Reload_MensajesUsuario_resultado();
       
    }//GEN-LAST:event_BTN_GO

    private void btn_conf_quitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_conf_quitarActionPerformed

        this.jdkjre_seleccionado = null;
        this.Reload_Lista_Java_Disponibles();
    }//GEN-LAST:event_btn_conf_quitarActionPerformed

    private void btn_conf_sistemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_conf_sistemaActionPerformed
        
        this.accion_seleccionada = "sistema";
        this.init.Set_Select_Accions( this.jdkjre_seleccionado, this.accion_seleccionada);
        this.Reload_MensajesUsuario_lista_cambios();
    }//GEN-LAST:event_btn_conf_sistemaActionPerformed

    private void btn_conf_limpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_conf_limpiarActionPerformed
        
        this.accion_seleccionada   = null;
        this.jdkjre_seleccionado   = null;
        this.jdkjre_aux            = null;
        this.Mensajes_Conf_Sistema = new ArrayList();
        this.Mensajes_Resultado    = new ArrayList();
        this.Reload_MensajesUsuario_lista_cambios();
        this.Reload_MensajesUsuario_resultado();
    }//GEN-LAST:event_btn_conf_limpiarActionPerformed

    private void btn_conf_añadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_conf_añadirActionPerformed

        this.jdkjre_seleccionado = this.jdkjre_aux;
        this.init.Set_Select_Accions( jdkjre_seleccionado, accion_seleccionada );
        this.Reload_MensajesUsuario_lista_cambios();
        this.Reload_MensajesUsuario_resultado();
    }//GEN-LAST:event_btn_conf_añadirActionPerformed

    /** Calcula el desplazamiento del frame segun el movimiento del raton */
    private void Movimiento_Raton_Jframe(){
        PointerInfo a = MouseInfo.getPointerInfo(); Point b = a.getLocation(); 
        int mouse_x   = (int) b.getX() - this.pos_mouse_x; 
        int mouse_y   = (int) b.getY() - this.pos_mouse_y;
        int frame_x   = this.pos_frame_x + mouse_x;
        int frame_y   = this.pos_frame_y + mouse_y;
        this.setLocation(frame_x, frame_y);
    }
    
    /** Si la aplicacion esta maximizada se minimiza altamaño por defecto de la aplicacion
    * Si la aplicacion esta minimizada
    */
    private void Posicion_Centrar_Aplicacion(){
        
        Toolkit t           = Toolkit.getDefaultToolkit();    
        Dimension pantalla  = t.getScreenSize();
        
        if(pantalla.width>this.resolucionPorDefecto.width && pantalla.height>this.resolucionPorDefecto.height){
            int posicionW = (pantalla.width/2)-(this.resolucionPorDefecto.width/2);
            int posicionH = (pantalla.height/2)-(this.resolucionPorDefecto.height/2);
            this.setLocation( posicionW , posicionH );
        }else{
            this.Maximizar_Ventana();
        }  
    }
    
    /** Reduce el JFrame al tamaño por defecto y la centra en la pantalla*/
    private void DefaultSize_Centra_Aplicacion(){
        this.setSize(this.resolucionPorDefecto);
        this.Posicion_Centrar_Aplicacion();
    }
    
    /** Maximiza la aplicacion al tamaño de pantalla*/
    private void Maximizar_Ventana(){
        //this.setExtendedState(this.MAXIMIZED_BOTH); // Maximizar Ventana
        //this.setLocation(0,0);
    }
      
    private void Minimiza_Ventana(){
        this.setExtendedState(ICONIFIED);
    }

    /**Estilo JScrollBar */
    private void Estilo_ScrollBar(javax.swing.JScrollPane SCROLBAR) {
        UIManager.put("ScrollBar.thumb", new ColorUIResource(new Color(102, 102, 102)));
        UIManager.put("ScrollBar.track", new ColorUIResource(new Color(51, 51, 51)));
        UIManager.put("ScrollBar.backgorund", new ColorUIResource(new Color(51, 51, 51)));
        SCROLBAR.setBorder(null);
        SCROLBAR.getVerticalScrollBar().setUI(new BasicScrollBarUI());
    }
  
    /** 
    * Aplica fuentes a elementos de la interfaz
    */
    private void Set_FontsJavaSelect(){
        
        //Titulos paneles
        this.title_ListaCambios.setFont( this.fuente.fOxygenMonoRegularFont.deriveFont( Font.BOLD, 12 ) );
        this.title_ResultadoEjecucion.setFont( this.fuente.fOxygenMonoRegularFont.deriveFont( Font.BOLD, 12 ) );
        this.title_configuracionSistema.setFont( this.fuente.fOxygenMonoRegularFont.deriveFont( Font.BOLD, 12 ) );
        this.title_versionesJava.setFont( this.fuente.fOxygenMonoRegularFont.deriveFont( Font.BOLD, 12 ) );
        //Fuente Paneles
        this.JTextPanel_ListaCambios.setFont( this.fuente.fOxygenMonoRegularFont.deriveFont( Font.BOLD, 10 ) );
        this.jTextArea_configuracionSistema.setFont( this.fuente.fOxygenMonoRegularFont.deriveFont( Font.BOLD, 10 ) );
        this.jTextPane_Resultado.setFont( this.fuente.fOxygenMonoRegularFont.deriveFont( Font.BOLD, 10 ) );
        this.JTextPanel_ListaCambios.setMargin( new Insets(10,10,10,10) );
        this.jTextArea_configuracionSistema.setMargin( new Insets(10,10,10,10) );       
        this.jTextPane_Resultado.setMargin( new Insets(10,10,10,10) );

        //Botones sistemas
        this.btn_conf_añadir.setFont( this.fuente.fOxygenMonoRegularFont.deriveFont( Font.BOLD, 11 ) );
        this.btn_conf_limpiar.setFont( this.fuente.fOxygenMonoRegularFont.deriveFont( Font.BOLD, 11 ) );
        this.btn_conf_quitar.setFont( this.fuente.fOxygenMonoRegularFont.deriveFont( Font.BOLD, 11 ) );
        this.btn_conf_sistema.setFont( this.fuente.fOxygenMonoRegularFont.deriveFont( Font.BOLD, 11 ) );

    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BTN_DO;
    private javax.swing.JPanel Body_head;
    private javax.swing.JPanel Header;
    private javax.swing.JPanel Header_title;
    private javax.swing.JPanel Header_window;
    private javax.swing.JTextArea JTextPanel_ListaCambios;
    private javax.swing.JLabel SELECTJAVA;
    private javax.swing.JButton btn_conf_añadir;
    private javax.swing.JButton btn_conf_limpiar;
    private javax.swing.JButton btn_conf_quitar;
    private javax.swing.JButton btn_conf_sistema;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.JButton jButton_Minimiza;
    private javax.swing.JButton jButton_close;
    private javax.swing.JButton jButton_maximiza;
    private javax.swing.JLabel jLabel_VersionPrograma;
    private javax.swing.JLabel jLabel_alerta;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel_ListaPlugins;
    private javax.swing.JPanel jPanel_Lista_Versiones_Java;
    private javax.swing.JPanel jPanel_body;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea jTextArea_configuracionSistema;
    private javax.swing.JTextPane jTextPane_Conf_Sistema;
    private javax.swing.JTextArea jTextPane_Resultado;
    private javax.swing.JLabel title_ListaCambios;
    private javax.swing.JLabel title_ResultadoEjecucion;
    private javax.swing.JLabel title_configuracionSistema;
    private javax.swing.JLabel title_versionesJava;
    // End of variables declaration//GEN-END:variables
}
