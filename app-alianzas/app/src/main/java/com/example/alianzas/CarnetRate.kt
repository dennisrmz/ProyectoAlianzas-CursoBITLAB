package com.example.alianzas

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.bumptech.glide.Glide
import com.example.alianzas.Modelos.RateCarnet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CarnetRate : Fragment(), CameraGalleryDialog.NoticeDialogListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var imageView: ImageView
    private lateinit var colaborador: TextView
    private lateinit var departamento: TextView
    private lateinit var codigo: TextView
    private lateinit var continuar: Button
    private lateinit var carnetContainer: FrameLayout
    private lateinit var progressBarContainer: FrameLayout
    private var imageUri: Uri? = null
    private var dataArray: ArrayList<RateCarnet> = arrayListOf()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_carnet_rate, container, false)
        imageView = view.findViewById(R.id.imageView_rate_carnet)
        colaborador = view.findViewById(R.id.textView_nombre_colaborador)
        departamento = view.findViewById(R.id.textView_colaborador_departamento)
        codigo = view.findViewById(R.id.textView_codigo_colaborador)
        continuar = view.findViewById(R.id.button_showRate)
        carnetContainer = view.findViewById(R.id.carnet_container)
        progressBarContainer = view.findViewById(R.id.frameContainer_progressBar)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        imageView.setOnClickListener{
            showDialog()
        }

        continuar.setOnClickListener {
            listener?.onCarnetRateFragmentInteraction()
        }

        getData()

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event

    private fun getData() {
        val currentUser = auth.currentUser
        val docRef = db.collection("usuarios").document("${currentUser?.email}")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val codigoData = document.getString("Codigo")
                    val colaboradorData = document.getString("Colaborador")
                    val departamentoData = document.getString("Departamento")
                    val imageData = document.getString("imageURL")

//                    Glide.with(this).load(imageData).into(imageView)
                    Picasso.get().load(imageData).into(imageView)
                    colaborador.text = colaboradorData
                    departamento.text = departamentoData
                    codigo.text = codigoData

                    Log.d("DATA_STATUS_CARNET_RATE", "DocumentSnapshot data: ${colaboradorData}")
                } else {
                    Log.d("DATA_STATUS_CARNET_RATE", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("DATA_STATUS_CARNET_RATE", "get failed with ", exception)
            }
    }

    private fun blockView(state: Boolean) {
        if (state) {
            carnetContainer.visibility = View.GONE
            progressBarContainer.visibility = View.VISIBLE
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            carnetContainer.visibility = View.VISIBLE
            progressBarContainer.visibility = View.GONE
            activity?.window?.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    private fun changeImage(image: String, file: String) {
        db.collection("usuarios").document("colaborador4@unicomer.com")
            .update(
                mapOf(
                    "imageURL" to image,
                    "fileName" to file
                )
            )
            .addOnSuccessListener {
                blockView(false)
                Log.d(
                    "CREDENTIALS_STATUS",
                    "DocumentSnapshot successfully updated!"
                )
            }
            .addOnFailureListener { e ->
                blockView(false)
                Log.w("CREDENTIALS_STATUS", "Error updating document", e)
            }
    }

    private fun uploadImageFirebase() {
        if (imageUri == null ) {
            Log.d("UPLOAD_STATUS", "The uri is null")
            return
        }

        blockView(true)

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images-users/$filename")

        ref.putFile(imageUri!!)
            .addOnSuccessListener { image ->
                Log.d("StatusRegister", "Successfully uploaded image: ${image.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("StatusRegister", "File Location: $uri")
                    changeImage(uri.toString(), filename)
                }
            }
            .addOnFailureListener {
                Log.d("StatusRegister", "Successfully uploaded image: ${it.message}")
            }
    }

    private fun showDialog() {
        val cameraGalleryDialog = CameraGalleryDialog()
        cameraGalleryDialog.setTargetFragment(this@CarnetRate, 1)
        fragmentManager?.let { fragmentManager -> cameraGalleryDialog.show(fragmentManager, "CameraGalleryDialog") }
    }

    override fun onDialogPositiveClick(position: Int) {
        if (position == 0) {
            //if system os is Marshmallow or above, we need to request runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity!!.checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    activity!!.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //permission was not enabled
                    Log.d("PERMISSION_STATUS", "NOT GRANTED")
                    val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE_CAMERA)
                } else {
                    //permission already granted
                    Log.d("PERMISSION_STATUS", "YES GRANTED")
                    openCamera()
                }
            } else {
                //system os < Marshmallow
                openCamera()
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity!!.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //permission denied
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE_GALLERY)
                } else {
                    //permission already granted
                    pickImageFromGallery()
                }
            } else {
                //System OS is < Marshmallow
                pickImageFromGallery()
            }
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)

        Log.d("RESULT_STATUS", "$imageUri")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses ALLOW or DENY from permission request popup
        when (requestCode){
            PERMISSION_CODE_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission from popup was granted
                    openCamera()
                } else {
                    //Permission from popup was denied
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_CODE_GALLERY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission from popup granted
                    pickImageFromGallery()
                } else {
                    //Permission from popup denied
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Called when image was captured from the camera intent
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            //set image captured to image view
            imageView.setImageURI(imageUri)
            uploadImageFirebase()
        } else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageView.setImageURI(data?.data)
            imageUri = data?.data
            uploadImageFirebase()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onCarnetRateFragmentInteraction()
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CarnetRate().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
