package com.example.alianzas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alianzas.Modelos.Personal
import com.squareup.picasso.Picasso

class RecyclerAdapterPersonal : RecyclerView.Adapter<RecyclerAdapterPersonal.ViewHolder>(){

    var personal: MutableList<Personal> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapterPersonal(personal: MutableList<Personal>, context: Context){
        this.personal = personal
        this.context = context

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_layout_personal, parent, false))
    }

    override fun getItemCount(): Int {
        return personal.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = personal.get(position)

        holder.bind(item)


    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val nombre = view.findViewById<TextView>(R.id.nombrePersonal)
        val imagen = view.findViewById<ImageView>(R.id.imagePersonal)

//cambiando nombre de variable para real time
        fun bind(personal: Personal){
            nombre.text = personal.Nombre
            imagen.loadUrl(personal.Imagen!!)
        }
        fun ImageView.loadUrl(url: String){
            Picasso.get().load(url).into(this)
        }


    }

}
