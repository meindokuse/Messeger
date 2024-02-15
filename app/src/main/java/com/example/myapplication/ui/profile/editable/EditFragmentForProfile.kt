package com.example.myapplication.ui.profile.editable

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentEditForProfileBinding
import com.example.myapplication.ui.profile.viewmodel.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class EditFragmentForProfile : BottomSheetDialogFragment() {
    private var foto: Uri? = null
    lateinit var binding: FragmentEditForProfileBinding

    val DataModel: ProfileViewModel by activityViewModels()

    private val changeAvatar =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data
                if (imageUri != null) {

                    Glide.with(binding.root.context)
                        .load(imageUri)
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(binding.AvatarChange)

                    foto = imageUri
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

        binding.DoneButton.setOnClickListener {

            if (!isEmptyData()) {
                val name = binding.NameEdit.text.toString()
                val familia = binding.SecondNameEdit.text.toString()

                lifecycleScope.launch {
                    DataModel.updateProfile(name, familia, foto)
                }
                dismiss()
                foto = null
            }
        }
    }

    private fun isEmptyData(): Boolean {
        binding.apply {
            if (NameEdit.text.isNullOrEmpty()) NameEdit.error = "Заполни поле"
            if (SecondNameEdit.text.isNullOrEmpty()) SecondNameEdit.error = "Заполни поле"
            return NameEdit.text.isNullOrEmpty() || SecondNameEdit.text.isNullOrEmpty()
        }
    }

    private fun init() {
        DataModel.userProfile.observe(viewLifecycleOwner) {
            if (it != null) {

                val internalFoto = File(context?.filesDir, it.avatar)

                lifecycleScope.launch {
                    val remoteFoto = async {
                        DataModel.getLinkToFile(it.user_id, it.avatar)
                    }.await()

                    Glide.with(binding.root.context)
                        .load(remoteFoto)
                        .placeholder(R.drawable.loading)
                        .error(Glide.with(binding.root.context).load(internalFoto.path))
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(binding.AvatarChange)
                }

            } else {
                binding.AvatarChange.setImageResource(R.drawable.profile_foro)
            }
        }
        DataModel.userProfile.observe(viewLifecycleOwner) {
            binding.NameEdit.setText(it.firstname)
        }
        DataModel.userProfile.observe(viewLifecycleOwner) {
            binding.SecondNameEdit.setText(it.secondname)
        }
    }

    private fun openImagePicker() {
        val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickImageIntent.type = "image/*"
        changeAvatar.launch(pickImageIntent)
    }
}