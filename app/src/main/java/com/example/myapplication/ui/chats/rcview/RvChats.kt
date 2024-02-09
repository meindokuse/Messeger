package com.example.myapplication.ui.chats

import android.content.Context
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemChatBinding
import com.example.myapplication.models.ItemChat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RvChats(private val context: Context): RecyclerView.Adapter<RvChats.ChatHolder>() {
    val listOfChats = ArrayList<ItemChat>()

    val selectionChats = HashSet<Int>()

    fun isSelected(position: Int): Boolean{
        return selectionChats.contains(position)
    }

    fun toggleSelection(position: Int,actionMode: ActionMode){
        if(selectionChats.contains(position)){
            selectionChats.remove(position)
        } else {
            selectionChats.add(position)
        }
        actionMode.title = selectionChats.size.toString()
        notifyItemChanged(position)
    }

    fun clearSelection(){
        selectionChats.clear()
        notifyDataSetChanged()
    }
    fun deleteSelectionChats(){
        val selectedItems = getSelectedItems()
        for (position in selectedItems.sortedDescending()){
            listOfChats.removeAt(position)
            notifyItemRemoved(position)
        }
        clearSelection()

    }
    fun getSelectedItems(): List<Int> {
        return ArrayList(selectionChats)
    }

    fun outGetSelectedChats():List<ItemChat>{
        return listOfChats.filterIndexed{ index, _ -> isSelected(index) }
    }

    inner class ChatHolder(item: View):RecyclerView.ViewHolder(item){
        val binding = ItemChatBinding.bind(item)

        fun bind(chat: ItemChat, position: Int){
            binding.NameWhoWtiteText.text=chat.nickname
            binding.LastMesText.text=chat.mes_text

            Glide.with(context)
                .load(chat.avatar)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .override(300, 300)
            .into(binding.FotoWhoWrite)

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val formattedDate = sdf.format(Date(chat.mes_time))
            binding.DataTimeText.text = formattedDate

            val backgroundColor = if(isSelected(position)){
                ContextCompat.getColor(binding.root.context, R.color.grey_white)

            }else{
                ContextCompat.getColor(binding.root.context,android.R.color.transparent)

            }
            binding.root.setBackgroundColor(backgroundColor)

        }


    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatHolder {
        val chat = LayoutInflater.from(parent.context).inflate(R.layout.item_chat,parent,false)
        return ChatHolder(chat)
    }

    override fun getItemCount(): Int {
        return listOfChats.size
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        holder.bind(listOfChats[position],position)

    }

    fun setChats(chats: List<ItemChat>){
        listOfChats.clear()
        listOfChats.addAll(chats)
        notifyDataSetChanged()
    }
    fun addChat(chat: ItemChat){
        listOfChats.add(chat)
        notifyDataSetChanged()
    }
    fun removeChat(position: Int){
        listOfChats.removeAt(position)
        notifyDataSetChanged()
    }



}