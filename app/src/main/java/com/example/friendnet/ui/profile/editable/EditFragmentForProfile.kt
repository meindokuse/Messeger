package com.example.friendnet.ui.profile.editable

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.friendnet.R
import com.example.friendnet.data.models.local.ProfileEntity
import com.example.friendnet.data.models.remote.ProfileDto
import com.example.friendnet.databinding.FragmentEditForProfileBinding
import com.example.friendnet.ui.LoadingDialog
import com.example.friendnet.ui.profile.viewmodel.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


@AndroidEntryPoint
class EditFragmentForProfile : BottomSheetDialogFragment() {
    private var foto: Uri? = null
    lateinit var binding: FragmentEditForProfileBinding

    private val profileViewModel: ProfileViewModel by activityViewModels()


    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(4,4)
                .setCropShape(CropImageView.CropShape.OVAL)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private val changeAvatar =
        registerForActivityResult(cropActivityResultContract) { uri ->

            uri?.let { imageUri ->
                Glide.with(binding.root.context)
                    .load(imageUri)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(binding.AvatarChange)

                foto = imageUri

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

    companion object {
        const val REQUEST_CODE_STORAGE_PERMISSION = 123
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.AvatarChange.setOnClickListener {
//            Log.d("MyLog","click to change foto")
//            if (ContextCompat.checkSelfPermission(
//                    requireContext(),
//                    android.Manifest.permission.READ_MEDIA_IMAGES
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
//                    REQUEST_CODE_STORAGE_PERMISSION
//                )
//                Log.d("MyLog","requestPermissions")
//            } else {
//                Log.d("MyLog","READ_MEDIA_IMAGES")
//                openImagePicker()
//            }
            openImagePicker()

        }

        binding.DoneButton.setOnClickListener {

            if (!isEmptyData()) {

                val name = binding.NameEdit.text.toString()
                val familia = binding.SecondNameEdit.text.toString()
                val nowInfo = profileViewModel.userProfile.value!!
                val loadingDialog = LoadingDialog()

                lifecycleScope.launch(Dispatchers.Main) {
                    loadingDialog.show(childFragmentManager, LoadingDialog().tag)
                    Log.d("MyLog", "запуск обновы $foto")

                    nowInfo.apply {
                        profileViewModel.updateProfile(
                           profileDto =  ProfileDto(
                                user_id = user_id,
                                firstname = name,
                                secondname = familia,
                                school, city, age, targetClass, avatar, email, password
                            ) ,
                           avatar =  foto)
                    }

                    loadingDialog.dismiss()
                    dismiss()
                    foto = null
                }

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
        profileViewModel.userProfile.observe(viewLifecycleOwner) {
            if (it != null) {

                val internalFoto = File(context?.filesDir, it.avatar)

                Glide.with(binding.root.context)
                    .load(internalFoto)
                    .error(R.drawable.profile_foro)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(binding.AvatarChange)


            } else {
                binding.AvatarChange.setImageResource(R.drawable.profile_foro)
            }
        }
        profileViewModel.userProfile.observe(viewLifecycleOwner) {
            binding.NameEdit.setText(it.firstname)
        }
        profileViewModel.userProfile.observe(viewLifecycleOwner) {
            binding.SecondNameEdit.setText(it.secondname)
        }
    }

    private fun openImagePicker() {
        changeAvatar.launch(null)
    }
}