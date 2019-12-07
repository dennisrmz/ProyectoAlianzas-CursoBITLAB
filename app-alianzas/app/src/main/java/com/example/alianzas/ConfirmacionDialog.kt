package com.example.alianzas

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ConfirmacionDialog : DialogFragment(){

    internal lateinit var listener: NoticeDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.dialogFragment_mensaje)
                .setPositiveButton(R.string.dialogFragment_aceptar)
               // .setNegativeButton(R.string.dialogFragment_cancelar)
                { _, _ ->
                    listener.onDialogPositiveClick()
                }
                .setNegativeButton(R.string.dialogFragment_cancelar)
                { _, _ ->
                    dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("")
    }

    interface NoticeDialogListener {
        fun onDialogPositiveClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            listener = targetFragment as NoticeDialogListener
        } catch (e: ClassCastException) {
            Log.e("LISTENER_STATUS", "onAttach: ClassCastException : " + e.message)
        }
    }
}