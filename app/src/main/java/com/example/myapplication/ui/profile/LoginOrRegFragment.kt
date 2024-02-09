package com.example.myapplication.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.ui.MainActivity
import com.example.myapplication.R
import com.example.myapplication.ui.profile.viewmodel.ProfileViewModel
import com.example.myapplication.util.Constance
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
@AndroidEntryPoint
class LoginOrRegFragment : Fragment() {

    private val dataModel: ProfileViewModel by activityViewModels()
    private lateinit var fotoImage: ImageView

    private var foto: Uri? = null


    private val changeAvatar = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data
            if (imageUri != null) {

                Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(fotoImage)

                foto = imageUri
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fotoImage = view.findViewById(R.id.fotoReg)
        val firstName = view.findViewById<EditText>(R.id.firstNameReg)
        val secondName = view.findViewById<EditText>(R.id.seondNameReg)
        val age = view.findViewById<EditText>(R.id.ageReg)
        val school = view.findViewById<EditText>(R.id.schoolReg)
        val city = view.findViewById<EditText>(R.id.cityReg)
        val doneButton = view.findViewById<FloatingActionButton>(R.id.DoneReg)
        val email = view.findViewById<EditText>(R.id.email)
        val password = view.findViewById<EditText>(R.id.password)

        fotoImage.setOnClickListener {
            val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
            pickImageIntent.type = "image/*"
            changeAvatar.launch(pickImageIntent)
        }

        fun areFieldsEmpty(): Boolean {
            val fields = listOf(firstName, secondName, age, school, city)

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
            val tClass = "11"
            val login = email.text.toString()
            val passwordToReg = password.text.toString()

            val profile = arrayListOf(name, name2, sch, cityyy, ageee, tClass,login,passwordToReg)

            if (!areFieldsEmpty()) {
                lifecycleScope.launch(Dispatchers.IO){
                    try {
                        val result = dataModel.addUser(requireContext(), profile, foto)
                        withContext(Dispatchers.Main){

                            if (result == Constance.SUCCESS){
                                (activity as MainActivity).showBottomNavigationBar()
                                (activity as MainActivity).supportActionBar?.show()
                                findNavController().navigate(R.id.action_loginOrRegFragment_to_profileFragment)
                            } else {
                                Toast.makeText(requireContext(), "Ошибка!",Toast.LENGTH_SHORT).show()
                            }

                        }
                    } catch (e:Exception){
                        withContext(Dispatchers.Main){
                            Log.d("MyLog","Ошибка при регистрации ${e.message}")
                            Toast.makeText(requireContext(), "Ошибка!",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Не все поля заполнены!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}