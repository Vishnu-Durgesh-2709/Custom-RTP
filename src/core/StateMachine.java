package core;


public class StateMachine {

    enum HandshakeState {
        CLOSED,
        SYN_SENT,
        SYN_RECEIVED,
        ESTABLISHED
    }
    private HandshakeState currentState;
    private int expectedSeqNo;
    private int expectedAckNo;

    public StateMachine() {
        this.currentState = HandshakeState.CLOSED;
    }

    public void processPacket(Packet packet) {
        switch (currentState) {
//            case CLOSED:
//                if (packet.syn && !packet.ack) {
//                    currentState = HandshakeState.SYN_RECEIVED;
//                    expectedSeqNo = packet.seqNo + 1;
//                    System.out.println("Received SYN, moving to SYN_RECEIVED");
//                }
//                break;
//
//            case SYN_SENT:
//                if (packet.syn && packet.ack && packet.ackNo == expectedSeqNo) {
//                    currentState = HandshakeState.ESTABLISHED;
//                    System.out.println("Received SYN-ACK, handshake complete.");
//                }
//                break;
//
//            case SYN_RECEIVED:
//                if (packet.ack && packet.ackNo == expectedSeqNo) {
//                    currentState = HandshakeState.ESTABLISHED;
//                    System.out.println("Received ACK, handshake complete.");
//                }
//                break;

            case ESTABLISHED:
                System.out.println("Connection already established.");
                break;
        }
    }

    public void initiateHandshake(int initialSeqNo) {
        currentState = HandshakeState.SYN_SENT;
        expectedSeqNo = initialSeqNo + 1;
        System.out.println("Sending SYN, moving to SYN_SENT.");
    }

    public HandshakeState getCurrentState() {
        return currentState;
    }
}
