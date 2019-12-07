package com.example.alianzas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alianzas.Interface.ClickListener
import com.example.alianzas.Modelos.Rubro
import com.squareup.picasso.Picasso

class RecyclerAdapterRubros(private val rubros: ArrayList<Rubro>, private val clickListener: ClickListener) : RecyclerView.Adapter<RecyclerAdapterRubros.ViewHolder>(){

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_rubros, parent, false)
        return ViewHolder(layoutInflater, clickListener)
    }

    override fun getItemCount(): Int {
        return rubros.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = rubros.get(position)
        holder.bind(item)
    }

    inner class ViewHolder(view: View, var listener: ClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener{
        private val cell = view
        val nombre = view.findViewById<TextView>(R.id.nombreRubro)
        val imagen = view.findViewById<ImageView>(R.id.imageRubro)

        fun bind(rubro: Rubro){
            nombre.text = rubro.Description
            imagen.loadUrl(rubro.ImageURL!!)
        }
        fun ImageView.loadUrl(url: String){
            Picasso.get().load(url).into(this)
        }
        init {
            this.cell.setOnClickListener(this)
        }

        override fun onClick(v: View?){
            this.listener.dataPosition(adapterPosition)
        }

    }
}
