package org.main;

import com.google.gson.Gson;
import org.blockchain.Block;
import org.blockchain.Constants;
import org.blockchain.Range;

import java.io.BufferedReader;
import java.io.IOException;
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

    private boolean needsRange;

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
                else if("[".equals(inputLine))
                {
                    out.println(block.toJSON());
                }
                else if ("]".equals(inputLine)) {
                    goldenHashData=in.readLine();
                    Gson gson = new Gson();
                    Block goldenBlock = gson.fromJson(goldenHashData, Block.class);
                    if(!block.notGoldenHash(goldenBlock.getHash())) {
                        System.out.println("Goldenhash found");
                        System.out.println(goldenBlock.toJSON());
                        goldenHash = true;
                    }
                    out.println("]");
                }
                else if(":".equals(inputLine)){
                    needsRange=true;
                }
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

    public void setBlock(Block block) throws IOException {
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

    public boolean isNeedsRange() {
        return needsRange;
    }

    public void setNeedsRange(Range range) throws IOException{
        if(out!=null) {
            out.println(range.toJSON());
        }
        else if(clientSocket!=null){
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(range.toJSON());
        }
        this.needsRange = false;
    }

    public void sendBlockChangeRequest() throws IOException{
        if(out!=null) {
            out.println("]");
        }
        else if(clientSocket!=null){
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println("]");
        }
    }
    public void setNeedsRange(boolean needsRange) {
        this.needsRange = needsRange;
    }
}