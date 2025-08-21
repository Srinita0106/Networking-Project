import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UDPServer extends JFrame {
    private static final int BUFFER_SIZE = 1024;
    private static final int SERVER_PORT = 12348;

    private JTextArea textArea;

    public UDPServer() {



        // Create a JTextArea to display the console output
        textArea = new JTextArea(20, 40);

        // Redirect the standard output to the JTextArea
        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
        System.setOut(printStream);
        System.setErr(printStream);

        // Create a panel with BorderLayout to hold the text area
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Set up the GUI
        add(panel);
        setTitle("UDP Server");
        setPreferredSize(new Dimension(800, 600)); // Set the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        // Create an instance of the UDPServer GUI
        UDPServer server = new UDPServer();

        try {
            // Create a UDP socket
            DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
            System.out.println("UDP server is up and running.");

            byte[] receiveBuffer = new byte[BUFFER_SIZE];

            while (true) {
                // Receive data from the client
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);

                // Extract the received data
                String url = new String(receivePacket.getData(), 0, receivePacket.getLength());

                byte[] ipHeader = Arrays.copyOfRange(receivePacket.getData(), 0, 20);

                // Print the packet details
                System.out.println("\n--------Packet Details:--------\n");

                System.out.println("Source Port: " + receivePacket.getPort());

                System.out.println("Destination Port: " + serverSocket.getLocalPort());
                System.out.println("Packet Length: 0x" + Integer.toHexString(receivePacket.getLength()));

                System.out.println("Transport Protocol: UDP");


                // Extract the payload data
                byte[] payloadData = Arrays.copyOfRange(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                String payload = new String(payloadData);
                System.out.println("Payload Data: " + payload);

                // Extract IP header details
                int version = (ipHeader[0] & 0xFF) >> 4;
                int headerLength = (ipHeader[0] & 0x0F) * 4;
                int priority = (ipHeader[1] & 0xE0) >> 5;
                int totalLength = ((ipHeader[2] & 0xFF) << 8) | (ipHeader[3] & 0xFF);
                int identification = ((ipHeader[4] & 0xFF) << 8) | (ipHeader[5] & 0xFF);
                int flags = (ipHeader[6] & 0xE0) >> 5;
                int fragmentOffset = ((ipHeader[6] & 0x1F) << 8) | (ipHeader[7] & 0xFF);
                int timeToLive = ipHeader[8] & 0xFF;
                int protocol = ipHeader[9] & 0xFF;
                int headerChecksum = ((ipHeader[10] & 0xFF) << 8) | (ipHeader[11] & 0xFF);
                String sourceIP = InetAddress.getByAddress(Arrays.copyOfRange(ipHeader, 12, 16)).getHostAddress();
                String destinationIP = InetAddress.getByAddress(Arrays.copyOfRange(ipHeader, 16, 20)).getHostAddress();



                System.out.println("\n--------IP Header Details:--------\n");
                System.out.println("Version: " + version);
                System.out.println("Header Length: " + headerLength + " bytes");
                System.out.println("Priority: " + priority);
                System.out.println("Total Length: " + totalLength + " bytes");
                System.out.println("Identification: " + identification);
                System.out.println("Flags: " + flags);
                System.out.println("Fragment Offset: " + fragmentOffset);
                System.out.println("Time to Live: " + timeToLive);
                System.out.println("Protocol: " + protocol);
                System.out.println("Header Checksum: " + headerChecksum);
                System.out.println("Source IP Address: " + sourceIP);


                // Print the URL
                String prof = "https://fresh-linkedin-profile-data.p.rapidapi.com/get-linkedin-profile?linkedin_url=";
                String newVariable = prof + url;

                System.out.println("Received URL: " + newVariable);

                // Your JSON parsing logic
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(newVariable))
                        .header("X-RapidAPI-Key", "f555ce2efbmsh02637303023d8c2p192948jsna472c8697530")
                        .header("X-RapidAPI-Host", "fresh-linkedin-profile-data.p.rapidapi.com")
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());


                String jsonString = response.body();



                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject dataObject = jsonObject.getJSONObject("data");

                // Extract the desired fields
                String about = getStringValue(dataObject, "about");
                String company = getStringValue(dataObject, "company");
                String city = getStringValue(dataObject, "city");
                String country = getStringValue(dataObject, "country");
                int connections = dataObject.getInt("connections_count");
                JSONArray educations = dataObject.getJSONArray("educations");
                String skills = getStringValue(dataObject, "skills");

                // Print the extracted fields
                System.out.println("\n--------PROFILE DATA--------\n" );
                System.out.println("About: " + about);
                System.out.println("Company: " + company);
                System.out.println("City: " + city);
                System.out.println("Country: " + country);
                System.out.println("Connections: " + connections);
                System.out.println("Educations:");
                for (int i = 0; i < educations.length(); i++) {
                    JSONObject education = educations.getJSONObject(i);
                    String school = education.getString("school");
                    String degree = education.getString("degree");
                    System.out.println("- School: " + school);
                    System.out.println("  Degree: " + degree);
                }
                System.out.println("Skills: " + skills);
                // Get search terms from user input

                String searchTerms = skills;

                // Prepare the request
                String requestBody = "{\r\n" +
                        "    \"search_terms\": \"" + searchTerms + "\",\r\n" +
                        "    \"location\": \"Chicago, IL\",\r\n" +
                        "    \"page\": \"1\"\r\n" +
                        "}";

                HttpRequest jobRequest = HttpRequest.newBuilder()
                        .uri(URI.create("https://linkedin-jobs-search.p.rapidapi.com/"))
                        .header("content-type", "application/json")
                        .header("X-RapidAPI-Key", "13a72b51a1msh7167b79aa73e659p1d5ae8jsn2114a9ebe711")
                        .header("X-RapidAPI-Host", "linkedin-jobs-search.p.rapidapi.com")
                        .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                // Send the job request
                HttpResponse<String> jobResponse = HttpClient.newHttpClient().send(jobRequest, HttpResponse.BodyHandlers.ofString());

                // Print the job response body
                String jsonResponse = jobResponse.body();

                JSONArray jobsArray = new JSONArray(jsonResponse);

                for (int i = 0; i < jobsArray.length(); i++) {
                    JSONObject jobObject = jobsArray.getJSONObject(i);

                    String jobUrl = jobObject.getString("job_url");
                    String cleanedJobUrl = jobObject.getString("linkedin_job_url_cleaned");
                    String companyName = jobObject.getString("company_name");
                    String companyUrl = jobObject.getString("company_url");
                    String cleanedCompanyUrl = jobObject.getString("linkedin_company_url_cleaned");
                    String jobTitle = jobObject.getString("job_title");
                    String jobLocation = jobObject.getString("job_location");
                    String postedDate = jobObject.getString("posted_date");
                    String normalizedCompanyName = jobObject.getString("normalized_company_name");

                    System.out.println("\n--------Available jobs for your skills--------\n");
                    System.out.println("Job URL: " + jobUrl);
                    System.out.println("Cleaned Job URL: " + cleanedJobUrl);
                    System.out.println("Company Name: " + companyName);
                    System.out.println("Company URL: " + companyUrl);
                    System.out.println("Cleaned Company URL: " + cleanedCompanyUrl);
                    System.out.println("Job Title: " + jobTitle);
                    System.out.println("Job Location: " + jobLocation);
                    System.out.println("Posted Date: " + postedDate);
                    System.out.println("Normalized Company Name: " + normalizedCompanyName);
                    System.out.println();
                }

                // Perform job scraping
                JobScraper.performJobScraping(jsonResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getStringValue(JSONObject jsonObject, String key) {
        if (jsonObject.has(key) && !jsonObject.isNull(key)) {
            return jsonObject.getString(key);
        }
        return "";
    }

    class JobScraper {
        public static void performJobScraping(String jsonResponse) {
            JSONArray jobsArray = new JSONArray(jsonResponse);

            String[] jobTitles = new String[jobsArray.length()];

            for (int i = 0; i < jobsArray.length(); i++) {
                JSONObject jobObject = jobsArray.getJSONObject(i);
                String jobTitle = jobObject.getString("job_title");
                jobTitles[i] = jobTitle;
            }



            // Count the occurrences of each job title
            Map<String, Integer> titleCountMap = new HashMap<>();
            for (String title : jobTitles) {
                titleCountMap.put(title, titleCountMap.getOrDefault(title, 0) + 1);
            }

            // Find the most occurring job title
            String mostOccurringTitle = "";
            int maxCount = 0;
            for (Map.Entry<String, Integer> entry : titleCountMap.entrySet()) {
                String title = entry.getKey();
                int count = entry.getValue();
                if (count > maxCount) {
                    mostOccurringTitle = title;
                    maxCount = count;
                }
            }

            // Print the most occurring job title
            System.out.println("\n--------JOB RECOMMENDATION--------\n" );
            System.out.println("Recommended Job : " + mostOccurringTitle);
        }
    }


    // Custom OutputStream implementation
    private static class CustomOutputStream extends OutputStream {
        private JTextArea textArea;


        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            // Redirect the output to the JTextArea
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}
