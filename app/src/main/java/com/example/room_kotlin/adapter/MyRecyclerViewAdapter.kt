package com.example.room_kotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.room_kotlin.R
import com.example.room_kotlin.databinding.ListItemBinding
import com.example.room_kotlin.db.Subscriber
import java.util.concurrent.Flow

class MyRecyclerViewAdapter(private val clickListener:(Subscriber)->Unit):
    RecyclerView.Adapter<MyViewHolder>() {

    private val subscribersList=ArrayList<Subscriber>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val binding:ListItemBinding=DataBindingUtil.inflate(layoutInflater, R.layout.list_item,parent,false)
        return  MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(subscribersList[position],clickListener)
    }

    override fun getItemCount(): Int {
        return subscribersList.size
    }

    fun setList(subscriber: List<Subscriber>){
        subscribersList.clear()
        subscribersList.addAll(subscriber)
    }

}

class MyViewHolder(private val binding:ListItemBinding):RecyclerView.ViewHolder(binding.root){

    fun bind(subscriber:Subscriber,clickListener:(Subscriber)->Unit){
        binding.nameTextItemDesignId.text=subscriber.name
        binding.emailTextItemDesignId.text=subscriber.email
        binding.listItemLayout.setOnClickListener {
            clickListener(subscriber)
        }
    }
}