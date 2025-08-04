package client;

import core.MessageManager;
import core.ReliableChannel;


public class ReliableUDPClient {
    public static void main(String[] args) {
        try {
            MessageManager messageManager = new MessageManager();
            messageManager.getMessage();
            messageManager.splitMessage();

            ReliableChannel channel = new ReliableChannel(messageManager, "localhost", 9876);
            channel.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
