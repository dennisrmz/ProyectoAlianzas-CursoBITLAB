package com.example.alianzas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alianzas.Interface.ClickListener
import com.example.alianzas.Modelos.Anuncio
import com.squareup.picasso.Picasso

class RecyclerAdapterHome(private val anuncios: ArrayList<Anuncio>, private val clickListener: ClickListener) : RecyclerView.Adapter<RecyclerAdapterHome.ViewHolder>(){

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_home, parent, false)
        return ViewHolder(layoutInflater, clickListener)
    }

    override fun getItemCount(): Int {
        return anuncios.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = anuncios.get(position)
        holder.bind(item)
    }
// cambiando nombre de variables para real time
    inner class ViewHolder(view: View, var listener: ClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener{
        private val cell = view
        val nombre = view.findViewById<TextView>(R.id.textNombre)
        val fecha = view.findViewById<TextView>(R.id.textFecha)
        val imagen = view.findViewById<ImageView>(R.id.imageAnuncio)

        fun bind(anuncio : Anuncio){
            nombre.text = anuncio.Nombre
            fecha.text = anuncio.Fecha
            imagen.loadUrl(anuncio.Imagen!!)
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