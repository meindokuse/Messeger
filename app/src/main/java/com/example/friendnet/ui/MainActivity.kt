package com.example.friendnet.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.NavigationUI
import com.example.friendnet.R
import com.example.friendnet.databinding.ActivityMainBinding
import com.example.friendnet.util.ActivityHolder
import com.example.friendnet.util.AudioService
import com.example.friendnet.util.Constance
import com.example.friendnet.util.ServiceManager
import com.example.friendnet.util.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: SharedViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val sharedPreferences =
            this.getSharedPreferences(Constance.KEY_USER_PREFERENCES, Context.MODE_PRIVATE)

        val userId = sharedPreferences.getString(Constance.KEY_USER_ID, null)

        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                resources.getColor(
                    R.color.default_color_plus_dark,
                    null
                )
            )
        );

        val fragmentHostNavigate =
            supportFragmentManager.findFragmentById(R.id.placeholder) as NavHostFragment

        val controller = fragmentHostNavigate.navController
        NavigationUI.setupWithNavController(binding.BNV, controller)

        setContentView(binding.root)

        Log.d("MyLog", "$userId")

        if (userId != null) {
            controller.navigate(R.id.action_loginOrRegFragment_to_profileFragment)
        } else {
            supportActionBar?.hide()
            hideBottomNavigationBar()
        }
        init()

        binding.BNV.setOnNavigationItemReselectedListener {
            //ignore for create only single fragments
        }

        binding.BNV.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> {
                    controller.navigate(R.id.profileFragment,
                        bundleOf(),
                        navOptions {
                        launchSingleTop = true
                        popUpTo(R.id.navigator) {
                            inclusive = true
                        }
                        })
                }

                R.id.chats -> {
                    controller.navigate(R.id.listOfChatsFragment,
                        bundleOf(),
                        navOptions {
                            launchSingleTop = true
                            popUpTo(R.id.navigator){
                                inclusive = true
                            }
                        }
                    )
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
        ActivityHolder.setCurrentActivity(this)

        binding.BNV.menu.findItem(R.id.chats).isChecked = false

        binding.stopAudioButton.setOnClickListener {
            ServiceManager.clearFordisconect()
            stopService(Intent(this,AudioService::class.java))
        }

    }

    fun hideBottomNavigationBar() {
        binding.BNV.visibility = View.GONE

    }

    fun showBottomNavigationBar() {
        binding.BNV.visibility = View.VISIBLE
    }

    private val handler = Handler(Looper.myLooper()!!)

    private val updateTitleRunnable = object : Runnable {
        var dots = 0
        override fun run() {
            val actionBar = supportActionBar
            if (actionBar != null) {
                val title = when (dots) {
                    0 -> "Обновление."
                    1 -> "Обновление.."
                    else -> "Обновление..."
                }
                actionBar.title = title
            }
            dots = (dots + 1) % 3
            handler.postDelayed(this, 500) // Обновлять каждые 500 миллисекунд
        }
    }


    fun updatingTitle() {
        handler.post(updateTitleRunnable)
    }

    fun stopUpdatingTitle(title: String) {
        handler.removeCallbacks(updateTitleRunnable)
        val actionBar = supportActionBar
        actionBar?.title = title

    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    fun showAudioPanel(title: String,service: AudioService){
        binding.audioPanel.visibility = View.VISIBLE
        binding.textNameAudio.text = title
    }
    fun hideAudioPanel(){
        binding.audioPanel.visibility = View.GONE
    }


}



