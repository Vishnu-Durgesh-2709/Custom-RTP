package core;

import java.util.*;

public class Message {
    StringBuilder message;
    ArrayList<String> blocks;
    static int blockSize = 1024;

    void getMessage(){
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

    void setMessage(String encryptedMessage){
        message = new StringBuilder(encryptedMessage);
    }

    void splitMessage(){
        System.out.println(message.length());
        System.out.println(blockSize);
        System.out.println((message.length() + blockSize - 1) / blockSize);
        blocks = new ArrayList<String>((message.length() + blockSize - 1) / blockSize );
        for(int offset = 0; offset < message.length(); offset += blockSize){
            blocks.add(message.substring(offset, Math.min(offset + blockSize, message.length() - 1)));
        }
    }

    public static void main(String[] args){
        Message obj = new Message();
        obj.getMessage();
        obj.splitMessage();
        for(int i = 0; i < obj.blocks.size(); i++){
            System.out.println("Block " + (i+1) + " : " + obj.blocks.get(i));
        }
    }
}
