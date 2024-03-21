package com.example.myapplication.ui.chats.rcview

import android.util.Log
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingData
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
    suspend fun deleteSelectionChats() {
        val items = snapshot().toMutableList()
        selectionChats.sortedDescending().forEach { position ->
            items.removeAt(position)
        }
        submitData(PagingData.from(items.filterNotNull()))
        selectionChats.clear()
    }


    inner class ChatHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ItemChatBinding.bind(item)

        fun bind(chat: ItemChat?, position: Int) {
            binding.NameWhoWtiteText.text = chat?.nickname
            binding.LastMesText.text = chat?.mes_text
            Log.d("MyLog","отрисовка чата")

            Glide.with(binding.root.context)
                .load(chat?.avatar)
                .placeholder(R.drawable.profile_foro)
                .error(R.drawable.profile_foro)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .override(300, 300)
                .into(binding.FotoWhoWrite)

            val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
            val formattedDate = sdf.format(Date(chat!!.mes_time))
            binding.DataTimeText.text = formattedDate

            binding.chatBox.visibility = if (isSelected(position)) View.VISIBLE else View.GONE
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
        return oldItem.chat_id == newItem.chat_id
    }

    override fun areContentsTheSame(oldItem: ItemChat, newItem: ItemChat): Boolean {
        return oldItem == newItem
    }
}