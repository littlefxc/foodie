package com.fengxuechao.io.demo02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInputHandler implements Runnable {
    ChatClient client;
    public UserInputHandler(ChatClient chatClient) {
        this.client=chatClient;
    }
    @Override
    public void run() {
        BufferedReader read=new BufferedReader(
                new InputStreamReader(System.in)
        );
        while (true){
            try {
                String input=read.readLine();
                client.send(input);
                if(input.equals("quit")) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}