package FontsJavaSelect;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.List;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cargar fuentes 
 * @author CodeBAou
 */
public class Fonts {    

    public Font fOxygenMonoRegularFont            = null; 
    private InputStream inputRubikMoonrocks       = null;

    public Fonts(){
        
        String OxygenMonoRegularpath  = ".\\src\\OxygenMono-Regular.ttf";
        try {
            inputRubikMoonrocks     = new BufferedInputStream( new FileInputStream(OxygenMonoRegularpath) );
            fOxygenMonoRegularFont  = Font.createFont(Font.TRUETYPE_FONT, inputRubikMoonrocks);   
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fonts.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FontFormatException ex) {
            Logger.getLogger(Fonts.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Fonts.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
