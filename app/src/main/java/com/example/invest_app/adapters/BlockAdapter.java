package com.example.invest_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invest_app.R;
import com.example.invest_app.holders.RecyclerViewHolder;
import com.example.invest_app.model.BlockModel;

import java.util.Date;
import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private final List<BlockModel> blocks;
    private final Context mContext;
    private int lastPosition = -1;
    public BlockAdapter(@NonNull Context context, @Nullable List<BlockModel> blocks) {
        this.mContext = context;
        this.blocks = blocks;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder viewHolder, int position) {
        viewHolder.txtIndex.setText(String.format(
                mContext.getString(R.string.block_number), blocks.get(position).getIndex()));
        viewHolder.txtPreviousHash.setText(blocks.get(position).getPreviousHash() != null ?
                blocks.get(position).getPreviousHash() : "Null");
        viewHolder.txtTimestamp.setText(String.valueOf(new Date(blocks.get(position).getTimestamp())));
        viewHolder.txtData.setText(blocks.get(position).getData());
        viewHolder.txtHash.setText(blocks.get(position).getHash());

        setAnimation(viewHolder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    @Override
    public int getItemCount() {
        return blocks == null ? 0 : blocks.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_block_data;
    }
}

