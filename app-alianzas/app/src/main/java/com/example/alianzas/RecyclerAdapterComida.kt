package com.example.alianzas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alianzas.Models.Comida
import com.squareup.picasso.Picasso

class RecyclerAdapterComida(private val lista: ArrayList<Comida>, val context: Context, private val clickListener: ClickListener): RecyclerView.Adapter<RecyclerAdapterComida.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context)
            .load(lista[position].ImageURL)
            .into(holder.logo)

        holder.fecha.text = lista[position].Description

    }


    class ViewHolder(view: View, var listener: ClickListener): RecyclerView.ViewHolder(view), View.OnClickListener{

        private val cell=view
        val logo= view.findViewById<ImageView>(R.id.image_establecimiento)
        val fecha= view.findViewById<TextView>(R.id.textFechaE)

        init{
            this.cell.setOnClickListener(this)
        }

        override fun onClick(v: View?){
            this.listener.dataPosition(adapterPosition)
        }

    }
}
