import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<Socket> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println("Server started");

            while (true) {
            	System.out.println("teste");
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);

                // Start a thread to handle each client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private InputStream inFromClient;
        private OutputStream outToClients;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                inFromClient = clientSocket.getInputStream();
                outToClients = clientSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[4*4]; // Adjust buffer size as needed
                int bytesRead;
                while ((bytesRead = inFromClient.read(buffer)) != -1) {
                    // Process received bytes (buffer) as needed
                    System.out.println("Received bytes from client.");
                    broadcast(buffer, bytesRead);
                }
            } catch (IOException e) {
            	//System.out.println("run");
                e.printStackTrace();
            }
        }

        // Send received bytes to all connected clients
        private void broadcast(byte[] data, int length) {
            for (Socket socket : clients) {
                try {
                    OutputStream outToClient = socket.getOutputStream();
                    //System.out.println("outToClient");
                    outToClient.write(data, 0, length);
                    //System.out.println("write");
                    outToClient.flush();
                    //System.out.println("flush");
                } catch (IOException e) {
                	//System.out.println("broadcast");
                    e.printStackTrace();
                }
            }
        }
    }
}