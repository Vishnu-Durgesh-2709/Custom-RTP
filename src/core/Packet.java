package core;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Packet {
    int seqNo;
    int ackNo;
    byte flags;
    short windowSize;
    String message;

    public Packet(int seqNo, int ackNo, byte flags, short windowSize, String message) {
        this.seqNo = seqNo;
        this.ackNo = ackNo;
        this.flags = flags;
        this.windowSize = windowSize;
        this.message = message;
    }

    public byte[] serialize(){
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int size = message.length();
        int totalSize = Integer.BYTES + Integer.BYTES + Byte.BYTES + Short.BYTES + Integer.BYTES + messageBytes.length;

        ByteBuffer buffer = ByteBuffer.allocate(totalSize);

        buffer.putInt(this.seqNo);
        buffer.putInt(this.ackNo);
        buffer.put(flags);
        buffer.putShort(windowSize);
        buffer.putInt(size);
        buffer.put(messageBytes);

        return buffer.array();
    }

    public static Packet deserialize(byte[] data){

        ByteBuffer buffer = ByteBuffer.wrap(data);
        int seqNo = buffer.getInt();
        int ackNo = buffer.getInt();
        byte flags = buffer.get();
        short windowSize = buffer.getShort();

        int size = buffer.getInt();
        byte[] message = new byte[size];
        buffer.get(message);
        String msg = new String(message, StandardCharsets.UTF_8);

        return new Packet(seqNo, ackNo, flags, windowSize, msg);
    }

    @Override
    public String toString() {
        return String.format(
                "Packet(seqNo=%d, ackNo=%d, flags=%d, windowSize=%d, message='%s')",
                seqNo, ackNo, flags, windowSize, message
        );
    }

}