package com.example.alianzas

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alianzas.Interface.ClickListener
import com.example.alianzas.Modelos.Anuncio
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var list : RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter : RecyclerAdapterHome
    private var anunciosArray: ArrayList<Anuncio> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        list = view.findViewById(R.id.anuncioListView)
        listener?.activateHomeToolbar()
        layoutManager = LinearLayoutManager(context)
        adapter = RecyclerAdapterHome(
            anunciosArray,
            object :
                ClickListener {
                override fun dataPosition(position: Int) {
                    Toast.makeText(context, "Cell position $position", Toast.LENGTH_SHORT).show()
                    onCellPressed(anunciosArray, position)
                }
            })
        list.layoutManager = layoutManager
        list.adapter = adapter

        adapter.notifyDataSetChanged()
        return view

    }
    fun onCellPressed(data: ArrayList<Anuncio>, num: Int){
        listener?.onFragmentRecyclerInteraction(data[num].Imagen!!, data[num].Detalle!!)
    }
//    funcion para obtener datos con real time
    fun addData(){
        val ref = FirebaseDatabase.getInstance().getReference("/Anuncios")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.w("DATA_STATUS", "Failed to read value ${p0.toException()}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                    p0.children.forEach {
                        Log.d("DATA_STATUS", it.toString())
                        val dataDownload = it.getValue(Anuncio::class.java)
                        anunciosArray.add(Anuncio(dataDownload?.Nombre, dataDownload?.Fecha, dataDownload?.Imagen,dataDownload?.Detalle))
                    }
                adapter.notifyDataSetChanged()
                Log.w("DOWNLOAD_DATA", "Data: $anunciosArray")
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentRecyclerInteraction(anuncioImage: String, anuncioDetail: String)
        fun activateHomeToolbar()
    }
}
