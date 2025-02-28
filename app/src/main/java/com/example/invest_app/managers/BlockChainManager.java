package com.example.invest_app.managers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.invest_app.adapters.BlockAdapter;
import com.example.invest_app.model.BlockModel;

import java.util.ArrayList;
import java.util.List;

public class BlockChainManager {
    private final int difficulty;
    private final List<BlockModel> blocks;
    public final BlockAdapter adapter;
    private final BlockChainDataManager blockchainDataManager;

    public BlockChainManager(@NonNull Context context, int difficulty) {
        this.difficulty = difficulty;
        this.blocks = new ArrayList<>();
        this.blockchainDataManager = new BlockChainDataManager(context);
        List<BlockModel> savedBlocks = blockchainDataManager.loadBlockChain();

        if (savedBlocks != null) {
            blocks.addAll(savedBlocks);
        } else {
            createAndAddGenesisBlock();
        }

        adapter = new BlockAdapter(context, blocks);
    }

    private BlockModel latestBlock() {
        return blocks.get(blocks.size() - 1);
    }

    public BlockModel newBlock(String data) {
        BlockModel latestBlock = latestBlock();
        return new BlockModel(latestBlock.getIndex() + 1, System.currentTimeMillis(),
                latestBlock.getHash(), data);
    }

    public void addBlock(BlockModel block) {
        if (block != null) {
            block.mineBlock(difficulty);
            blocks.add(block);
            blockchainDataManager.saveBlockChain(blocks);
        }
    }

    private boolean isFirstBlockValid() {
        BlockModel firstBlock = blocks.get(0);
        if (firstBlock.getIndex() != 0) {
            return false;
        }

        if (firstBlock.getPreviousHash() != null) {
            return false;
        }

        return firstBlock.getHash() != null &&
                BlockModel.calculateHash(firstBlock).equals(firstBlock.getHash());
    }

    private boolean isValidNewBlock(@Nullable BlockModel newBlock, @Nullable BlockModel previousBlock) {
        if (newBlock != null && previousBlock != null) {
            if (previousBlock.getIndex() + 1 != newBlock.getIndex()) {
                return false;
            }

            if (newBlock.getPreviousHash() == null ||
                    !newBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }

            return newBlock.getHash() != null &&
                    BlockModel.calculateHash(newBlock).equals(newBlock.getHash());
        }

        return false;
    }

    public boolean isBlockChainValid() {
        if (!isFirstBlockValid()) {
            return false;
        }

        for (int i = 1; i < blocks.size(); i++) {
            BlockModel currentBlock = blocks.get(i);
            BlockModel previousBlock = blocks.get(i - 1);

            if (!isValidNewBlock(currentBlock, previousBlock)) {
                return false;
            }
        }

        return true;
    }

    private void createAndAddGenesisBlock() {
        BlockModel genesisBlock = new BlockModel(0, System.currentTimeMillis(), null, "Genesis Block");
        genesisBlock.mineBlock(difficulty);
        blocks.add(genesisBlock);
        blockchainDataManager.saveBlockChain(blocks);
    }
}