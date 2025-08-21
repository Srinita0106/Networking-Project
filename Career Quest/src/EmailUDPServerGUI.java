import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;

class EmailUDPServerGUI extends JFrame {
    private static final int SERVER_PORT = 2047;
    private static final List<String> clientAddresses = Arrays.asList(
            "192.168.0.19"// Replace with actual client addresses
    );

    private JTextArea logTextArea;
    private JLabel statusLabel;

    public EmailUDPServerGUI() {
        setTitle("Email UDP Server");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new BackgroundPanel());


        // Create the status label
        statusLabel = new JLabel("Server Status: Stopped");
        statusLabel.setFont(new Font("PT Sans", Font.BOLD, 20));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBounds(50, 50, 50, 50);
        getContentPane().add(statusLabel);


        // Create the start server button
        JButton startButton = new JButton("Start Server");
        startButton.setFont(new Font("PT Sans", Font.BOLD, 18));
        startButton.setBounds(50, 50, 50, 50);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
                statusLabel.setText("Server Status: Running");
                startButton.setEnabled(false);

                logTextArea = new JTextArea();
                logTextArea.setEditable(false);
                logTextArea.setFont(new Font("PT Sans", Font.PLAIN, 18));
                startButton.setBounds(50, 120, 200, 50); // Update the position and size
                // Create the log area

                JScrollPane scrollPane = new JScrollPane(logTextArea);
                scrollPane.setBounds(0, 0, 0, 0);
                getContentPane().add(scrollPane);



            }
        });
        getContentPane().add(startButton);
    }

    public void startServer() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(SERVER_PORT)) {
                byte[] receiveData = new byte[1024];

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);

                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    logTextArea.append("Received message from client: " + message + "\n");

                    // Extract message and received checksum
                    String extractedMessage = message.substring(0, message.length() - 4);
                    String receivedChecksum;

                    if (extractedMessage.matches("[0-9]+")) {
                        // Received checksum is a numeric string
                        // Assign receivedChecksum to a different value

                        receivedChecksum = "0100";
                    } else {
                        // Received checksum is not a numeric string
                        // Keep the received checksum as it is
                         receivedChecksum = message.substring(message.length() - 4);
                    }

                    // Verify checksum
                    if (verifyChecksum(extractedMessage, receivedChecksum)) {
                        logTextArea.append("Checksum verification passed. Message received successfully.\n");

                        // Send email to receivers
                        sendEmail(extractedMessage);
                    } else {
                        logTextArea.append("Checksum verification failed. Message may be corrupted.\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean verifyChecksum(String message, String receivedChecksum) {
        // Calculate checksum
        String calculatedChecksum = calculateCRC(message);

        logTextArea.append("Message: " + message + "\n");
        logTextArea.append("Received Checksum: " + receivedChecksum + "\n");
        logTextArea.append("Calculated Checksum: " + calculatedChecksum + "\n");

        // Compare received checksum with calculated checksum
        return calculatedChecksum.equals(receivedChecksum);
    }

    private String calculateCRC(String message) {
        String divisor = "1101"; // CRC-4: x^4 + x + 1

        StringBuilder dividend = new StringBuilder(message);
        dividend.append("0000");

        int dividendBitLength = dividend.length();
        int divisorBitLength = divisor.length();
        int shiftBitCount = dividendBitLength - divisorBitLength + 1;

        for (int i = 0; i < shiftBitCount; i++) {
            if (dividend.charAt(i) == '1') {
                for (int j = 0; j < divisorBitLength; j++) {
                    if (dividend.charAt(i + j) == divisor.charAt(j)) {
                        dividend.setCharAt(i + j, '0');
                    } else {
                        dividend.setCharAt(i + j, '1');
                    }
                }
            }
        }

        return dividend.substring(dividendBitLength - 4);
    }

    private void sendEmail(String message) {
        String pythonScriptPath = "C:/Users/jssri/Python/broadcast.py"; // Replace with the actual path to your Python script

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                logTextArea.append("Email sent to receivers.\n");
            } else {
                logTextArea.append("Failed to send email.\n");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EmailUDPServerGUI serverGUI = new EmailUDPServerGUI();
                serverGUI.setVisible(true);
            }
        });
    }

    // Custom panel for displaying background image
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon("C:/Users/jssri/Downloads/Grenze (2).png").getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
