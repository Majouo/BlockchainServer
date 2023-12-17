package org.blockchain;

import java.util.Date;

public class Block {
    private int id;
    private long timeStamp;
    //the variable hash will contain the hash of the block
    private String hash;
    //The previousHash variable contains the hash of the previous block
    private String previousHash;
    private String transaction;

    private long nonce;

    private boolean goldenHash=false;

    public Block(int id, String transaction, String previousHash) {

        this.id = id;

        this.transaction = transaction;

        this.previousHash = previousHash;

        this.timeStamp = new Date().getTime();

        this.hash=null;

        generateHash();

    }

    public void generateHash(long nonce) {

        String dataToHash = Integer.toString(id) + previousHash + Long.toString(timeStamp) + Long.toString(nonce) + transaction.toString();

        String hashValue = SHA256Helper.generateHash(dataToHash);

        if(!notGoldenHash(hashValue))
        {
            this.hash = hashValue;
            this.nonce=nonce;
            this.goldenHash=true;
        }

    }

    public boolean notGoldenHash(String hash) {

        String leadingZeros = new String(new char[Constants.DIFFICULTY]).replace('\0', '0');

        return !hash.substring (0, Constants.DIFFICULTY).equals (leadingZeros);
    }

    public void generateHash() {

        String dataToHash = Integer.toString(id) + previousHash + Long.toString(timeStamp) + Long.toString(0) + transaction.toString();

        String hashValue = SHA256Helper.generateHash(dataToHash);

        this.hash = hashValue;

    }


    public String getHash() {

        return this.hash;

    }

    public void setHash(String hash) {

        this.hash = hash;

    }

    public String getPreviousHash() {

        return this.previousHash;

    }

    public void setPreviousHash(String previousHash) {

        this.previousHash = previousHash;
    }
    public boolean isGoldenHash()
    {
        return this.goldenHash;
    }

    @Override

    public String toString() {

        return Integer.toString(id) + previousHash + Long.toString(timeStamp) + Long.toString(nonce) + transaction.toString();

    }
}
