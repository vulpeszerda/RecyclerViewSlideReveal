package com.vulpeszerda.recyclerviewanimation

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item.view.*

/**
 * Created by vulpes on 2017. 6. 27..
 */
class Adapter(private val slideRevealHelper: SlideRevealHelper)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = ArrayList<String>()

    var visible: Boolean = false
        set(value) {
            field = value
            slideRevealHelper.visible = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.create(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        val holder = viewHolder as? ViewHolder ?: return
        holder.bind(items[position])
        slideRevealHelper.onBindViewHolder(holder, position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        slideRevealHelper.onAttachedToRecyclerView(recyclerView)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder?) {
        super.onViewAttachedToWindow(holder)
        slideRevealHelper.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder?) {
        slideRevealHelper.onViewDetachedFromWindow(holder)
        super.onViewDetachedFromWindow(holder)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        slideRevealHelper.onDetachedFromRecyclerView(recyclerView)
        super.onDetachedFromRecyclerView(recyclerView)
    }


    fun setItems(items: List<String>) {
        val result = DiffUtil.calculateDiff(DiffCallback(this.items, items))
        this.items.clear()
        this.items.addAll(items)
        result.dispatchUpdatesTo(this)
    }

    class ViewHolder(itemView: View) : SlideRevealHelper.ViewHolder(itemView) {

        fun bind(text: String) {
            itemView.text.text = text
        }

        companion object {
            @JvmStatic
            fun create(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
                return ViewHolder(inflater.inflate(R.layout.item, parent, false))
            }
        }
    }

    private class DiffCallback(private val prev: List<String>,
                               private val curr: List<String>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return prev[oldItemPosition] == curr[newItemPosition]
        }

        override fun getOldListSize(): Int {
            return prev.size
        }

        override fun getNewListSize(): Int {
            return curr.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return prev[oldItemPosition] == curr[newItemPosition]
        }

    }
}