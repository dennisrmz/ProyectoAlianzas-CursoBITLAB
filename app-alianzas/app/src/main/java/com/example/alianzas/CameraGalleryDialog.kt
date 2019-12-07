package com.example.alianzas

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class CameraGalleryDialog: DialogFragment() {

    private lateinit var listener: NoticeDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.carnet_verification_dialog_camera_gallery_title)
                .setItems(R.array.carnet_verification_dialog_camera_gallery_options
                ) { _, which ->
                    listener.onDialogPositiveClick(which)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface NoticeDialogListener {
        fun onDialogPositiveClick(position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = targetFragment as NoticeDialogListener
        } catch (e: ClassCastException) {
            Log.e("LISTENER_STATUS", "onAttach: ClassCastException : " + e.message)
        }
    }

}