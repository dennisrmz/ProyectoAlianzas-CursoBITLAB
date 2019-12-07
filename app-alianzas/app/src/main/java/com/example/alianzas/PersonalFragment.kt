package com.example.alianzas

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alianzas.Modelos.Personal
import com.google.firebase.database.*


class PersonalFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    var fragmentviewPersonal : View? = null

    lateinit var mRecyclerViewPersonal : RecyclerView
    val mAdapterPersonal : RecyclerAdapterPersonal = RecyclerAdapterPersonal()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        listener?.activatePersonalToolbar()
        fragmentviewPersonal = inflater.inflate(R.layout.fragment_personal, container, false)

        getAccounts()

        return fragmentviewPersonal
    }
    fun getAccounts(){
//        cambiando funcion para real time
        var personales:MutableList<Personal> = mutableListOf()
        val ref = FirebaseDatabase.getInstance().getReference("/Personal")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.w("DATA_STATUS", "Failed to read value ${p0.toException()}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                    p0.children.forEach {
                        Log.d("DATA_STATUS", it.toString())
                        val dataDownload = it.getValue(Personal::class.java)
                        personales.add(Personal(dataDownload?.Nombre, dataDownload?.Imagen))
                        setUpRecyclerView(personales)
                    }
                Log.w("DOWNLOAD_DATA", "Data: $personales")
            }
        })
    }
    fun setUpRecyclerView(personal: MutableList<Personal>){
        val activity = activity as Context
        mRecyclerViewPersonal= this.fragmentviewPersonal!!.findViewById(R.id.personalListView) as RecyclerView
        mRecyclerViewPersonal.setHasFixedSize(true)
        mRecyclerViewPersonal.layoutManager = LinearLayoutManager(activity)
        mAdapterPersonal.RecyclerAdapterPersonal(personal, activity)
        mRecyclerViewPersonal.adapter = mAdapterPersonal
        mAdapterPersonal.notifyDataSetChanged()
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
        fun activatePersonalToolbar()
    }
}
