package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.profile.BlankFragment

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        binding.BNV.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.profile->{
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.placeholder, BlankFragment.newInstance())
                        .commit()
                    supportActionBar?.title="Ваш Профиль"
                }
                R.id.chats->{
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.placeholder,listOfChatsFragment.newInstance())
                        .commit()
                    supportActionBar?.title="Чаты"
                }

            }
            true
        }




    }
    private fun init(){
        supportActionBar?.title="Ваш Профиль"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.BNV.menu.findItem(R.id.profile).isChecked=false
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.placeholder, BlankFragment.newInstance())
            .commit()


    }
}