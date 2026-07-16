package com.denzo.daschess

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class LoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_login).setOnClickListener {
            val email = view.findViewById<android.widget.EditText>(R.id.et_email).text.toString()
            val password = view.findViewById<android.widget.EditText>(R.id.et_password).text.toString()
            
            if (email.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Email cannot be empty", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!email.contains("@")) {
                android.widget.Toast.makeText(requireContext(), "Invalid email format", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password.length < 6) {
                android.widget.Toast.makeText(requireContext(), "Password must be at least 6 characters", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNotEmpty()) {
                UserSession.userName = email.split("@")[0].replaceFirstChar { it.uppercase() }
                UserSession.userEmail = email
                UserSession.isGuest = false
            }
            
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        view.findViewById<TextView>(R.id.tv_go_to_register).setOnClickListener {
            (activity as? AuthActivity)?.showRegister()
        }

        view.findViewById<TextView>(R.id.tv_skip_login).setOnClickListener {
            UserSession.userName = "Guest"
            UserSession.isGuest = true
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}