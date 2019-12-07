package com.example.alianzas

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LogInFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var displayLogInProblem: TextView
    private lateinit var continueButton: Button
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var logInContainer: FrameLayout
    private lateinit var logInProgressBarContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_log_in, container, false)
        displayLogInProblem = view.findViewById(R.id.textView_login_problem)
        continueButton = view.findViewById(R.id.button_login_continue)
        emailField = view.findViewById(R.id.editText_login_email)
        passwordField = view.findViewById(R.id.editText_login_password)
        logInContainer = view.findViewById(R.id.frameLayout_login_container)
        logInProgressBarContainer = view.findViewById(R.id.frameLayout_loginProgress_container)
        auth = FirebaseAuth.getInstance()

        continueButton.setOnClickListener {
            checkFields(emailField, passwordField)

        }

        displayLogInProblem.setOnClickListener {
            showLoginProblemDialog()
        }



        return view
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            listener?.onLogInFragmentInteraction()
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    private fun onButtonPressed() {
        listener?.onLogInFragmentInteraction()
    }

    private fun checkFields(email: EditText, password: EditText) {
        val emailText = email.text.toString()
        val passwordText = password.text.toString()

        if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
            logInUser(emailText, passwordText)
            logInContainer.alpha = 0.5f
            logInProgressBarContainer.visibility = View.VISIBLE
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            if (emailText == "") {
                email.requestFocus()
                email.error = "Campo requerido"
            }
            if (passwordText == "") {
                password.requestFocus()
                password.error = "Campo requerido"
            }
        }
    }

    private fun logInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LOGIN_STATUS", "signInWithEmail:success")
                    Toast.makeText(context, "Good to go!", Toast.LENGTH_SHORT).show()
                    logInContainer.alpha = 1.0f
                    logInProgressBarContainer.visibility = View.GONE
                    activity?.window?.clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    listener?.onLogInFragmentInteraction()
                } else {
                    Log.w("LOGIN_STATUS", "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "${task.exception?.message}", Toast.LENGTH_LONG).show()
                    logInContainer.alpha = 1.0f
                    logInProgressBarContainer.visibility = View.GONE
                    activity?.window?.clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
            }
    }

    private fun showLoginProblemDialog() {
        val logInProblemDialog = LogInProblemDialog()
        logInProblemDialog.show(fragmentManager!!, "TestDialog_TAG")
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
        fun onLogInFragmentInteraction()
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LogInFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
