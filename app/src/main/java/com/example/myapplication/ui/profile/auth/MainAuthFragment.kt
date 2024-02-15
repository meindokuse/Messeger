package com.example.myapplication.ui.profile.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.R
import com.example.myapplication.ui.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainAuthFragment : Fragment(), MainAuthInteraction{

    private lateinit var viewPager: ViewPager

    private val profileViewModel:ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_main_auth, container, false)

        viewPager = rootView.findViewById(R.id.viewPager)
        val adapter = MainFragmentPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter

        return rootView
    }
    private inner class MainFragmentPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> LoginFragment()
                1 -> RegFragment()
                else -> throw IllegalArgumentException("Invalid position")
            }
        }

        override fun getCount(): Int {
            return 2
        }
    }

   override suspend fun loginUser(email:String,password:String):Int{
       return profileViewModel.loginUser(requireContext(),email,password)
    }

    override fun goReg() {
        viewPager.setCurrentItem(0, true)
    }

    override fun goLogin() {
        viewPager.setCurrentItem(1, true)
    }

    override suspend fun regUser(profile:ArrayList<String>): Int {
       return profileViewModel.addUser(requireContext(),profile)
   }








}