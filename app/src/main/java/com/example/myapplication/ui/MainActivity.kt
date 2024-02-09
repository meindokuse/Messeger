package com.example.myapplication.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.util.Constance
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val sharedPreferences =
            this.getSharedPreferences(Constance.KEY_USER_PREFERENCES, Context.MODE_PRIVATE)

        val user_id = sharedPreferences.getString(Constance.KEY_USER_ID, null)


        val fragmentHostNavigate =
            supportFragmentManager.findFragmentById(R.id.placeholder) as NavHostFragment
        val controller = fragmentHostNavigate.navController
        NavigationUI.setupWithNavController(binding.BNV, controller)
        setContentView(binding.root)

        if (user_id != null) {
            controller.navigate(R.id.action_loginOrRegFragment_to_profileFragment)
        } else {
            supportActionBar?.hide()
            hideBottomNavigationBar()
        }
        init()

        binding.BNV.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> {
                    controller.navigate(R.id.profileFragment)
                }

                R.id.chats -> {
                    controller.navigate(R.id.listOfChatsFragment)
                }
            }
            true
        }
        controller.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.profileFragment -> {
                    binding.BNV.menu.findItem(R.id.profile).isChecked = true
                    supportActionBar?.title = "Ваш Профиль"
                }

                R.id.listOfChatsFragment -> {
                    binding.BNV.menu.findItem(R.id.chats).isChecked = true
                    supportActionBar?.title = "Чаты"
                }
            }
        }


    }


    private fun init() {

        supportActionBar?.title = "Ваш Профиль"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.BNV.menu.findItem(R.id.chats).isChecked = false

    }

    fun hideBottomNavigationBar() {
        binding.BNV.visibility = View.GONE
    }

    fun showBottomNavigationBar() {
        binding.BNV.visibility = View.VISIBLE
    }


}



