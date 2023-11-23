package com.example.myapplication.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.FragmentEditForProfileBinding
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.viewmodel.MyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class EditFragmentForProfile :  BottomSheetDialogFragment() {
    var profile = ProfileInfo("Ромн","Самофалов","ОГБОУ СОЩ 3","Строитель","17","11 А")
    lateinit var binding: FragmentEditForProfileBinding
    val DataModel: MyViewModel by activityViewModels{
        MyViewModelFactory(LocalReposetoryHelper(requireContext()))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = FragmentEditForProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        Log.d("MyLog", "After - FirstName: ${DataModel.FirstName.value}, SecondName: ${DataModel.SecondName.value}")
        init()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.DoneButton.setOnClickListener{
            Log.d("MyLog","Чатичгая Обработка")
            if (!isEmptyData()){
                val Name = binding.NameEdit.text.toString()
                val Familia = binding.SecondNameEdit.text.toString()
                Log.d("MyLog","Полная Обработка")

//                DataModel.addUser(profile)

                DataModel.uppdateProfile(Name,Familia)
                dismiss()
            }
        }


    }



    companion object {
        @JvmStatic
        fun newInstance() = EditFragmentForProfile()

    }
    private fun isEmptyData():Boolean{
        binding.apply {
            if (NameEdit.text.isNullOrEmpty()) NameEdit.error = "Заполни поле"
            if (SecondNameEdit.text.isNullOrEmpty()) SecondNameEdit.error = "Заполни поле"
            return NameEdit.text.isNullOrEmpty() || SecondNameEdit.text.isNullOrEmpty()
        }
    }
    fun init(){
        DataModel.userProfile.observe(viewLifecycleOwner){
            binding.NameEdit.setText(it.firstname)
        }
        DataModel.userProfile.observe(viewLifecycleOwner){
            binding.SecondNameEdit.setText(it.secondname)
        }
    }
}