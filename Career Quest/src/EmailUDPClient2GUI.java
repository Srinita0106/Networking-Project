import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;

class EmailUDPClient2GUI extends JFrame {

    private static final int SERVER_PORT = 2047;

    private JLabel jLabel1;
    private JTextField jTextField1;
    private JButton jButton1;

    public EmailUDPClient2GUI() {
        initComponents();
        setSize(600, 500);
    }

    private void initComponents() {
        jLabel1 = new JLabel();
        jTextField1 = new JTextField();
        jButton1 = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(new Color(0, 0, 0));


        getContentPane().setBackground(Color.BLACK); // Set the background color to black

        jLabel1.setBackground(new Color(255, 255, 255));
        jLabel1.setForeground(new Color(255, 255, 255));
        jLabel1.setText("Enter Message:");


        jButton1.setText("Send Message");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(40, 40, 40)
                                                .addComponent(jLabel1)
                                                .addGap(45, 45, 45)
                                                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(134, 134, 134)
                                                .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(98, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(114, 114, 114)
                                .addComponent(jButton1)
                                .addContainerGap(123, Short.MAX_VALUE))
        );

        pack();
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
        sendMessage();
    }

    private void sendMessage() {
        String message = jTextField1.getText().trim();

        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a non-empty message.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                InetAddress serverAddress = InetAddress.getByName("localhost");

                String checksum = calculateCRC(message);

                String messageWithChecksum = message + checksum;
                byte[] sendData = messageWithChecksum.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                socket.send(sendPacket);

                // Update the GUI in the event dispatch thread
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Message sent to server.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    jTextField1.setText(""); // Clear the text field
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EmailUDPClient2GUI().setVisible(true);
            }
        });
    }
}
