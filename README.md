- ## 🔹 Folder Structure
    - src/
    - ├── core/
    - │   ├── Packet.java
    - │   ├── Connection.java
    - │   ├── ReliableChannel.java
    - │   ├── FlowControl.java
    - │   ├── StateMachine.java
    - │   └── TimerManager.java
    - │
    - ├── server/
    - │   └── ReliableUDPServer.java
    - │
    - ├── client/
    - │   └── ReliableUDPClient.java
    - │
    - └── utils/
    - ─ Logger.java
    - └── Config.java
    -
- ## 🔹 core/Packet.java
  -
  | Component | Description                                                 |
  |-----------|-------------------------------------------------------------|
  | Class     | `Packet` — defines packet structure and serialization logic |
  
    | Attributes                                          | Description                            |
    |-----------------------------------------------------|----------------------------------------|
    | `int seqNum`                                        | Sequence number                        |
    | `int ackNum`                                        | Acknowledgment number                  |
    | `byte flags`                                        | SYN, ACK, FIN (bitfield)               |
    | `short windowSize`                                  | Flow control window                    |
    | `byte[] data`                                       | Payload                                |

    | Methods                                             | Description                            |
    |-----------------------------------------------------|----------------------------------------|
    | `byte[] toBytes()`                                  | Serialize packet to byte array         |
    | `static Packet fromBytes(byte[])`                   | Parse from raw bytes                   |
    | `DatagramPacket toDatagramPacket(InetAddress, int)` | Wrap into UDP packet (optional helper) |
- ## 🔹 core/Connection.java

  | Component | Description                                                  |
  |-----------|--------------------------------------------------------------|
  | Class     | `Connection` — Manages connection state, handshake, teardown |
-
| Attributes                        |
  |-----------------------------------|
| `DatagramSocket socket`           |
| `InetAddress peerAddress`         |
| `int peerPort`                    |
| `StateMachine stateMachine`       |
| `ReliableChannel reliableChannel` |
-
| Methods                      |
  |------------------------------|
| `void initiateHandshake()`   |
| `void acceptHandshake()`     |
| `void sendData(byte[] data)` |
| `void receiveData()`         |
| `void closeConnection()`     |

---
- ## 🔹 core/ReliableChannel.java

  | Component | Description                                                     |
    |-----------|-----------------------------------------------------------------|
  | Class     | `ReliableChannel` — Handles sending with ACK tracking, timeouts |
-
| Attributes                            |
  |---------------------------------------|
| `Map<Integer, Packet> unackedPackets` |
| `ScheduledExecutorService timerPool`  |
| `DatagramSocket socket`               |
-
| Methods                        |
  |--------------------------------|
| `void sendPacket(Packet p)`    |
| `void handleAck(Packet ack)`   |
| `void handleTimeouts()`        |
| `void forceResend(int seqNum)` |
- Attributes
  Map<Integer, Packet> unackedPackets  
  ScheduledExecutorService timerPool  
  DatagramSocket socket
- ## 🔹 core/FlowControl.java

  | Component | Description                                   |
    |-----------|-----------------------------------------------|
  | Class     | `FlowControl` — Sliding window implementation |
-
| Attributes                               |
  |------------------------------------------|
| `int base` — First unacknowledged seqNum |
| `int nextSeqNum`                         |
| `int windowSize`                         |
| `Map<Integer, Packet> windowBuffer`      |
-
| Methods                        |
  |--------------------------------|
| `boolean canSend()`            |
| `void slideWindow(int ackNum)` |
| `void bufferPacket(Packet p)`  |

---
- ## 🔹 core/StateMachine.java

  | Component | Description                                         |
    |-----------|-----------------------------------------------------|
  | Class     | `StateMachine` — Manages protocol state transitions |
-
| Attributes                                                       |
  |------------------------------------------------------------------|
| `State currentState` — Enum: CLOSED, SYN_SENT, ESTABLISHED, etc. |
-
| Methods                         |
  |---------------------------------|
| `void onEvent(String event)`    |
| `State getCurrentState()`       |
| `void setState(State newState)` |
-
- ## 🔹 core/TimerManager.java

  | Component | Description                                           |
    |-----------|-------------------------------------------------------|
  | Class     | `TimerManager` — Manages packet retransmission timers |
-
| Attributes                                |
  |-------------------------------------------|
| `Map<Integer, ScheduledFuture<?>> timers` |
| `ScheduledExecutorService scheduler`      |
-
| Methods                                      |
  |----------------------------------------------|
| `void startTimer(int seqNum, Runnable task)` |
| `void cancelTimer(int seqNum)`               |
| `void cancelAll()`                           |
-
- ## 🔹 server/ReliableUDPServer.java

  | Component | Description                                                           |
    |-----------|-----------------------------------------------------------------------|
  | Class     | `ReliableUDPServer` — Receives connections, handles data from clients |
-
| Attributes                    |
  |-------------------------------|
| `DatagramSocket serverSocket` |
-
| Methods                              |
  |--------------------------------------|
| `void start(int port)`               |
| `void handleClient(Connection conn)` |
| `Packet receivePacket()`             |

---
- ## 🔹 client/ReliableUDPClient.java

  | Component | Description                                          |
    |-----------|------------------------------------------------------|
  | Class     | `ReliableUDPClient` — Connects to server, sends data |
-
| Attributes                    |
  |-------------------------------|
| `DatagramSocket clientSocket` |
| `Connection connection`       |
-
| Methods                  |
  |--------------------------|
| `void connect()`         |
| `void send(byte[] data)` |
| `void close()`           |

---
- ## 🔹 utils/Logger.java

  | Component | Description                                          |
    |-----------|------------------------------------------------------|
  | Class     | `Logger` — Custom logger for consistent debug output |
-
| Methods                         |
  |---------------------------------|
| `static void info(String msg)`  |
| `static void error(String msg)` |
| `static void debug(String msg)` |

---
- ## 🔹 utils/Config.java

  | Component | Description                          |
    |-----------|--------------------------------------|
  | Class     | `Config` — Contains global constants |
-
| Constants             |
  |-----------------------|
| `int MAX_PACKET_SIZE` |
| `int TIMEOUT_MS`      |
| `int WINDOW_SIZE`     |
| `int SERVER_PORT`     |
| `int CLIENT_PORT`     |
-
- ## 🧠 Notes
- Each module is independent and testable.
- Connection logic is centralized in `Connection.java` — this helps separate network I/O from protocol behavior.
- Protocol logic should evolve in sync with the spec (`PROTOCOL.md`).!