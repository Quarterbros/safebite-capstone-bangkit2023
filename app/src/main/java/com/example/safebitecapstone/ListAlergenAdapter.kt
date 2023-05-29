package com.example.safebitecapstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safebitecapstone.dummyData.Alergen
import java.util.ArrayList

class ListAlergenAdapter(private val listAlergen: ArrayList<Alergen>) : RecyclerView.Adapter<ListAlergenAdapter.ListViewHolder>() {
//    private lateinit var onItemClickCallback: OnItemClickCallback

//    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
//        this.onItemClickCallback = onItemClickCallback
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.alergen_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListAlergenAdapter.ListViewHolder, position: Int) {
        val (name, description, photo) = listAlergen[position]
        holder.imgPhoto.setImageResource(photo)
        holder.title.text = name
        holder.description.text = description
    }

    override fun getItemCount(): Int = listAlergen.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.tv_image)
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val description: TextView = itemView.findViewById(R.id.tv_desc)
    }
//
//    interface OnItemClickCallback {
//        fun onItemClicked(data: Alergen)
//    }
}