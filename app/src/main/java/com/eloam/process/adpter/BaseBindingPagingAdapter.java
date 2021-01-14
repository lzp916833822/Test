package com.eloam.process.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.eloam.process.data.entity.DataInfo;

import org.jetbrains.annotations.NotNull;

import static androidx.recyclerview.widget.RecyclerView.ViewHolder;


public abstract class BaseBindingPagingAdapter<M, B extends ViewDataBinding> extends PagingDataAdapter<DataInfo,RecyclerView.ViewHolder>{
    protected Context context;
    protected ObservableArrayList<M> items;
    protected ListChangedCallback itemsChangeCallback;


    public BaseBindingPagingAdapter(Context context,@NotNull DiffUtil.ItemCallback<DataInfo> diffCallback) {
        super(diffCallback);
        this.context = context;
        this.items = new ObservableArrayList<>();
        this.itemsChangeCallback = new ListChangedCallback();
    }

    public ObservableArrayList<M> getItems() {
        return items;
    }

    public class BaseBindingViewHolder extends ViewHolder {
        public BaseBindingViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        B binding = DataBindingUtil.inflate(LayoutInflater.from(this.context), this.getLayoutResId(viewType), parent, false);
        return new BaseBindingViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        B binding = DataBindingUtil.getBinding(holder.itemView);
        this.onBindItem(binding, this.items.get(position), position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.items.addOnListChangedCallback(itemsChangeCallback);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.items.removeOnListChangedCallback(itemsChangeCallback);
    }

    //region 处理数据集变化
    protected void onChanged(ObservableArrayList<M> newItems) {
        resetItems(newItems);
        notifyDataSetChanged();
    }

    protected void onItemRangeChanged(ObservableArrayList<M> newItems, int positionStart, int itemCount) {
        resetItems(newItems);
        notifyItemRangeChanged(positionStart, itemCount);
    }

    protected void onItemRangeInserted(ObservableArrayList<M> newItems, int positionStart, int itemCount) {
        resetItems(newItems);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    protected void onItemRangeMoved(ObservableArrayList<M> newItems) {
        resetItems(newItems);
        notifyDataSetChanged();
    }

    protected void onItemRangeRemoved(ObservableArrayList<M> newItems, int positionStart, int itemCount) {
        resetItems(newItems);
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    protected void resetItems(ObservableArrayList<M> newItems) {
        this.items = newItems;
    }

    @LayoutRes
    protected abstract int getLayoutResId(int viewType);

    protected abstract void onBindItem(B binding, M item, int position);

    private class ListChangedCallback extends ObservableArrayList.OnListChangedCallback<ObservableArrayList<M>> {
        @Override
        public void onChanged(ObservableArrayList<M> newItems) {
            BaseBindingPagingAdapter.this.onChanged(newItems);
        }

        @Override
        public void onItemRangeChanged(ObservableArrayList<M> newItems, int i, int i1) {
            BaseBindingPagingAdapter.this.onItemRangeChanged(newItems, i, i1);
        }

        @Override
        public void onItemRangeInserted(ObservableArrayList<M> newItems, int i, int i1) {
            BaseBindingPagingAdapter.this.onItemRangeInserted(newItems, i, i1);
        }

        @Override
        public void onItemRangeMoved(ObservableArrayList<M> newItems, int i, int i1, int i2) {
            BaseBindingPagingAdapter.this.onItemRangeMoved(newItems);
        }

        @Override
        public void onItemRangeRemoved(ObservableArrayList<M> sender, int positionStart, int itemCount) {
            BaseBindingPagingAdapter.this.onItemRangeRemoved(sender, positionStart, itemCount);
        }
    }
}
