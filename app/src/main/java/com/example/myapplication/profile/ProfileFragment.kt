package com.example.myapplication.profile

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.profile.rcview.EventsAdapter
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.profile.rcview.ItemListener
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.viewmodel.MyViewModel


class ProfileFragment : Fragment() {

   private var EditEventTime = false

    val fragmentForEditEvents = FragmentForEditEvents()
    val editFragmentForProfile = EditFragmentForProfile()
    private var currentlyPlayingViewHolder: EventsAdapter.AudioPostViewHolder? = null


    private lateinit var binding:FragmentProfileBinding
    lateinit var adapter: EventsAdapter
    private val DataModel: MyViewModel by activityViewModels{
        MyViewModelFactory(LocalReposetoryHelper(requireContext()),requireActivity().application)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EventsAdapter(object: ItemListener{
            override fun onClickDelete(position: Int) {
                val event = adapter.getAllItems()[position]
                DataModel.DeleteEvent(event)
                adapter.removeItem(position)
            }

            override fun onClickStartListen(position: Int,mediaPlayer: MediaPlayer) {
                stopCurrentlyPlaying()
                val viewHolder = binding.postsRecyclerView.findViewHolderForAdapterPosition(position) as? EventsAdapter.AudioPostViewHolder
                viewHolder?.updatePlayButtonImage(true)
                currentlyPlayingViewHolder = viewHolder

            }
            override fun onClickStopListen(position: Int, mediaPlayer: MediaPlayer) {
                currentlyPlayingViewHolder?.updatePlayButtonImage(false)
                currentlyPlayingViewHolder = null
            }
        })

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true

        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.postsRecyclerView.adapter = adapter
        binding.postsRecyclerView.isNestedScrollingEnabled = false

        init()

        binding.EditProfile.setOnClickListener {
            editFragmentForProfile.show(childFragmentManager,  editFragmentForProfile.tag)
        }
        binding.AddEventButton.setOnClickListener {
            fragmentForEditEvents.show(childFragmentManager,fragmentForEditEvents.tag)
            EditEventTime = true
        }
    }
    private fun stopCurrentlyPlaying() {
        currentlyPlayingViewHolder?.let {
            it.updatePlayButtonImage(false)
            it.mediaPlayer.pause()
        }
    }
    private fun init() {
        binding.EditProfile.setColorFilter(ContextCompat.getColor(requireContext(),R.color.white))

        if(DataModel.userEvents.value != null) {
            adapter.addListEvent(DataModel.userEvents.value!!)
        }else
            Toast.makeText(activity,"Постов пока что нету",Toast.LENGTH_SHORT).show()

        Log.d("MyLog", "Load Init")
        DataModel.userProfile.observe(viewLifecycleOwner) {

            Log.d("MyLog", "Changed $it")
            Glide.with(binding.root.context)
                .load(it.avatar)
                .error(R.drawable.profile_foro)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(binding.profileImage)

            binding.firstName.text = it.firstname
            binding.secondName.text = it.secondname
            binding.schoolText.text = it.scholl
            binding.ageText.text = it.age
            binding.cityText.text = it.city
            binding.classText.text = it.targetClass

        }

        DataModel.UserEventRightNow.observe(viewLifecycleOwner){
            Log.d("MyLog","UserEventRightNow")
            if(it != null && EditEventTime ) adapter.addEvent(it)
        }
    }

}