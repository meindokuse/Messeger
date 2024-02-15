package com.example.myapplication.ui.messages.rcview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.MessageBodyBinding
import com.example.myapplication.models.MessageInChat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessagesPagingAdapter(
    private val userId:String
): PagingDataAdapter<MessageInChat, MessagesPagingAdapter.MessageHolder>(MessageDiffItemCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_body,parent,false)
        return MessageHolder(view)
    }
    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MessageHolder(item: View): RecyclerView.ViewHolder(item){
        val binding = MessageBodyBinding.bind(item)

        fun bind(messageInChat: MessageInChat?){
            binding.textMessage.text = messageInChat?.text
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedDate = sdf.format(Date(messageInChat!!.time))
            binding.dataSend.text = formattedDate
            Log.d("MyLog","bind message ${messageInChat.text}")
            val layoutParams = binding.cardMes.layoutParams as ConstraintLayout.LayoutParams

            if(messageInChat.id_sender == userId){

                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
                binding.cardMes.setCardBackgroundColor(ContextCompat.getColor(binding.cardMes.context,R.color.white))
                Log.d("MyLog","Это я")

            } else {
                // Сообщение не пользователя, отображать слева
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
                binding.cardMes.setCardBackgroundColor(ContextCompat.getColor(binding.cardMes.context,R.color.lite_blue))
                Log.d("MyLog","Это не я")

            }
        }
    }
}
private object MessageDiffItemCallback : DiffUtil.ItemCallback<MessageInChat>() {

    override fun areItemsTheSame(oldItem: MessageInChat, newItem: MessageInChat): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MessageInChat, newItem: MessageInChat): Boolean {
        return oldItem.message_id == newItem.message_id
    }
}