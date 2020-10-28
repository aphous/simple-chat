package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int port;
    private String hostName;
    private String username;

    public static void main(String[] args) {

        ChatClient client = new ChatClient();
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

        try {

            client.getClientInput(r);

            client.start();

            client.sendMessage(r);

        } catch (IOException e) {
            System.out.println("Error");
        } finally {
            client.close(r);
            System.out.println("Bye Bye");
        }

    }

    private void start() throws IOException {

        clientSocket = new Socket(hostName, port);

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    }

    private void getClientInput(BufferedReader r) throws IOException {

        System.out.print("HostName: ");
        this.hostName = r.readLine();

        System.out.print("Port: ");
        this.port = Integer.parseInt(r.readLine());

        System.out.print("Username: ");
        this.username = r.readLine();

    }

    private void sendMessage(BufferedReader r) throws IOException {

        String line;
        String message;
        System.out.println("Chat open");

        while(!(line = r.readLine()).equals(":q")) {
            message = username + ": " + line;
            out.println(message);
            System.out.println(in.readLine());
        }
    }

    private void close(BufferedReader r) {

        try {
            System.out.println("Closing");
            clientSocket.close();
            in.close();
            out.close();
            r.close();
        } catch (IOException e) {
            System.out.println("Closing Error");
        }
    }

}
