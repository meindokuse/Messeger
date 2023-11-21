package com.example.myapplication.profile.rcview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.elements.Event
import com.example.myapplication.R
import com.example.myapplication.databinding.EventItemBinding

class EventListAdapter():RecyclerView.Adapter<EventListAdapter.EventHolder>() {
    val contentList = ArrayList<Event>()
    class EventHolder(item: View):RecyclerView.ViewHolder(item){
        val binding = EventItemBinding.bind(item)
        fun bind(event: Event) {
            Log.d("MyLog","bind")
            binding.title.text = "Тема: ${event.title}"
            binding.description.text = event.desc

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        Log.d("MyLog","onCreate")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item,parent,false)
        return EventHolder(view)
    }

    override fun getItemCount(): Int {
        return contentList.size

    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        Log.d("MyLog","onBind")
        holder.bind(contentList[position])
    }
    fun addEvent(DataEventModel:Event){
        Log.d("MyLog","addEvent")
        contentList.add(DataEventModel)
        notifyDataSetChanged()
    }
    fun addListEvent(events: List<Event>){
        contentList.addAll(events)
        notifyDataSetChanged()
    }
}