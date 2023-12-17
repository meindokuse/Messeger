package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.profile.ProfileFragment

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragmentHostNavigate = supportFragmentManager.findFragmentById(R.id.placeholder) as NavHostFragment
        val controller = fragmentHostNavigate.navController
        NavigationUI.setupWithNavController(binding.BNV,controller)



        init()

        binding.BNV.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.profile->{
                    controller.navigate(R.id.profileFragment)
                    supportActionBar?.title="Ваш Профиль"
                }
                R.id.chats->{
                    controller.navigate(R.id.listOfChatsFragment)
                    supportActionBar?.title="Чаты"
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
    private fun init(){
        supportActionBar?.title="Ваш Профиль"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.BNV.menu.findItem(R.id.chats).isChecked=false
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.placeholder, ProfileFragment.newInstance())
//            .commit()


    }
}