package core;

import java.util.*;

public class MessageManager{
    public StringBuilder message;
    ArrayList<String> blocks;
    static int blockSize = 512;

    public MessageManager(){
        this.message = new StringBuilder();
    }

    public void getMessage(){
        System.out.println("Enter your Input : ");
        Scanner userInput = new Scanner(System.in);
        message = new StringBuilder();
        while(userInput.hasNextLine()){
            String line = userInput.nextLine();
            if(line.equalsIgnoreCase("EOF")){
                break;
            }
            if(message != null){
                message.append(line);
            }
        }
        userInput.close();
        System.out.println("User Message : "  + message);
    }

//    void setMessage(String encryptedMessage){
//        message = new StringBuilder(encryptedMessage);
//    }

    public void splitMessage(){
        blocks = new ArrayList<String>((message.length() + blockSize - 1) / blockSize );
        for(int offset = 0; offset < message.length(); offset += blockSize){
            blocks.add(message.substring(offset, Math.min(offset + blockSize, message.length() - 1)));
        }
    }

    public void joinBlocks(List<String> receivedBlocks) {
        for (String block : receivedBlocks) {
            message.append(block);
        }
    }

    public List<String> getBlocks() {
        return blocks;
    }
}
