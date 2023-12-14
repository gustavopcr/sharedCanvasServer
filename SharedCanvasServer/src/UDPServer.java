import java.net.*;

public class UDPServer implements Runnable {
    private DatagramSocket serverSocket;
    private volatile boolean isRunning = true;

    public void stopServer() {
        isRunning = false;
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new DatagramSocket(9876); // Replace with your desired port
            byte[] receiveData = new byte[1024];
            System.out.println("Server started...");

            while (isRunning) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // Process received message - add client to the list of online users
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                System.out.println("Received from client at " + clientAddress + ":" + clientPort);

                // Code to maintain a list of online users, store their address and port
                
                // Reply to client with acknowledgment if needed
                String ack = "ACK";
                byte[] sendData = ack.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
            }
        } catch (Exception e) {
            if (!(e instanceof SocketException && !isRunning)) {
                e.printStackTrace();
            }
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        }
    }
}