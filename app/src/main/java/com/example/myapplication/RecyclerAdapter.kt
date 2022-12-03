package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(
    private var list: List<Task>
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    lateinit var onItemClick: (id: Int) -> Unit
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = list[position].name
        holder.buttonEdit.setOnClickListener {
            onItemClick(holder.adapterPosition)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(newList :List<Task>){
        list = newList
        this.notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.textView)
        val buttonEdit = itemView.findViewById<Button>(R.id.buttonEdit)
    }
}