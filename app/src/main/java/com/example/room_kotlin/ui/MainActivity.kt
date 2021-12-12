package com.example.room_kotlin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.room_kotlin.R
import com.example.room_kotlin.adapter.MyRecyclerViewAdapter
import com.example.room_kotlin.databinding.ActivityMainBinding
import com.example.room_kotlin.db.Subscriber
import com.example.room_kotlin.db.SubscriberDAO
import com.example.room_kotlin.db.SubscriberDatabase
import com.example.room_kotlin.db.SubscriberRepository
import com.example.room_kotlin.viewmodel.SubscriberViewModel
import com.example.room_kotlin.viewmodel.SubscriberViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao: SubscriberDAO =SubscriberDatabase.getInstance(application).subscriberDAO
        val repository=SubscriberRepository(dao)
        val factory=SubscriberViewModelFactory(repository)
        subscriberViewModel=ViewModelProvider(this,factory).get(SubscriberViewModel::class.java)
        binding.myViewModel=subscriberViewModel
        binding.lifecycleOwner=this
        initRecyclerView()

        subscriberViewModel.message.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
            }
        })


    }
    private fun initRecyclerView(){
        binding.recyclerViewId.layoutManager=LinearLayoutManager(this)
        adapter=MyRecyclerViewAdapter({selectedItem:Subscriber->listItemClicked(selectedItem)})
        binding.recyclerViewId.adapter=adapter
        displaySubscriberList()
    }

    private fun displaySubscriberList(){
        subscriberViewModel.subscribers.observe(this, Observer{
            Log.i("MY_TAG",it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
             })
    }

    private fun listItemClicked(subscriber: Subscriber){
      //  Toast.makeText(this,"Selected name is ${subscriber.name}",Toast.LENGTH_SHORT).show()
        subscriberViewModel.initUpdateAndDelete(subscriber)
    }
}