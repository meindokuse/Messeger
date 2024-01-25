package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.myapplication.profile.DescriptionVoiceFragment
import com.example.myapplication.profile.MyViewModelFactory
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.viewmodel.MyViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginOrRegFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginOrRegFragment : Fragment() {

    val DataModel:MyViewModel by viewModels {

        MyViewModelFactory(LocalReposetoryHelper(requireContext()),requireActivity().application)
    }
    lateinit var fotoImage:ImageView

    private val changeAvatar = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data
            if (imageUri != null) {

                Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(fotoImage)

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
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_or_reg, container, false)
    }
    var foto:Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fotoImage = view.findViewById(R.id.fotoReg)
        val firstName = view.findViewById<EditText>(R.id.firstNameReg)
        val secondName = view.findViewById<EditText>(R.id.seondNameReg)
        val age = view.findViewById<EditText>(R.id.ageReg)
        val school = view.findViewById<EditText>(R.id.schoolReg)
        val targetClass = view.findViewById<EditText>(R.id.classReg)
        val city = view.findViewById<EditText>(R.id.cityReg)
        val doneButton = view.findViewById<FloatingActionButton>(R.id.DoneReg)

        fotoImage.setOnClickListener {
            val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
            pickImageIntent.type = "image/*"
            changeAvatar.launch(pickImageIntent)
        }



        fun areFieldsEmpty(): Boolean {
            val fields = listOf(firstName, secondName, age, school, targetClass, city)

            for (field in fields) {
                if (field.text.isNullOrEmpty()) {
                    return true
                }
            }
            return false
        }

        doneButton.setOnClickListener {
            val name = firstName.text.toString()
            val name2 = secondName.text.toString()
            val sch = school.text.toString()
            val cityyy = city.text.toString()
            val ageee = age.text.toString()
            val tClass = targetClass.text.toString()

            val profile = arrayListOf(name, name2, sch, cityyy, ageee, tClass)

            if (!areFieldsEmpty()) {
                DataModel.addUser(requireContext(), profile, foto)

                (activity as MainActivity).showBottomNavigationBar()
                (activity as MainActivity).supportActionBar?.show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Не все поля заполнены!", Toast.LENGTH_SHORT).show()
            }
        }






    }


    companion object {
        @JvmStatic
        fun newInstance() = LoginOrRegFragment()
    }

}