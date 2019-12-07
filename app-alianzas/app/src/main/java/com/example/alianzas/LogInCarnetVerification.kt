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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_log_in_carnet_verification.*
import java.util.*
import java.util.jar.Manifest

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LogInCarnetVerification : Fragment(), CameraGalleryDialog.NoticeDialogListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var carnetImage: ImageView
    private lateinit var addImageIcon: ImageView
    private var imageUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var carnetScreenContainer: FrameLayout
    private lateinit var carnetProgressBar: FrameLayout
    private lateinit var db: FirebaseFirestore
    private lateinit var uploadImage: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_log_in_carnet_verification, container, false)
        carnetImage = view.findViewById(R.id.login_carnet_verification_imageView)
        addImageIcon = view.findViewById(R.id.carnet_verification_addPhoto_icon)
        carnetScreenContainer = view.findViewById(R.id.carnetScreen_container)
        carnetProgressBar = view.findViewById(R.id.frameLayout_dataBaseProgress_container)
        uploadImage = view.findViewById(R.id.login_carnet_verification_upload_image)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        Log.d("EMAIL_STATUS", "${currentUser?.email}")

        blockView(true)

        checkCredentials()

        carnetImage.setOnClickListener {
            showDialog()
        }

        addImageIcon.setOnClickListener {
            showDialog()
        }

        uploadImage.setOnClickListener {
            blockView(true)
            uploadImageFirebase()
        }

        uploadImage.isEnabled = false

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed() {
        listener?.onLogInCarnetVerificationInteraction()
    }


    //Block view from user interaction for a safe check in server.
    private fun blockView(state: Boolean) {
        if (state) {
            uploadImage.isEnabled = false
            carnetScreenContainer.visibility = View.GONE
            carnetProgressBar.visibility = View.VISIBLE
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            uploadImage.isEnabled = false
            carnetScreenContainer.visibility = View.VISIBLE
            carnetProgressBar.visibility = View.GONE
            activity?.window?.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        carnetProgressBar.visibility = View.GONE
    }

    //Check if the user has already created his credentials if not create new ones to the user.
    private fun checkCredentials() {
        val currentUser = auth.currentUser
        db.collection("usuarios")
            .document("${currentUser?.email}")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.exists()) {
                    carnetProgressBar.visibility = View.GONE
                    Toast.makeText(context, "The user already exist", Toast.LENGTH_SHORT).show()
                    Log.d("SUCCESS_QUERY_STATUS", "${documents.exists()}")
                    activity?.window?.clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    listener?.onAlreadyGotCredentials()
                } else {
                    blockView(false)
                    Toast.makeText(context, "The user doesn't exist", Toast.LENGTH_SHORT).show()
                    Log.d("SUCCESS_QUERY_STATUS", "${documents.exists()}")
                }
            }
            .addOnFailureListener { exception ->
                blockView(false)
                Log.w("FAILURE_QUERY_STATUS", "Error getting documents: ", exception)
            }
    }


    private fun createCredentials(image: String, file: String) {
        val currentUser = auth.currentUser
        val user = hashMapOf(
            "email" to "${currentUser?.email}",
            "imageURL" to image,
            "fileName" to  file,
            "Codigo" to "-------------",
            "Colaborador" to "-------------",
            "Departamento" to "-------------"
        )
        db.collection("usuarios").document("${currentUser?.email}")
            .set(user)
            .addOnSuccessListener {
                Log.d("CREDENTIALS_STATUS", "DocumentSnapshot successfully written!")
                blockView(false)
                listener?.onAlreadyGotCredentials()
            }
            .addOnFailureListener { e ->
                Log.w("CREDENTIALS_STATUS", "Error writing document", e)
                blockView(false)
            }

    }


    private fun uploadImageFirebase() {
        if (imageUri == null ) {
            blockView(false)
            Log.d("STATUS_IMAGE", "We have a problem with the uri}")
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images_users/$filename")

        ref.putFile(imageUri!!)
            .addOnSuccessListener { image ->
                Log.d("StatusRegister", "Successfully uploaded image: ${image.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("StatusRegister", "File Location: $uri")
                    createCredentials(uri.toString(), filename)
                }
            }
            .addOnFailureListener {
                Log.d("StatusRegister", "Successfully uploaded image: ${it.message}")
                blockView(false)
            }
    }


    private fun showDialog() {
        val cameraGalleryDialog = CameraGalleryDialog()
        cameraGalleryDialog.setTargetFragment(this@LogInCarnetVerification, 1)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Called when image was captured from the camera intent
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            //set image captured to image view
            carnetImage.setImageURI(imageUri)
            uploadImage.isEnabled = true
        } else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            carnetImage.setImageURI(data?.data)
            uploadImage.isEnabled = true
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onLogInCarnetVerificationInteraction()
        fun onAlreadyGotCredentials()
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LogInCarnetVerification().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
