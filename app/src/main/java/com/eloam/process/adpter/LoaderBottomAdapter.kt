package com.eloam.process.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eloam.process.R
import kotlinx.android.synthetic.main.item_view_log_loader.view.*

class LoaderBottomAdapter(
    private val retry: () -> Unit

) :
    LoadStateAdapter<LoaderBottomAdapter.MyViewHolder>() {

    override fun onBindViewHolder(holder: MyViewHolder, loadState: LoadState) {
        MyViewHolder.bind(loadState, holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MyViewHolder {
        return MyViewHolder.getView(parent, retry)
    }


    class MyViewHolder(view: View, retry: () -> Unit) : RecyclerView.ViewHolder(view) {

        init {
            view.findViewById<Button>(R.id.btnRetry).setOnClickListener {
                retry()
            }
        }

        companion object {

            fun bind(
                loadState: LoadState,
                holder: MyViewHolder
            ) {

                when (loadState) {
                    is LoadState.Loading -> {
                        holder.itemView.mlLoader.transitionToEnd()
                    }
                    else -> {
                        holder.itemView.mlLoader.transitionToStart()
                    }
                }

            }


            fun getView(parent: ViewGroup, retry: () -> Unit): MyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.item_view_log_loader, parent, false)
                return MyViewHolder(view, retry)

            }
        }


    }

}

