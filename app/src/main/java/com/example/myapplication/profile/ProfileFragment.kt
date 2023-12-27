package com.example.myapplication.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.UniversalAdapter
import com.example.myapplication.constanse
import com.example.myapplication.profile.rcview.EventListAdapter
import com.example.myapplication.databinding.FragmentBlankBinding
import com.example.myapplication.elements.Event
import com.example.myapplication.profile.rcview.ItemListener
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.viewmodel.MyViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    var EditEventTime = false

    val fragmentForEditEvents = FragmentForEditEvents()
    val editFragmentForProfile = EditFragmentForProfile()

    private lateinit var binding:FragmentBlankBinding
    lateinit var adapter: UniversalAdapter<Event>
    private val DataModel: MyViewModel by activityViewModels{
        MyViewModelFactory(LocalReposetoryHelper(requireContext()),requireActivity().application)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBlankBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = UniversalAdapter(object: ItemListener{
            override fun onClick(position: Int) {
                val event = adapter.getAllItems()[position]
                DataModel.DeleteEvent(event,1)
                adapter.removeItem(position)
            }
        },constanse.KEY_FOR_POSTS)

        binding.RcView.layoutManager = LinearLayoutManager(activity)
        binding.RcView.adapter = adapter
        binding.RcView.isNestedScrollingEnabled = false

        init()

        binding.EditProfileButton.setOnClickListener {
            editFragmentForProfile.show(childFragmentManager,  editFragmentForProfile.tag)
        }
        binding.EditNowAcivities.setOnClickListener {
            fragmentForEditEvents.show(childFragmentManager,fragmentForEditEvents.tag)
            EditEventTime = true
        }



    }
    private fun init() {
        if(DataModel.userEvents.value != null) {
            adapter.addListData(DataModel.userEvents.value!!)
        }else
            Toast.makeText(activity,"Постов пока что нету",Toast.LENGTH_SHORT).show()

        Log.d("MyLog", "Load Init")
        DataModel.userProfile.observe(viewLifecycleOwner) {

            Log.d("MyLog", "Changed $it")
            Glide.with(binding.root.context)
                .load(it.avatar)
                .error(R.drawable.profile_foro)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(binding.AvatarImage)

            binding.FirstNameText.text = it.firstname
            binding.SecondNameText.text = it.secondname
            binding.SchoolText.text = it.scholl
            binding.AgeText.text = it.age
            binding.CityText.text = it.city
            binding.ClassText.text = it.targetClass

        }

        DataModel.UserEventRightNow.observe(viewLifecycleOwner){
            Log.d("MyLog","UserEventRightNow")
            if(it != null && EditEventTime ) adapter.addData(it)
        }

    }
    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()

    }


}