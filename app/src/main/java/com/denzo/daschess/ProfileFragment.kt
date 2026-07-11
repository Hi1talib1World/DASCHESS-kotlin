package com.denzo.daschess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.denzo.daschess.data.ChessRepository
import com.denzo.daschess.models.UserStats

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val repository = ChessRepository()
        val factory = ProfileViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers(view)
        setupClickListeners(view)

        viewModel.loadProfileData()
    }

    private fun setupObservers(view: View) {
        viewModel.userStats.observe(viewLifecycleOwner, Observer { stats ->
            updateProfileUI(view, stats)
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            view.findViewById<View>(R.id.loading_overlay)?.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
        })
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<Button>(R.id.btn_edit_profile).setOnClickListener {
            Toast.makeText(context, "Edit Profile coming soon!", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btn_settings).setOnClickListener {
            Toast.makeText(context, "Settings coming soon!", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btn_sign_out).setOnClickListener {
            UserSession.userName = "Guest"
            UserSession.isGuest = true
            val intent = android.content.Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun updateProfileUI(view: View, stats: UserStats) {
        val name = if (UserSession.isGuest) "Guest" else UserSession.userName
        view.findViewById<TextView>(R.id.profile_name).text = name
        view.findViewById<TextView>(R.id.profile_title).text = stats.title
        view.findViewById<TextView>(R.id.stat_total_games).text = stats.totalGames.toString()
        view.findViewById<TextView>(R.id.stat_win_rate).text = "${stats.winRate}%"
        view.findViewById<TextView>(R.id.stat_best_win).text = stats.bestWin.toString()
        view.findViewById<TextView>(R.id.stat_accuracy).text = "${stats.accuracy}%"
    }
}

class ProfileViewModelFactory(private val repository: ChessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository) as T
    }
}