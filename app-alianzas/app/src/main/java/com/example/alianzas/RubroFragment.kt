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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alianzas.Interface.ClickListener
import com.example.alianzas.Modelos.Rubro
import com.google.firebase.database.*
class RubroFragment: Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var list : RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter : RecyclerAdapterRubros
    private var rubrosArray: ArrayList<Rubro> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addData()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_rubro, container, false)
        listener?.activateBottomNavigation()
        listener?.activateRubrolToolbar()
        list = view.findViewById(R.id.rubrosListView)
        layoutManager = GridLayoutManager(context,2)
        adapter = RecyclerAdapterRubros(
            rubrosArray,
            object :
                ClickListener {
                override fun dataPosition(position: Int) {
                    Toast.makeText(context, "Cell position $position", Toast.LENGTH_SHORT).show()
                    onCellPressed(rubrosArray, position)
                }
            })
        list.layoutManager = layoutManager
        list.adapter = adapter

        adapter.notifyDataSetChanged()
        return view
    }
    fun onCellPressed(data: ArrayList<Rubro>, num: Int){
        listener?.onFragmentRecyclerInteractionRubro(data[num].Query!!)
    }


    private fun addData(){
    val ref = FirebaseDatabase.getInstance().getReference("/Rubros")
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Log.w("DATA_STATUS", "Failed to read value ${p0.toException()}")
        }

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists())
                p0.children.forEach {
                    Log.d("DATA_STATUS", it.toString())
                    val dataDownload = it.getValue(Rubro::class.java)
                    rubrosArray.add(Rubro(dataDownload?.Description, dataDownload?.ImageURL,dataDownload?.Query))
                }
            adapter.notifyDataSetChanged()
            Log.w("DOWNLOAD_DATA", "Data: $rubrosArray")
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
        fun onFragmentRecyclerInteractionRubro(query: String)
        fun activateBottomNavigation()
        fun activateRubrolToolbar()
    }

}
