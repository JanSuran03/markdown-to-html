import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Form extends JFrame {
    private JTextArea markdownTextArea;
    private JPanel mainPanel;
    private JButton saveButton;
    private JTextPane markdownHTML;
    private JButton testButton;

    void addButtonListeners() {
        saveButton.addActionListener(actionEvent -> {
            try {
                markdownHTML.setText(Parser.parseHTML(markdownTextArea.getText()));
                pack();
            } catch (Exception ex) {
                System.out.println("Failed converting to HTML.");
            }
        });

        testButton.addActionListener(actionEvent -> {
            markdownTextArea.setText(testString);
            try {
                markdownHTML.setText(Parser.parseHTML(testString));
            } catch (Exception ex) {
                System.out.println("Failed converting to HTML.");
            }
            pack();
        });
    }

    static public String testString =
            "pepega kozel\n" +
                    "nice\n" +
                    "--asd\n" +
                    "---\n" +
                    "\n" +
                    "An [example](http://example.com) link\n" +
                    "\n" +
                    "This paragraph\n" +
                    "should not end\n" +
                    "\n" +
                    "now it should\n" +
                    "\n" +
                    "**this** is bold\n" +
                    "`this` is monospace\n" +
                    "_this_ is italic\n\n" +
                    "if any **tags are open,\n" +
                    "this _program\n" +
                    "closes ~~them\n" +
                    "`automatically";

    Form(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setBounds(600, 400, 500, 300);
        pack();
        addButtonListeners();
        markdownTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                pack();
            }
        });
    }

    public static void main(String[] args) {
        Form form = new Form("Markdown -> HTML");
        form.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
