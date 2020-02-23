package com.example.kotlintest

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlintest.data.ItemDetailResponse
import kotlinx.android.synthetic.main.item_main.view.*

class MainAdapter(var itemList: MutableList<ItemDetailResponse>, val context: Context) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {


    var items: MutableList<ItemDetailResponse> = itemList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MainViewHolder(parent)


    override fun getItemCount() : Int{
        return items.size
    }

    fun addItem(itemAddedList: List<ItemDetailResponse>){
        val insertPosition = items.size
        for(item in itemAddedList){
            items.add(item)
        }
        notifyItemInserted(insertPosition)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {

        items[position].let{item ->
            with(holder){
                tvBrand.text = item.brand
                tvName.text = item.name

                Glide.with(context).load(item.image)
                    .into(ivImage)
            }
        }
    }


    inner class MainViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)){
        val tvBrand = itemView.tv_brand
        val tvName = itemView.tv_name
        val ivImage = itemView.iv_image
    }
}