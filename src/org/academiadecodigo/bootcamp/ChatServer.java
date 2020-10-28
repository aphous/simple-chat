package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int port;

    public static void main(String[] args) {

        ChatServer server = new ChatServer();

        try {

            server.getPortInput();

            server.start();

            server.printAndSendMessage();

        } catch (IOException e) {
            System.out.println("Error");
        } finally {
            server.close();
            System.out.println("Byebye");
        }
    }

    private void start() throws IOException {

        serverSocket = new ServerSocket(port);
        System.out.println("Waiting for connection...");
        clientSocket = serverSocket.accept();
        System.out.println("Connection Success");

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    }

    private void printAndSendMessage() throws IOException {
        String message;
        while((message = in.readLine())!=null) {
            System.out.println(clientSocket.getInetAddress().getHostName() + " " + message);
            out.println(message);
        }
    }

    private void getPortInput() throws IOException {

        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Port: ");
        this.port = Integer.parseInt(r.readLine());
        r.close();

    }

    private void close() {

        try {
            System.out.println("Closing");
            serverSocket.close();
            clientSocket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.out.println("Closing Error");
        }
    }
}
