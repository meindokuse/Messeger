package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.elements.Event
import com.example.myapplication.profile.rcview.ItemListener
import io.reactivex.rxjava3.core.Observable


class BlankFragmentForTest : Fragment() {

    private lateinit var adapter: UniversalAdapter<Event>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(view.rootView)
            .load(R.drawable.profile_foro)
            .error(R.drawable.profile_foro)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(view.findViewById(R.id.profileImage))

        adapter = UniversalAdapter(object: ItemListener {
            override fun onClickDelete(position: Int) {
                val event = adapter.getAllItems()[position]
                adapter.removeItem(position)
            }

            override fun onClickStartListen(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onClickStopListen(position: Int) {
                TODO("Not yet implemented")
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
            adapter.addData(Event("qwe","Ауе","sadasdasdasdasdasd",1,1))
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