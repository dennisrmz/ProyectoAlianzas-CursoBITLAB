package com.example.alianzas

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

private const val ARG_PARAM1 = "anuncioImage"
private const val ARG_PARAM2 = "anuncioDetail"

class fragmentDetailAnuncios : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentRecyclerInteractionListener? = null
    private lateinit var anuncioImage: ImageView
    private lateinit var anuncioDetail: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_datail_anuncios, container, false)
        anuncioImage = view.findViewById(R.id.imageAnuncioDetail)
        anuncioDetail = view.findViewById(R.id.textAnuncioDetail)
        listener?.activateAnuncioDetalleToolbar()
        anuncioDetail.text = "$param2"
        Picasso.get().load(param1).into(anuncioImage)

        return view
    }


    fun onButtonPressed() {
        listener?.onFragmentDetailInteraction()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentRecyclerInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentRecyclerInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentDetailInteraction()
        fun activateAnuncioDetalleToolbar()
}

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragmentDetailAnuncios().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    Log.d("DATA_STATUS", "DATA: $param1, $param2")
                }
            }
    }
}


