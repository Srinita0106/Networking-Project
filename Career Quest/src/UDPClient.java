import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {
    private static final int SERVER_PORT = 12348;

    public static void mainInp(String client_url){
        try {
            // Get the URL from the user via standard input
            String directory = System.getProperty("user.dir");
            System.out.println("Java file directory: " + directory);

            Scanner scanner = new Scanner(System.in);
            //System.out.print("Enter the URL: ");
            String url = client_url;


            // Send the URL to the server
            sendUrlToServer(url);

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        System.out.println("MAIN");
    }

    private static void sendUrlToServer(String url) throws Exception {
        // Create a UDP socket
        DatagramSocket clientSocket = new DatagramSocket();

        // Get the server address
        InetAddress serverAddress = InetAddress.getLocalHost();

        // Convert the URL to bytes
        byte[] sendData = url.getBytes();

        // Create a datagram packet to send to the server
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);

        // Send the packet to the server
        clientSocket.send(sendPacket);
        System.out.println("URL sent to the server.");

        // Receive the response from the server
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        // Convert the response to a string
        String serverOutput = new String(receivePacket.getData()).trim();

        // Display the server output
        System.out.println("Server Output:");
        System.out.println(serverOutput);

        // Close the socket
        clientSocket.close();
    }
}

// https://www.linkedin.com/in/srinita-jayaraj-b3b27a227/
// name , title , objective , realtime application