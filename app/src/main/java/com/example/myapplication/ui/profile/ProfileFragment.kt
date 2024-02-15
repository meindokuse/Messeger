package com.example.myapplication.ui.profile

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.ui.profile.rcview.EventsAdapter
import com.example.myapplication.R
import com.example.myapplication.util.Constance
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.ui.profile.editable.EditFragmentForProfile
import com.example.myapplication.ui.profile.editable.FragmentForEditEvents
import com.example.myapplication.ui.profile.rcview.ItemListener
import com.example.myapplication.data.reposetory.profile.RemoteUserReposImpl
import com.example.myapplication.domain.reposetory.profile.LocalProfileReposetory
import com.example.myapplication.ui.LoadingDialog
import com.example.myapplication.ui.profile.viewmodel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var EditEventTime = false

    private val fragmentForEditEvents = FragmentForEditEvents()
    private val editFragmentForProfile = EditFragmentForProfile()
    private var currentlyPlayingViewHolder: EventsAdapter.AudioPostViewHolder? = null


    private lateinit var binding: FragmentProfileBinding
    lateinit var adapter: EventsAdapter

    val user_id by lazy {
        activity?.getSharedPreferences(Constance.KEY_USER_PREFERENCES, Context.MODE_PRIVATE)
            ?.getString(Constance.KEY_USER_ID, null)
    }


    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        adapter = EventsAdapter(object : ItemListener {
            override fun onClickDelete(position: Int) {
                val event = adapter.getAllItems()[position]
                profileViewModel.DeleteEvent(event)
                adapter.removeItem(position)
            }

            override fun onClickStartListen(position: Int, mediaPlayer: MediaPlayer) {
                stopCurrentlyPlaying()
                val viewHolder =
                    binding.postsRecyclerView.findViewHolderForAdapterPosition(position) as? EventsAdapter.AudioPostViewHolder
                viewHolder?.updatePlayButtonImage(true)
                currentlyPlayingViewHolder = viewHolder

            }

            override fun onClickStopListen(position: Int, mediaPlayer: MediaPlayer) {
                currentlyPlayingViewHolder?.updatePlayButtonImage(false)
                currentlyPlayingViewHolder = null
            }
        })

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true

        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.postsRecyclerView.adapter = adapter
        binding.postsRecyclerView.isNestedScrollingEnabled = false

        init()


    }

    private fun stopCurrentlyPlaying() {
        currentlyPlayingViewHolder?.let {
            it.updatePlayButtonImage(false)
            it.mediaPlayer.pause()
        }
    }

    private fun init() {

        binding.EditProfile.setOnClickListener {
            editFragmentForProfile.show(childFragmentManager, editFragmentForProfile.tag)
        }
        binding.AddEventButton.setOnClickListener {
            fragmentForEditEvents.show(childFragmentManager, fragmentForEditEvents.tag)
            EditEventTime = true
        }

        binding.EditProfile.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))

        if (profileViewModel.userEvents.value != null) {
            adapter.addListEvent(profileViewModel.userEvents.value!!)
        } else
            Toast.makeText(activity, "Постов пока что нету", Toast.LENGTH_SHORT).show()

        Log.d("MyLog", "Load Init $user_id")
        profileViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->

            Log.d("MyLog","$userProfile")

            val internalFoto = File(context?.filesDir, userProfile.avatar)

            lifecycleScope.launch {
                val remoteFoto = async {
                    profileViewModel.getLinkToFile(userProfile.user_id, userProfile.avatar)
                }.await()

                Glide.with(binding.root.context)
                    .load(remoteFoto)
                    .placeholder(R.drawable.loading)
                    .error(Glide.with(binding.root.context).load(internalFoto.path))
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(binding.profileImage)
            }

            binding.firstName.text = userProfile.firstname
            binding.secondName.text = userProfile.secondname
            binding.schoolText.text = userProfile.school
            binding.ageText.text = userProfile.age
            binding.cityText.text = userProfile.city
            binding.classText.text = userProfile.targetClass
        }



        user_id?.let {
            syncUserData(it)
        }

        profileViewModel.UserEventRightNow.observe(viewLifecycleOwner) {
            Log.d("MyLog", "UserEventRightNow")
            if (it != null && EditEventTime) adapter.addEvent(it)
        }
    }

    private fun syncUserData(idUser: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val loadingDialog = LoadingDialog()

            withContext(Dispatchers.Main){
                loadingDialog.show(childFragmentManager, loadingDialog.tag)
            }
            val code = profileViewModel.syncUser(idUser)

            withContext(Dispatchers.Main){
                loadingDialog.dismiss()
                if (code == 0) {
                    showErrorSnackbar(idUser)
                }
            }
        }
    }

    private fun showErrorSnackbar(userId: String) {
        view?.let { view ->
            Snackbar.make(view, "Произошла ошибка при получении чатов", Snackbar.LENGTH_LONG)
                .setAction("Повторить") {
                    syncUserData(userId)
                }
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.ultra_grey))
                .show()
        }
    }
}