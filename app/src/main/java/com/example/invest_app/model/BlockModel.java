package com.example.invest_app.model;

import androidx.annotation.Nullable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class BlockModel {
    private final int index;
    private int nonce;
    private final long timestamp;
    private String hash;
    private final String previousHash;
    private final String data;
    private static List<BlockModel> blocks = new ArrayList<>();

    public BlockModel(int index, long timestamp, @Nullable String previousHash, @Nullable String data) {
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.data = data;
        nonce = 0;
        hash = calculateHash(this);
    }

    public int getIndex() {
        return index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getData() {
        return data;
    }

    private String str() {
        return index + timestamp + previousHash + data + nonce;
    }

    public static String calculateHash(@Nullable BlockModel block) {
        if (block != null) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                String txt = block.str();
                final byte[] bytes = messageDigest.digest(txt.getBytes());
                final StringBuilder builder = new StringBuilder();
                for (final byte b : bytes) {
                    final String hex = String.format("%02x", b);
                    builder.append(hex);
                }
                return builder.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void mineBlock(int difficulty) {
        nonce = 0;
        while (!getHash().substring(0, difficulty).equals(addZeros(difficulty))) {
            nonce++;
            hash = calculateHash(this);
        }
    }

    private String addZeros(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append('0');
        }
        return builder.toString();
    }

    public static void addBlockToChain(String data, int difficulty) {
        int newIndex = blocks.size();
        String previousHash = blocks.get(newIndex - 1).getHash();
        BlockModel newBlock = new BlockModel(newIndex, System.currentTimeMillis(), previousHash, data);
        newBlock.mineBlock(difficulty);
        if (!blockExistsInFirebase(newBlock)) {
            blocks.add(newBlock);
            saveBlockToFirebase(newBlock);
        }
    }

    private static boolean blockExistsInFirebase(BlockModel block) {

        return false;
    }

    private static void saveBlockToFirebase(BlockModel block) {
        System.out.println("Block saved to storage - Index: " + block.getIndex() + ", Hash: " + block.getHash());
    }

    public static void main(String[] args) {
        addBlockToChain("Block 1 Data", 2);
    }
}