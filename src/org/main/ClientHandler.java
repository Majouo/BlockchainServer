package org.main;

import org.blockchain.Block;
import org.blockchain.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Block block;
    private boolean goldenHash;

    private String goldenHashData;

    public ClientHandler(Socket socket,Block block)  {
        this.clientSocket = socket;
        this.block=block;
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (".".equals(inputLine)) {
                    out.println("bye");
                    break;
                }
                if ("g".equals(inputLine)) {
                    goldenHash=true;
                }
                out.println(inputLine);
            }

            in.close();
            out.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            if(out!=null)
                out.close();
        }

    }
    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public boolean isGoldenHash() {
        return goldenHash;
    }

    public void setGoldenHash(boolean goldenHash) {
        this.goldenHash = goldenHash;
    }

    public String getGoldenHashData(){
        return goldenHashData;
    }
}