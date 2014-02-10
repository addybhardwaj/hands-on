import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public class SwingExample extends JFrame {

    public SwingExample() {
    }

    public void init() {

        JPanel panel = new JPanel();
        //add panel to frame
        getContentPane().add(panel);

        panel.setLayout(null);

        JButton quitButton = new JButton("Quit");
        quitButton.setBounds(50, 60, 80, 30);
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        panel.add(quitButton);

        setTitle("Quit button");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SwingExample example = new SwingExample();
                example.init();
                example.setVisible(true);
            }
        });
    }
}
