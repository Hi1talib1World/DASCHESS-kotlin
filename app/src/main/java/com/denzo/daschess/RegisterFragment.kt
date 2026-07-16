package com.denzo.daschess

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class RegisterFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_register).setOnClickListener {
            val name = view.findViewById<android.widget.EditText>(R.id.et_reg_name).text.toString()
            val email = view.findViewById<android.widget.EditText>(R.id.et_reg_email).text.toString()
            val password = view.findViewById<android.widget.EditText>(R.id.et_reg_password).text.toString()

            if (name.length < 3) {
                android.widget.Toast.makeText(requireContext(), "Name too short", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (!email.contains("@") || !email.contains(".")) {
                android.widget.Toast.makeText(requireContext(), "Invalid email address", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password.length < 8) {
                android.widget.Toast.makeText(requireContext(), "Password must be at least 8 chars", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isNotEmpty()) {
                UserSession.userName = name
                UserSession.isGuest = false
            }

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}