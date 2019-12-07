package com.example.alianzas

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alianzas.Models.Establecimiento
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "rubro"


class EstablecimientoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var mRecyclerView : RecyclerView
    private lateinit var mAdapter : RecyclerAdapterEstablecimiento
    private var datasArray: ArrayList<Establecimiento> = arrayListOf()
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        getComida()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_establecimiento, container, false)
        listener?.activateRubroDetalleToolbar()
        mRecyclerView = view.findViewById(R.id.listaEstablecimiento)
        Log.d(TAG, "EA: ${param1}")
        layoutManager = LinearLayoutManager(context)
        mAdapter = RecyclerAdapterEstablecimiento(
            datasArray, context!!,
            object :
                ClickListener {
                override fun dataPosition(position: Int) {
                    //Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
                    onCellPressed(datasArray, position)
                }
            })

        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.adapter = mAdapter


        mAdapter.notifyDataSetChanged()
        return view
    }

    fun onCellPressed(data: ArrayList<Establecimiento>, num: Int){
        listener?.onFragmentRecyclerEstabInteraction(data[num].Child!!, data[num].Query!!)
    }

    fun getComida() {
        val ref = FirebaseDatabase.getInstance().getReference("/$param1")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.w("DATA_STATUS", "Failed to read value ${p0.toException()}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                    p0.children.forEach {
                        Log.d("DATA_STATUS", it.toString())
                        val dataDownload = it.getValue(Establecimiento::class.java)
                        datasArray.add(Establecimiento(dataDownload?.Description, dataDownload?.ImageURL, dataDownload?.Query, dataDownload?.Title, dataDownload?.Child))
                    }
                mAdapter.notifyDataSetChanged()
                Log.w("DOWNLOAD_DATA", "Data: $datasArray")
            }
        })

    }

    // TODO: Rename method, update argument and hook method into UI event

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
        fun onFragmentRecyclerEstabInteraction(child: String?, query: String?)
        fun activateRubroDetalleToolbar()
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            EstablecimientoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}
