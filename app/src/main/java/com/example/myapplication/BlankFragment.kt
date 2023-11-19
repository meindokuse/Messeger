package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentBlankBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment() {

    val editFragmentForProfile = EditFragmentForProfile()
    val listtitle = arrayOf("Олимпиада"," Проект","Подготовка к экзамену","Спортивные соревнования")
    val desctittle = arrayOf("Очень трудная", "Ищу человека для совестной работы","Кто нибудь поможет разобрать одну тему?","21.11.23 г.Белгород")
    private lateinit var binding:FragmentBlankBinding
    private lateinit var adapter:EventListAdapter
    private val DataModel: MyViewModel by activityViewModels{
        MyViewModelFactory(LocalReposetoryHelper(requireContext()))
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
        init()
        binding.EditProfileButton.setOnClickListener {
            editFragmentForProfile.show(childFragmentManager,  editFragmentForProfile.tag)
        }
        adapter = EventListAdapter()
        binding.RcView.layoutManager = LinearLayoutManager(activity)
        binding.RcView.adapter = adapter
        for (i in 0..<listtitle.size){
            val event = Event(listtitle[i],desctittle[i])
            adapter.addEvent(event)
            Log.d("MyLog","EventInCycle")

        }


    }
    private fun init() {
        Log.d("MyLog", "Load Init")
        DataModel.userProfile.observe(viewLifecycleOwner) {
            Log.d("MyLog", "Changed $it")

            binding.FirstNameText.text = it.firstname
            binding.SecondNameText.text = it.secondname
            binding.SchoolText.text = it.scholl
            binding.AgeText.text = it.age
            binding.CityText.text = it.city
            binding.ClassText.text = it.targetClass

        }
//        DataModel.FirstName.observe(viewLifecycleOwner){
//            binding.FirstNameText.text = it
//        }
    }
    companion object {

        @JvmStatic
        fun newInstance() = BlankFragment()


    }

    override fun onPause() {
        super.onPause()
        Log.d("MyLog","weqeweqeqweqq")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MyLog","Reasawq")
    }


}