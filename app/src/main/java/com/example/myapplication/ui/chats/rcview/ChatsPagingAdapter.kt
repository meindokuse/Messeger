package com.example.myapplication.ui.chats.rcview

import android.util.Log
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemChatBinding
import com.example.myapplication.models.ItemChat
import com.example.myapplication.models.MessageInChat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatsPagingAdapter(

): PagingDataAdapter<ItemChat, ChatsPagingAdapter.ChatHolder>(ChatsDiffItemCallback) {

    private val selectionChats = HashSet<Int>()

    fun isSelected(position: Int): Boolean {
        return selectionChats.contains(position)
    }

    fun getSelectedItems(): List<Int> {
        return ArrayList(selectionChats)
    }

    fun outGetSelectedChats(): List<ItemChat> {
        return snapshot().items.filterIndexed { index, _ -> isSelected(index) }
    }

    fun toggleSelection(position: Int, actionMode: ActionMode) {
        if (selectionChats.contains(position)) {
            selectionChats.remove(position)
        } else {
            selectionChats.add(position)
        }
        actionMode.title = selectionChats.size.toString()
        notifyItemChanged(position)
    }

    fun clearSelection() {
        selectionChats.clear()
        notifyDataSetChanged()
    }


    inner class ChatHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ItemChatBinding.bind(item)

        fun bind(chat: ItemChat?, position: Int) {
            binding.NameWhoWtiteText.text = chat?.nickname
            binding.LastMesText.text = chat?.mes_text
            Log.d("MyLog","отрисовка чата")

            Glide.with(binding.root.context)
                .load(chat?.avatar)
                .placeholder(R.drawable.loading)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .override(300, 300)
                .into(binding.FotoWhoWrite)

            val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
            val formattedDate = sdf.format(Date(chat!!.mes_time))
            binding.DataTimeText.text = formattedDate

            val backgroundColor = if (isSelected(position)) {
                ContextCompat.getColor(binding.root.context, R.color.grey_white)

            } else {
                ContextCompat.getColor(binding.root.context, android.R.color.transparent)

            }
            binding.root.setBackgroundColor(backgroundColor)
        }
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        holder.bind(getItem(position),position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        Log.d("MyLog","отрисовка чата")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat,parent,false)
        return ChatHolder(view)
    }
}

private object ChatsDiffItemCallback : DiffUtil.ItemCallback<ItemChat>() {

    override fun areItemsTheSame(oldItem: ItemChat, newItem: ItemChat): Boolean {
        return oldItem.mes_time == newItem.mes_time && oldItem.mes_text == newItem.mes_text
    }

    override fun areContentsTheSame(oldItem: ItemChat, newItem: ItemChat): Boolean {
        return oldItem == newItem
    }
}