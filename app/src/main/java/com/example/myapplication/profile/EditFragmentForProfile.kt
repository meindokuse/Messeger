package com.example.myapplication.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentEditForProfileBinding
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.viewmodel.MyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class EditFragmentForProfile :  BottomSheetDialogFragment() {
    private var foto:Bitmap? = null
    var profile = arrayListOf<String>("Ромн","Самофалов","ОГБОУ СОЩ 3","Строитель","17","11 А")
    lateinit var binding: FragmentEditForProfileBinding

    val DataModel: MyViewModel by activityViewModels{
        MyViewModelFactory(LocalReposetoryHelper(requireContext()),requireActivity().application)
    }

    private val changeAvatar = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data
            if (imageUri != null) {

                Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(binding.AvatarChange)

                Glide.with(this)
                    .asBitmap()
                    .load(imageUri)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            foto = resource
                        }
                    })
            }
        }
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
        init()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.AvatarChange.setOnClickListener {
            openImagePicker()
        }

        binding.DoneButton.setOnClickListener{
            Log.d("MyLog","Чатичгая Обработка")
            if (!isEmptyData()){
                val Name = binding.NameEdit.text.toString()
                val Familia = binding.SecondNameEdit.text.toString()
                Log.d("MyLog","Полная Обработка")

//                DataModel.addUser(requireContext(),profile,foto)

                DataModel.uppdateProfile(Name,Familia,foto)
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
            if(it != null) {
                Glide.with(binding.root.context)
                    .load(it.avatar)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(binding.AvatarChange)
            } else{
                binding.AvatarChange.setImageResource(R.drawable.profile_foro)
            }

        }


        DataModel.userProfile.observe(viewLifecycleOwner){
            binding.NameEdit.setText(it.firstname)
        }
        DataModel.userProfile.observe(viewLifecycleOwner){
            binding.SecondNameEdit.setText(it.secondname)
        }
    }
    private fun openImagePicker() {
        val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickImageIntent.type = "image/*"
        changeAvatar.launch(pickImageIntent)
    }
}