package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient {

    private Socket clientSocket;

    public static void main(String[] args) {

        ChatClient client = new ChatClient();

        try {

            client.start();

        } catch (IOException e) {
            System.out.println("Client Error - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void start() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String hostName = setHostInput(reader);
        int port = setPortInput(reader);

        clientSocket = new Socket(hostName, port);
        System.out.println("Connection to Server Success");

        ExecutorService service = Executors.newFixedThreadPool(10);
        PrintWriter outStream = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        setUsernameInput(reader, outStream);

        service.submit(new ClientReceive(inStream));
        sendMessage(reader, outStream);

        close(reader);
    }

    private String setHostInput(BufferedReader reader) throws IOException {
        System.out.print("HostName: ");
        return reader.readLine();
    }

    private int setPortInput(BufferedReader reader) throws IOException {
        System.out.print("Port: ");
        return Integer.parseInt(reader.readLine());
    }

    private void setUsernameInput(BufferedReader reader, PrintWriter out) throws IOException {

        String username = null;
        boolean usernameOK = false;

        while(!usernameOK) {

            System.out.print("Username: ");
            username = reader.readLine();

            if(username.length()<=20){
                usernameOK = true;
            } else {
                System.out.println("Username invalid, too long, 10 words max");
            }
        }
        out.println(username);
    }

    private void sendMessage(BufferedReader reader, PrintWriter outStream) {

        System.out.println("Connected to Chat");
        String line = "";

        while (!line.equals("/quit")) {

            try {

                line = reader.readLine();
                outStream.println(line);

            } catch (IOException ex) {

                System.out.println("Sending error: " + ex.getMessage() + ", closing client...");
                break;

            }
        }

    }

    private void close(BufferedReader reader) {

        try {

            if(reader!=null) {
                reader.close();
                System.out.println("Closed reader");
            }

            if(clientSocket!=null) {
                clientSocket.close();
                System.out.println("Closed Socket");
            }

            System.out.println("Bye bye");
            System.exit(0);

        } catch (IOException e) {
            System.out.println("Closing Error");
            System.exit(-1);
        }
    }


    public class ClientReceive implements Runnable {

        private BufferedReader inStream;

        public ClientReceive(BufferedReader inStream) {
            this.inStream=inStream;
        }

        @Override
        public void run() {

            String line;

            try {

                while(true){

                    line = inStream.readLine();

                    if(line == null){
                        System.out.println("Server closed");
                        break;
                    }

                    System.out.println(line);

                }

            } catch (IOException e) {
                System.out.println("Server closed - " + e.getMessage());
            }
        }
    }
}
