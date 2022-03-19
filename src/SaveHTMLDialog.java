import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SaveHTMLDialog extends JFileChooser {
    public SaveHTMLDialog() {
        setDialogTitle("Save as HTML");
        FileFilter asHTML = new FileNameExtensionFilter
                ("HTML files (*.html)", "html");
        FileFilter asTxt = new FileNameExtensionFilter
                ("Text files (*.txt)", "txt");
        addChoosableFileFilter(asHTML);
        addChoosableFileFilter(asTxt);
        setFileFilter(asHTML);
    }
}
