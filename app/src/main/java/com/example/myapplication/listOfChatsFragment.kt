package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ActionMode
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ListOfChatsBinding
import com.example.myapplication.reposetory.LocalReposetoryHelper
import kotlinx.coroutines.launch
import java.lang.Math.abs


class listOfChatsFragment : Fragment() {
    lateinit var binding: ListOfChatsBinding
    private lateinit var handler: Handler
    private var actionMode: ActionMode? = null
    private lateinit var gestureDetector: GestureDetector

    val ChatDataModel: ViewModelForChats by activityViewModels {
        ChatsViewModelFactory(LocalReposetoryHelper(requireContext()))
    }
    val addNewchatFragment = AddNewChatFragment()
    lateinit var adapter: RvChats

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ListOfChatsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        binding.FindNewChatButton.setOnClickListener {
            addNewchatFragment.show(childFragmentManager, addNewchatFragment.tag)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = listOfChatsFragment()
        val LONG_CLICK_DURATION = 1200L

    }

    @SuppressLint("ClickableViewAccessibility")
    fun init() {
        handler = Handler()

        adapter = RvChats()
        binding.ListChats.layoutManager = LinearLayoutManager(activity)
        binding.ListChats.adapter = adapter

            // ПРИ НЕОБХОДИМОСТИ ВЕРНУТЬ

//        var gestureDetector: GestureDetector? = null
//        gestureDetector =
//            GestureDetector(activity, object : GestureDetector.SimpleOnGestureListener() {
//                override fun onLongPress(e: MotionEvent) {
//                    val childView = binding.ListChats.findChildViewUnder(e.x, e.y)
//                    if (childView != null) {
//                        startActionMode(binding.ListChats)
//                        val position = binding.ListChats.getChildLayoutPosition(childView)
//                        adapter.toggleSelection(position)
//
//                    }
//                }
//            })
//
//        binding.ListChats.setOnTouchListener { _, event ->
//            gestureDetector.onTouchEvent(event)
//            false
//        }

        binding.ListChats.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            private var startX = 0f
            private var startY = 0f
            private var isSelectionStarted = false

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = e.x
                        startY = e.y
                        isSelectionStarted = false
                        handler.postDelayed({
                            if (!isSelectionStarted ) {
                                startActionMode(rv)
                                val childView = rv.findChildViewUnder(startX, startY)
                                if (childView != null) {
                                    val position = rv.getChildLayoutPosition(childView)
                                    adapter.toggleSelection(position)
                                    isSelectionStarted = true
                                }
                            }
                        }, LONG_CLICK_DURATION)
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (!isSelectionStarted && (abs(e.x - startX) > ViewConfiguration.get(
                                requireContext()
                            ).scaledTouchSlop ||
                                    abs(e.y - startY) > ViewConfiguration.get(requireContext()).scaledTouchSlop)
                        ) {
                            handler.removeCallbacksAndMessages(null)
                            isSelectionStarted = true
                        }
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        handler.removeCallbacksAndMessages(null)
                        if (!isSelectionStarted && actionMode !=null) {
                            val childView = rv.findChildViewUnder(startX, startY)
                            if (childView != null) {
                                val position = rv.getChildLayoutPosition(childView)
                                adapter.toggleSelection(position)
                            }
                        }
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                // Обработка событий касания
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                // Обработка запрета перехвата событий
            }
        })

        ChatDataModel.ListOfChats.observe(viewLifecycleOwner) {
            Log.d("MyLog","Модель обновила чаты")
            adapter.setChats(it)
        }
    }

    fun startActionMode(view: View): ActionMode {
        binding.FindNewChatButton.animate()
            .translationXBy(binding.FindNewChatButton.width.toFloat())
            .alpha(0.0f)
            .setDuration(500)
            .withEndAction{
                binding.FindNewChatButton.visibility = View.GONE
            }
        return if (actionMode == null) {
            // Передаем в startActionMode текущее окно или вид
            actionMode = view.startActionMode(object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    requireActivity().menuInflater.inflate(R.menu.menu_for_chats, menu)
                    mode?.title="Редактирование"
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.menu_delete -> {
                            mode?.title = "Удаление..."
                            lifecycleScope.launch {

                                ChatDataModel.deleteChat(adapter.outGetSelectedChats())
                                actionMode?.finish()
                            }
                        }
                    }
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode?) {
                    adapter.clearSelection()
                    actionMode = null

                    binding.FindNewChatButton.visibility = View.VISIBLE
                    binding.FindNewChatButton.alpha = 0.0f
                    binding.FindNewChatButton.translationX = binding.FindNewChatButton.width.toFloat() // Помещаем вправо на ширину кнопки

                    binding.FindNewChatButton.animate()
                        .translationXBy(-binding.FindNewChatButton.width.toFloat()) // Перемещение влево на ширину кнопки
                        .alpha(1.0f)
                        .setDuration(500)

                }
            })
            actionMode!!
        } else {
            actionMode!!
        }
    }
}
