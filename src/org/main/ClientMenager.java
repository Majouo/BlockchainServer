package org.main;

import com.google.gson.Gson;
import org.blockchain.Block;
import org.blockchain.Constants;
import org.blockchain.Range;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientMenager extends Thread {
    private List<ClientHandler> clientHandlerList;
    private Block block;
    private int id;

    private long nonceRange;

    private long nonce;

    public  ClientMenager(){
        this.clientHandlerList = new ArrayList<>();
        this.id=0;
        this.block= new Block(id, "transaction", Constants.GENESIS_PREV_HASH);
        this.nonceRange=100000;
        this.nonce=0;
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
                ch.sendBlockChangeRequest();
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
            try {
                int i = 0;
                for (ClientHandler ch : clientHandlerList) {
                    if (!ch.isAlive()) {
                        clientHandlerList.remove(i);
                        continue;
                    }
                    if (ch.isGoldenHash()) {
                        Gson gson = new Gson();
                        Block goldenBlock = gson.fromJson(ch.getGoldenHashData(), Block.class);
                        id++;
                        nonce = 0;
                        this.block = new Block(id, "transaction", goldenBlock.getHash());
                        ch.setGoldenHash(false);
                        refreshBlock();
                        break;
                    }
                    if (ch.isNeedsRange()) {
                        Range range = new Range(nonce, nonce + nonceRange);
                        nonce += nonceRange;
                        try {
                            ch.setNeedsRange(range);
                        } catch (IOException e) {
                            clientHandlerList.remove(i);
                        }
                    }
                    i++;
                }
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            catch (Exception e){
                System.out.println(e.getMessage());
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
