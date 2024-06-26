package com.example.myapplication.chats

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.MessageBodyBinding
import com.example.myapplication.elements.ItemChat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageListAdapter(private val userId:String, private val recyclerView: RecyclerView): RecyclerView.Adapter<MessageListAdapter.MessageHolder>(){

   private val listOfMessage = ArrayList<MessageInChat>().asReversed()




    inner class MessageHolder(item: View):RecyclerView.ViewHolder(item){
        val binding = MessageBodyBinding.bind(item)

        fun bind(messageInChat: MessageInChat){
            binding.textMessage.text = messageInChat.textOfMessage
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedDate = sdf.format(Date(messageInChat.dataTime))
            binding.dataSend.text = formattedDate
            val layoutParams = binding.cardMes.layoutParams as ConstraintLayout.LayoutParams

            if(messageInChat.idSender == userId){

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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageListAdapter.MessageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_body,parent,false)
        return MessageHolder(view)
    }

    override fun onBindViewHolder(holder: MessageListAdapter.MessageHolder, position: Int) {
        holder.bind(listOfMessage[position])
    }

    override fun getItemCount(): Int {
        return listOfMessage.size
    }
    fun addData(data: MessageInChat) {
        listOfMessage.add(data)
        listOfMessage.sortedByDescending { it.dataTime }
        val insertedPosition = listOfMessage.indexOf(data)

        notifyItemInserted(insertedPosition)
        recyclerView.smoothScrollToPosition(insertedPosition)

    }

    fun addListData(dataList: List<MessageInChat>) {
        listOfMessage.addAll(dataList)
        notifyDataSetChanged()
    }




}