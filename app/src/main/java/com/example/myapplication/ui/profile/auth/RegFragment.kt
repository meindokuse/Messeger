package com.example.myapplication.ui.profile.auth

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
class RegFragment : Fragment() {


    private val profileViewModel:ProfileViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_or_reg, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstName = view.findViewById<EditText>(R.id.firstNameReg)
        val secondName = view.findViewById<EditText>(R.id.seondNameReg)
        val age = view.findViewById<EditText>(R.id.ageReg)
        val school = view.findViewById<EditText>(R.id.schoolReg)
        val city = view.findViewById<EditText>(R.id.cityReg)
        val doneButton = view.findViewById<Button>(R.id.DoneReg)
        val email = view.findViewById<EditText>(R.id.email)
        val password = view.findViewById<EditText>(R.id.password)
        val toLoginText = view.findViewById<TextView>(R.id.goToLoginText)

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
                lifecycleScope.launch {
                    try {
                        val result = profileViewModel.addUser(requireContext(),profile)
                        withContext(Dispatchers.Main) {
                            if (result == Constance.SUCCESS) {
                                (activity as MainActivity).showBottomNavigationBar()
                                (activity as MainActivity).supportActionBar?.show()
                                findNavController().navigate(R.id.action_loginOrRegFragment_to_profileFragment)
                            } else {
                                Toast.makeText(requireContext(), "Ошибка!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.d("MyLog", "Ошибка при регистрации ${e.message}")
                            Toast.makeText(requireContext(), "Ошибка!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Не все поля заполнены!", Toast.LENGTH_SHORT).show()
            }
        }

        toLoginText.setOnClickListener {
            findNavController().navigate(R.id.action_loginOrRegFragment_to_loginFragment)
        }
    }

}