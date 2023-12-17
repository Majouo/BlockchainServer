package org.blockchain;

import java.util.ArrayList;
import java.util.List;

public class Miner {

    private double reward;

    private Block currentBlock;
    private long nonce;

    private long totalMines;
    private int availableProcessors;


    public Miner(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public void mine(Block block, BlockChain blockChain) throws Exception {

        List<Thread> threads=new ArrayList<>();
        this.currentBlock=block;
        totalMines=0;
        nonce=0;

        for (int i = 0; i < availableProcessors; i++) {
            Thread thread = new Thread(() -> {
                long localMines=0;
                while(!currentBlock.isGoldenHash()) {
                    MiningRange miningRange = getNonceRange();
                    for(long n=miningRange.getStart();n<miningRange.getEnd();n++)
                    {
                        currentBlock.generateHash(n);
                        localMines++;
                        if(currentBlock.isGoldenHash())
                        {
                            passMines(localMines);
                            return;
                        }
                    }
                }
                passMines(localMines);
                return;
            });
            threads.add(thread);
        }

        for (Thread thread:threads) {
            thread.start();
        }
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        }catch (Exception ex)
        {
            throw new Exception(ex.getMessage());
        }

        System.out.println(block+" has just mined...");
        System.out.println("Hash is: "+block.getHash()+ " Tries: "+nonce);
        //appending the block to the blockchain
        blockChain.addBlock(block);
        //calculating the reward
        reward+=Constants.MINER_REWARD;
    }

    // So miners will generate hash values until they find the right hash.
    //that matches with DIFFICULTY variable declared in class Constant
    public boolean notGoldenHash(Block block) {

        String leadingZeros = new String(new char[Constants.DIFFICULTY]).replace('\0', '0');

        return !block.getHash().substring (0, Constants.DIFFICULTY).equals (leadingZeros);
    }

    public synchronized void passMines(long minedNonces)
    {
        this.totalMines+=minedNonces;
    }
    public long getTotalMines()
    {
        return this.totalMines;
    }
    public synchronized MiningRange getNonceRange()
    {
        long temp = nonce;
        nonce+=10000;
        return new MiningRange(temp,nonce);
    }
    public double getReward() {
        return this.reward;
    }
}
