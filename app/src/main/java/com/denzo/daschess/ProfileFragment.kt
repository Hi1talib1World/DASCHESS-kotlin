package com.denzo.daschess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Reusing activity_profile or content_profil if they exist, or create new
        return inflater.inflate(R.layout.activity_profile, container, false)
    }
}