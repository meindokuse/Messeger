package com.example.myapplication

import android.animation.ObjectAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.databinding.EventItemBinding
import com.example.myapplication.databinding.UserForChooseBinding
import com.example.myapplication.elements.Event
import com.example.myapplication.elements.UserForChoose
import com.example.myapplication.profile.rcview.ItemListener


class UniversalAdapter<T>(val itemListener: ItemListener, val key: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val contentList = ArrayList<T>()

    companion object {
        const val EVENT_KEY = "event"
        const val USER_KEY = "user"
    }

    val selectionChats = HashSet<Int>()

    fun isSelected(position: Int): Boolean{
        return selectionChats.contains(position)
    }

    fun toggleSelection(position: Int){
        Log.d("MyLog","toggleSelection")
        if(selectionChats.contains(position)){
            selectionChats.remove(position)
        } else {
            selectionChats.add(position)
        }
        notifyItemChanged(position)
    }

    fun clearSelection(){
        selectionChats.clear()
        notifyDataSetChanged()
    }
//    fun deleteSelectionChats(){
//        val selectedItems = getSelectedItems()
//        for (position in selectedItems.sortedDescending()){
//            contentList.removeAt(position)
//            notifyItemRemoved(position)
//        }
//        clearSelection()
//
//    }
    fun getSelectedItems(): List<Int> {
        return ArrayList(selectionChats)
    }
    fun getAllItems():ArrayList<T>{
        return contentList
    }

    inner class EventHolder(item: View):RecyclerView.ViewHolder(item){

        val binding = EventItemBinding.bind(item)

        fun bind(event: Event) {
            Log.d("MyLog","bind")
            binding.title.text = "Тема: ${event.title}"
            binding.description.text = event.desc
            binding.deleteButton.setOnClickListener {
                itemListener.onClick(adapterPosition)
            }

        }
    }

    inner class UserForMessege(item: View):RecyclerView.ViewHolder(item){
        val binding = UserForChooseBinding.bind(item)
        val checkBox = binding.checkBox
        val fadeInAnimator = ObjectAnimator.ofFloat(checkBox, "alpha", 0f, 1f).apply {
            duration = 500
        }
        fun bind(userForChoose: UserForChoose,position: Int){
            Glide.with(binding.root.context)
                .load(userForChoose.foto)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(binding.avatarView)

            binding.nickname.text = userForChoose.nickname

            binding.avatarView.setOnClickListener {
                itemListener.onClick(position)
            }

           if(isSelected(position)){
               fadeInAnimator.start()

               checkBox.visibility = View.VISIBLE

            }else {
               checkBox.alpha = 1f
               checkBox.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (key) {
            EVENT_KEY -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
                EventHolder(view)
            }
            USER_KEY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_for_choose, parent, false)
                UserForMessege(view)
            }

            else -> throw IllegalArgumentException("Unknown key: $key")
        }
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is UniversalAdapter<*>.EventHolder) {
            (contentList[position] as? Event)?.let { holder.bind(it) }
        } else if (holder is UniversalAdapter<*>.UserForMessege) {
            (contentList[position] as? UserForChoose)?.let { holder.bind(it,position) }
        }
    }

    fun addData(data: T) {
        contentList.add(data)
        contentList.reverse()
        notifyDataSetChanged()
    }

    fun addListData(dataList: List<T>) {
        contentList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        contentList.removeAt(position)
        notifyDataSetChanged()
        notifyItemRemoved(position)
    }

    //FOR SEARCHING
    fun updateList(newList: ArrayList<T>){
        contentList.clear()
        contentList.addAll(newList)
        notifyDataSetChanged()
    }
}
