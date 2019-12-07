package com.example.alianzas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alianzas.Models.Establecimiento

class RecyclerAdapterEstablecimiento(private val lista2: ArrayList<Establecimiento>, val context: Context, private val clickListener: ClickListener): RecyclerView.Adapter<RecyclerAdapterEstablecimiento.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_establecimiento, parent, false)
        return ViewHolder(view, clickListener)
    }



    override fun getItemCount(): Int {
        return lista2.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(lista2[position].ImageURL)
            .into(holder.logo)

        holder.nombre.text = lista2[position].Title
        holder.descrip.text = lista2[position].Description
    }

    class ViewHolder(view: View, var listener: ClickListener): RecyclerView.ViewHolder(view), View.OnClickListener {

        private val cell=view
        val logo= view.findViewById<ImageView>(R.id.image_estableci)
        val nombre= view.findViewById<TextView>(R.id.textNomEstab)
        val descrip= view.findViewById<TextView>(R.id.textDescEstab)

        init{
            this.cell.setOnClickListener(this)
        }

        override fun onClick(v: View?){
            this.listener.dataPosition(adapterPosition)
        }

    }
}

