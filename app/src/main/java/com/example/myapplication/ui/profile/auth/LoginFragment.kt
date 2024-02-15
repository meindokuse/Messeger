package com.example.myapplication.ui.profile.auth

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.ui.MainActivity
import com.example.myapplication.ui.profile.viewmodel.ProfileViewModel
import com.example.myapplication.util.Constance
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {


    lateinit var binding: FragmentLoginBinding
    private val profileViewModel: ProfileViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailText.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //ignore
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.noFoundUserText.visibility = View.INVISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
                //ignore
            }

        })


        binding.loginButton.setOnClickListener {
            if (!isEmpty() && isValide()){
                val email = binding.emailText.text.toString()
                val password = binding.passwordText.text.toString()
                lifecycleScope.launch(Dispatchers.IO){
                    try {
                        val response = profileViewModel.loginUser(requireContext(),email,password)
                        withContext(Dispatchers.Main) {
                            when (response) {
                                Constance.SUCCESS -> {
                                    (activity as MainActivity).showBottomNavigationBar()
                                    (activity as MainActivity).supportActionBar?.show()
                                    findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
                                }
                                Constance.NO_FOUND_USER -> {
                                    binding.noFoundUserText.visibility = View.VISIBLE
                                }
                                else-> Toast.makeText(requireContext(),"Ошибка при входе ",Toast.LENGTH_SHORT).show()

                            }
                        }

                    } catch (e:Exception){
                        Log.d("MyLog","Ошибка при входе $e")
                        Toast.makeText(requireContext(),"Ошибка при входе ",Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
        binding.goBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }




    private fun isEmpty(): Boolean = with(binding) {
        if (loginButton.text.isNullOrEmpty()) loginButton.error = "Поле пустое"
        if (passwordText.text.isNullOrEmpty()) passwordText.error = "Поле пустое"
        return loginButton.text.isNullOrEmpty() || passwordText.text.isNullOrEmpty()
    }

    private fun isValide(): Boolean = with(binding){
        if (!loginButton.text.contains("@")) loginButton.error = "Некорректый ввод"
        if (loginButton.text.length < 8) loginButton.error = "Не менее 8 символов"
        return loginButton.text.contains("@") && loginButton.text.length > 8
    }



}