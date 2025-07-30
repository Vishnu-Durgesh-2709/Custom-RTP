package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import core.Packet;

public class ReliableUDPClient {

    public static void main(String[] args) throws Exception {

        // get message
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter message to send: ");
        String userMessage = scanner.nextLine();

        // encrypt message

        // Serialize message
        Packet packet = new Packet(1001, 2002, (byte) 1, (short) 512, userMessage);
        byte[] data = packet.serialize();

        // Reliable sending
        InetAddress receiverAddress = InetAddress.getByName("localhost");
        int receiverPort = 9876;

        DatagramPacket udpPacket = new DatagramPacket(data, data.length, receiverAddress, receiverPort);
        DatagramSocket socket = new DatagramSocket();
        socket.send(udpPacket);

        // Close Socket
        System.out.println("Packet sent.");
        socket.close();
    }

}
