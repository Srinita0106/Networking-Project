import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PictureBackgroundExample extends JFrame {
    private Timer timer;

    public PictureBackgroundExample() {
        setTitle("Picture Background Example");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

        // Set the layout manager
        setLayout(new BorderLayout());

        // Create a JPanel to hold the picture
        JPanel picturePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load the picture
                ImageIcon image = new ImageIcon("C:/Users/jssri/Downloads/Grenze.png");
                // Draw the picture on the panel
                g.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };

        // Add the picture panel to the frame
        add(picturePanel, BorderLayout.CENTER);

        // Start the timer
        timer = new Timer(20000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Stop the timer
                timer.stop();
                // Create the next Swing GUI page
                Swing nextPage = new Swing(PictureBackgroundExample.this, true);
                // Dispose of the current window
                dispose();
                // Display the next page
                nextPage.setVisible(true);
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        // Create and display the picture background example page
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PictureBackgroundExample().setVisible(true);
            }
        });
    }
}

// https://www.linkedin.com/in/karshana-b-g-0540b4226/
// https://www.linkedin.com/in/srinita-jayaraj-b3b27a227/