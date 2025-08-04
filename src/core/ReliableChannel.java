package core;

import java.net.*;
import java.util.*;

public class ReliableChannel {
    private final DatagramSocket socket;
    private InetAddress remoteAddress;
    private int remotePort;
    private int seqNo;
    private List<String> blocks;
    private final MessageManager manager;

    // Sender constructor
    public ReliableChannel(MessageManager manager, String host, int port) throws Exception {
        this.remoteAddress = InetAddress.getByName(host);
        this.remotePort = port;
        this.socket = new DatagramSocket();
        this.seqNo = new Random().nextInt(Integer.MAX_VALUE);
        this.blocks = manager.getBlocks();
        this.manager = manager;
    }

    // Receiver constructor
    public ReliableChannel(MessageManager manager, int bindPort) throws Exception {
        this.socket = new DatagramSocket(bindPort);
        this.seqNo = new Random().nextInt(Integer.MAX_VALUE);
        this.manager = manager;
    }

    public void start() {
        try{
            send(blocks);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void send(List<String> blocks) throws Exception {
        int pointer = 0;
        short windowSize = 4;

        while (pointer < blocks.size()) {
            System.out.println("Initial Sequence number : " + seqNo);
            List<Packet> window = getPackets(blocks, windowSize, pointer);

            // Send current window
            System.out.println(window.size());
            for (int i = 0; i < window.size(); i++){
                Packet pkt = window.get(i);
                byte[] data = pkt.serialize();
                DatagramPacket dp = new DatagramPacket(data, data.length, remoteAddress, remotePort);
                socket.send(dp);
                if(i == window.size() - 1){
                    System.out.println("Sent Last Packet SeqNo: " + pkt.getSeqNo());
                }
            }

            // TODO : try to reduce timeout
            socket.setSoTimeout(500);
            boolean ackReceived;
            int ackNo = -1;

            try {
                byte[] buffer = new byte[65535];
                DatagramPacket ackPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(ackPacket);

                Packet ack = Packet.deserialize(ackPacket.getData());
                ackNo = ack.getAckNo();
                ackReceived = true;
                System.out.println("Received ACK for SeqNo: " + ackNo);

            } catch (SocketTimeoutException e) {
                System.out.println("Timeout. Resending window.");
                ackReceived = false;
            }

            if (ackReceived) {
                if (ackNo > seqNo && ackNo <= seqNo + windowSize) {
                    int offset = ackNo - seqNo;
                    pointer += offset;
                    seqNo += offset;
                } else if (ackNo == seqNo) {
                    System.out.println("Duplicate ACK or base not yet delivered. Waiting briefly.");
                    Thread.sleep(20);
                } else {
                    System.out.println("Invalid ACK or out-of-window. Resending window.");
                }
            }
        }

        System.out.println("All packets sent.");
        socket.close();
    }

    private List<Packet> getPackets(List<String> blocks, short windowSize, int pointer) {
        List<Packet> window = new ArrayList<>();

        for (int i = 0; i < windowSize && pointer + i < blocks.size(); i++) {

            boolean isLast = (pointer + i == blocks.size() - 1);
            boolean isFirst = (pointer + i == 0);

            byte flags = isLast ? Packet.FLAG_LAST : isFirst ? Packet.FLAG_FIRST : 0;

            String block = blocks.get(pointer + i);
            Packet packet = new Packet(seqNo + i, 0, flags, windowSize, block);
            window.add(packet);
        }
        return window;
    }

    public void receive() throws Exception {
        Map<Integer, Packet> receivedPackets = new HashMap<>();
        List<String> receivedData = new ArrayList<>();
        short windowSize = 4;

        int base = -1;
        int expectedSeqNo = -1;
        boolean lastPacketReceived = false;

        while (!lastPacketReceived) {
            // TODO : Initial sensitivity is high
            socket.setSoTimeout(3000);

            //TODO : Update logic for last round, where there needn't be windowSize packets
            for(int pack = 0; pack < windowSize; pack++){
                byte[] buffer = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                socket.receive(dp);

                remoteAddress = dp.getAddress();
                remotePort = dp.getPort();

                byte[] data = Arrays.copyOf(dp.getData(), dp.getLength());
                Packet pkt;
                try {
                    pkt = Packet.deserialize(data);
                } catch (Exception e) {
                    System.out.println("❌ Failed to deserialize packet. Ignoring.");
                    continue;
                }

                int seqNo = pkt.getSeqNo();
                byte flags = pkt.getFlags();

                if(pack == windowSize - 1){
                    System.out.println("\n📥 Received Last Packet SeqNo: " + seqNo);
                }

                // Initialize base and expectedSeqNo
                if (base == -1 && flags == Packet.FLAG_FIRST) {
                    base = seqNo;
                    expectedSeqNo = seqNo;
                    System.out.println("🔰 Initial base set to: " + base);
                }

                // Accept packets only within current window
                if (seqNo >= base && seqNo < base + windowSize) {
                    if (!receivedPackets.containsKey(seqNo)) {
                        receivedPackets.put(seqNo, pkt);
                        System.out.println("✅ Buffered Packet: " + seqNo);
                    } else {
                        System.out.println("⚠️ Duplicate Packet: " + seqNo);
                    }
                } else {
                    System.out.println("❌ Dropped Packet (beyond buffer limit): " + seqNo);
                }
            }

            // TODO : Won't work in unreliable environment
            // Deliver in-order packets from expectedSeqNo onward
            for(int i = 0; i < windowSize; i++){
                if(receivedPackets.containsKey(expectedSeqNo)){
                    Packet p = receivedPackets.remove(expectedSeqNo);
                    receivedData.add(p.getMessage());
                    System.out.println("📦 Delivered Packet: " + expectedSeqNo + " → " + p.getMessage());

                    if ((p.getFlags() & Packet.FLAG_LAST) != 0) {
                        lastPacketReceived = true;
                        System.out.println(" Last packet flag set at SeqNo: " + expectedSeqNo);
                    }
                    expectedSeqNo++;
                    base++;
                }else{
                    break;
                }
            }

            // Send ACK for expectedSeqNo (cumulative ACK)
            Packet ack = new Packet(0, expectedSeqNo, (byte) 1, (short) 0, "");
            byte[] ackData = ack.serialize();
            DatagramPacket ackPacket = new DatagramPacket(
                    ackData, ackData.length,
                    remoteAddress, remotePort
            );
            socket.send(ackPacket);
            System.out.println("📤 Sent ACK for SeqNo: " + expectedSeqNo);

        }

        manager.joinBlocks(receivedData);
        socket.close();
        System.out.println("📦 All packets received and joined.");
    }

}
