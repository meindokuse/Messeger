package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.elements.Event
import com.example.myapplication.profile.rcview.ItemListener
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers


class BlankFragmentForTest : Fragment() {

    private lateinit var adapter: UniversalAdapter<Event>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank_for_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(view.rootView)
            .load(R.drawable.profile_foro)
            .error(R.drawable.profile_foro)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(view.findViewById(R.id.profileImage))

        adapter = UniversalAdapter(object: ItemListener {
            override fun onClick(position: Int) {
                val event = adapter.getAllItems()[position]
                adapter.removeItem(position)
            }
        },constanse.KEY_FOR_POSTS)

        val list = view.findViewById<RecyclerView>(R.id.postsRecyclerView)

        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = adapter

        testAddItems()

//        val button = view.findViewById<Button>(R.id.button123)

//        button.setOnClickListener {
//            Log.d("MyLog","click click")
//        }

//        val disposable = dataSource()
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe( {
//                button.text = "Next int $it"
//                Log.d("MyLog","$it")
//            },{
//                Log.d("MyLog","${it.localizedMessage}")
//            },{
//
//            })


    }

    companion object {
        @JvmStatic
        fun newInstance() = BlankFragmentForTest()
    }
    fun testAddItems(){
        for(i in 1..10){
            adapter.addData(Event("qwe","Ауе","sadasdasdasdasdasd"))
        }
    }

    fun dataSource(): Observable<Int>{
        return Observable.create {subscriber ->
            for (i in 0..100){
                Thread.sleep(500)
                subscriber.onNext(i)
            }
        }
    }
}