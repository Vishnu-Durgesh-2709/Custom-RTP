package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import core.Packet;


public class ReliableUDPServer {

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(9876);

        byte[] buffer = new byte[65535];

        System.out.println("Waiting for packet...");

        DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(udpPacket); // blocking

        byte[] receivedData = udpPacket.getData();
        Packet receivedPacket = Packet.deserialize(receivedData);

        System.out.println("Received Packet: " + receivedPacket);
        socket.close();
    }

}
