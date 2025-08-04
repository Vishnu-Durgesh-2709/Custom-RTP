package server;

import core.MessageManager;
import core.ReliableChannel;

public class ReliableUDPServer {
    public static void main(String[] args) {
        try {
            MessageManager messageManager = new MessageManager();
            ReliableChannel channel = new ReliableChannel(messageManager, 9876);
            channel.receive();
            System.out.println("Received Data : " + messageManager.message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

