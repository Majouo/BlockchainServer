package org.main;

import org.blockchain.Block;
import org.blockchain.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {

    public static void main(String[] args) {

        int port =4000;
        int id=0;
        ServerSocket serverSocket=null;
        ClientMenager clientMenager =new ClientMenager();
        Block block = new Block(id, "transaction", Constants.GENESIS_PREV_HASH);
        try {
            serverSocket = new ServerSocket(port);
            clientMenager.start();
            while (true)
            {
                clientMenager.addClientHandler(serverSocket.accept());
            }

        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        try {
            if(serverSocket!=null) {
                serverSocket.close();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}