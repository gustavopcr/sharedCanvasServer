import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<Socket> clients = new ArrayList<>();
    public static ArrayList<byte[]> drawingsBytes = new ArrayList<>();
    
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
                //System.out.println("ip: " + clientHandler.getIP());
                
                for( byte[] buf : drawingsBytes) {
                	//ClientHandler.sendDataOneClient(buf, buf.length, clientSocket);
                	int[] teste = bytesToIntArray(buf);
                	System.out.println("x: " + teste[0] + " y: " + teste[1]);
                }
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
        
        public InetAddress getIP() {
        	return this.clientSocket.getInetAddress();
        }
        
        @Override
        public void run() {
            try {
                byte[] buffer = new byte[4*4]; // Adjust buffer size as needed
                int bytesRead;
                while ((bytesRead = inFromClient.read(buffer)) != -1) {
                    // Process received bytes (buffer) as needed
                    //System.out.println("Received bytes from client.");
                    int[] teste = bytesToIntArray(buffer);
                    //System.out.println("Received bytes from client. x: " + teste[0] + " y: " + teste[1]);
                    drawingsBytes.add(buffer); // sync
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
                    
                    System.out.println(socket.getInetAddress());
                    outToClient.write(data, 0, length);
                    //System.out.println("write");
                    outToClient.flush();
                	DatagramSocket clientSocket = new DatagramSocket(); // Create a DatagramSocket
                	String message = "Hello from UDP client!";
                    byte[] sendData = message.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, socket.getInetAddress(), 9008);
                    //System.out.println("flush");
                } catch (IOException e) {
                	//System.out.println("broadcast");
                    e.printStackTrace();
                }
            }
        }
        
        
        private static void sendDataOneClient(byte[] data, int length, Socket socket) {
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
    
    private static int[] bytesToIntArray(byte[] bytes) {
        int[] integers = new int[bytes.length / Integer.BYTES];
        for (int i = 0; i < bytes.length; i += Integer.BYTES) {
            integers[i / Integer.BYTES] = byteArrayToInt(bytes, i);
        }
        return integers;
    }
    private static int byteArrayToInt(byte[] bytes, int offset) {
        return (bytes[offset] << 24) | ((bytes[offset + 1] & 0xFF) << 16) | ((bytes[offset + 2] & 0xFF) << 8) | (bytes[offset + 3] & 0xFF);
    }
    
    public static class Drawing{
    	int x, y;
    	public Drawing(int x, int y) {
    		this.x = x;
    		this.y = y;
    	}
    }
    

}