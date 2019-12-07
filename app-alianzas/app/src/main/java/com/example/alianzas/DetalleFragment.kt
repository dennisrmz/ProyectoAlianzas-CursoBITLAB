package com.example.alianzas

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alianzas.Models.Comida
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detalle.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "nombre"
private const val ARG_PARAM2 = "descripcion"
private const val ARG_PARAM3 = "imagen"


class DetalleFragment : Fragment(), ConfirmacionDialog.NoticeDialogListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var param3: String? = null

    private var listener: OnFragmentRecyclerInteractionListener? = null
    private lateinit var confirmarUtilizar: Button
    private lateinit var Nombre: TextView
    private lateinit var Descripcion: TextView
    private lateinit var Imagen: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1=it.getString(ARG_PARAM1)
            param2=it.getString(ARG_PARAM2)
            param3=it.getString(ARG_PARAM3)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_detalle, container, false)
        confirmarUtilizar= view.findViewById(R.id.button_utilizar)
        listener?.activatePromocionToolbar()
        Nombre= view.findViewById(R.id.textNomPromo)
        Imagen=view.findViewById(R.id.image_promocion)
        Descripcion=view.findViewById(R.id.textDescripcionPromo)

        Nombre.text = "$param1"
        context?.let { Glide.with(it).load(param2).into(Imagen) }
        Descripcion.text= "$param3"


        confirmarUtilizar.setOnClickListener{
            showConfirmacionDialog()
        }

        return  view
    }



    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed() {
        listener?.onFragmentDetailInteraction()
    }

    override fun onDialogPositiveClick() {
        Toast.makeText(context,"This is detalle fragment toast", Toast.LENGTH_SHORT).show()
        listener?.onFragmentDetailInteraction()
    }

    private fun showConfirmacionDialog(){
        val confirmarDialog= ConfirmacionDialog()
        confirmarDialog.setTargetFragment(this@DetalleFragment, 1)
        fragmentManager?.let { fragmentManager -> confirmarDialog.show(fragmentManager, "CameraGalleryDialog") }
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
        fun activatePromocionToolbar()
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String) =
            DetalleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                    Log.d("Data_status", "DATA: $param1, $param2, $param3")

                }
            }
    }
}
