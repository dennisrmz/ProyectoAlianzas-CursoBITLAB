package com.example.alianzas

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alianzas.Models.Comida
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ComidaFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mAdapter : RecyclerAdapterComida
    private var listener: OnFragmentInteractionListener? = null

    private var dataArray: ArrayList<Comida> = arrayListOf()
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var verDetalle: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1= it.getString(ARG_PARAM1)
            param2= it.getString(ARG_PARAM2)

        }
        getComida()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_comida, container, false)
        Log.d(ContentValues.TAG, "ESTOY EN PROMOCIONES: ${param1} , ${param2}")
        mRecyclerView = view.findViewById(R.id.listaComida)
        listener?.activatePromocionesToolbar()
        layoutManager = LinearLayoutManager(context)
        mAdapter = RecyclerAdapterComida(
            dataArray, context!!,
            object :
                ClickListener {
                override fun dataPosition(position: Int) {
                   // Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
                    onCellPressed(dataArray, position)
                }
            })

        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.adapter = mAdapter

        mAdapter.notifyDataSetChanged()
        return view
    }

    fun onCellPressed(data: ArrayList<Comida>, num: Int){
        listener?.onFragmentRecyclerInteraction(data[num].ImageURL!!, data[num].Title!!, data[num].Description!!, data[num].Date!!)
    }

    fun getComida() {
        val ref = FirebaseDatabase.getInstance().getReference("/Promociones").child("/$param1").child("/$param2")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.w("DATA_STATUS", "Failed to read value ${p0.toException()}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                    p0.children.forEach {
                        Log.d("DATA_STATUS", it.toString())
                        val dataDownload = it.getValue(Comida::class.java)
                        dataArray.add(Comida(dataDownload?.Date, dataDownload?.Description, dataDownload?.ImageURL, dataDownload?.Title))
                    }
                mAdapter.notifyDataSetChanged()
                Log.w("DOWNLOAD_DATA", "Data: $dataArray")
            }
        })
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentRecyclerInteraction(Imagen: String, Nombre:String, Descripcion:String, Fecha:String)
        fun activatePromocionesToolbar()
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ComidaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)

                }
            }
    }
}
