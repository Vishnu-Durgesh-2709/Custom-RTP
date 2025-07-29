package core;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Packet {
    int seqNo;
    int ackNo;
    byte flags;
    short windowSize;

    public byte[] serializeBytes(String message){
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

    public String deserializeBytes(byte[] data){

        ByteBuffer buffer = ByteBuffer.wrap(data);
        int seqNo = buffer.getInt();
        int ackNo = buffer.getInt();
        byte flag = buffer.get();
        short windowSize = buffer.getShort();

        int size = buffer.getInt();
        byte[] message = new byte[size];
        buffer.get(message);

        return new String(message, StandardCharsets.UTF_8);
    }
}