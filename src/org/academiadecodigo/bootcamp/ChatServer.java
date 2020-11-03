package org.academiadecodigo.bootcamp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private ServerSocket serverSocket;
    private LinkedList<ServerWorker> clientSocketList;

    public static void main(String[] args) {

        ChatServer server = new ChatServer();

        try {

            server.start();

        } catch (IOException e) {
            System.out.println("Server Error - " + e.getMessage());
            e.printStackTrace();
        } finally {
            server.closeServer();
        }
    }


    private void start() throws IOException {

        int port = getPortInput();

        serverSocket = new ServerSocket(port);
        clientSocketList = new LinkedList();
        ExecutorService service = Executors.newCachedThreadPool();

        while(true) {

            System.out.println("Waiting for connection...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection Success");

            service.submit(new ServerWorker(clientSocket));

        }
    }

    private int getPortInput() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Port: ");
        int port = Integer.parseInt(reader.readLine());
        reader.close();
        return port;
    }

    private synchronized void sendAll(String message) throws IOException{

        PrintWriter outAll;
        Iterator<ServerWorker> it = clientSocketList.iterator();

        while(it.hasNext()){

            ServerWorker serverWorker = it.next();

            outAll = new PrintWriter(serverWorker.getSocket().getOutputStream(), true);
            outAll.println(message);

        }

    }

    private void closeServer() {

        try {

            if(serverSocket!=null) {
                serverSocket.close();
                System.out.println("Server Closed");
            }

            System.out.println("Bye bye");
            System.exit(0);

        }
        catch (IOException e) {
            System.out.println("Closing Error");
            System.exit(-1);
        }
    }



    public class ServerWorker implements Runnable {

        private Socket clientSocket;
        private String username;
        private ChatClient chatClient = new ChatClient();

        public ServerWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {

            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                setUsernameInput(in);
                clientSocketList.add(this);

                printAndSendMessage(in);

            } catch (IOException e) {
                System.out.println("ServerWorker run Error - " + e.getMessage());
            } finally {
                close();
            }
        }

        private Socket getSocket() {
            return clientSocket;
        }

        private String getUsername(){
            return username;
        }

        private void setUsernameInput(BufferedReader in) throws IOException {
            System.out.println("Waiting for username input");
            username = in.readLine();
            String message = "User " + username + " Connected";
            System.out.println(message);
            sendAll(message);
        }

        private void printAndSendMessage(BufferedReader in) {

            String clientMessage;

            try {

                while (true) {

                    clientMessage = in.readLine();

                    if (clientMessage == null || clientMessage.equals("/quit")) {

                        System.out.println("Client " + username + " closed, exiting");
                        sendAll("User " + username + " Disconnected");
                        break;

                    }

                    clientMessage = username + ": " + clientMessage;

                    System.out.println(clientSocket.getInetAddress().getHostName() + " " + clientMessage);
                    sendAll(clientMessage);
                }

            } catch (IOException ex) {
                System.out.println("Receiving error: " + username + " " + ex.getMessage());
            } finally {
                close();
            }
        }

        private void close(){

            try {

                if(clientSocketList.contains(this)) {
                    clientSocketList.remove(this);
                }

                if(clientSocket!=null) {
                    clientSocket.close();
                }

            } catch (IOException e) {
                System.out.println("Closing Client Socket Error - " + e.getMessage());
            }
        }

        public String toString() {
            Thread.currentThread().setName(username);
            return Thread.currentThread().getName();
        }
    }
}

