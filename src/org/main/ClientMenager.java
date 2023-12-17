package org.main;

import com.google.gson.Gson;
import org.blockchain.Block;
import org.blockchain.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientMenager extends Thread {
    private List<ClientHandler> clientHandlerList;
    private Block block;
    private int id;

    public  ClientMenager(){
        this.clientHandlerList = new ArrayList<>();
        this.id=0;
        this.block= new Block(id, "transaction", Constants.GENESIS_PREV_HASH);
    }

    private void refreshBlock()
    {
        int i = 0;
        for (ClientHandler ch : clientHandlerList) {
            if (!ch.isAlive()) {
                clientHandlerList.remove(i);
                continue;
            }
            try {
                ch.setBlock(this.block);
            }
            catch (IOException e){
                clientHandlerList.remove(i);
            }
            i++;
        }
    }
    public void run(){
        while (true) {
            int i = 0;
            for (ClientHandler ch : clientHandlerList) {
                if (!ch.isAlive()) {
                    clientHandlerList.remove(i);
                    continue;
                }
                if (ch.isGoldenHash()) {
                    Gson gson = new Gson();
                    this.block = gson.fromJson(ch.getGoldenHashData(),Block.class);
                    ch.setGoldenHash(false);
                    refreshBlock();
                    break;
                }
                i++;
            }
        }
    }
    public List<ClientHandler> getClientHandlerList() {
        return clientHandlerList;
    }

    public void addClientHandler(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(socket,this.block);
        clientHandler.start();
        this.clientHandlerList.add(clientHandler);
    }
}
