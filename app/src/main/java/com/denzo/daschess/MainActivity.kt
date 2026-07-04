package com.denzo.daschess

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.denzo.daschess.customviews.ChessboardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            
            when (item.itemId) {
                R.id.nav_play -> {
                    if (currentFragment !is HomeFragment && currentFragment !is GameFragment) {
                        loadFragment(HomeFragment())
                    }
                    true
                }
                R.id.nav_history -> {
                    if (currentFragment !is HistoryFragment) loadFragment(HistoryFragment())
                    true
                }
                R.id.nav_puzzles -> {
                    if (currentFragment !is PuzzlesFragment) loadFragment(PuzzlesFragment())
                    true
                }
                R.id.nav_profile -> {
                    if (currentFragment !is ProfileFragment) loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitAllowingStateLoss()
    }

    fun startNewGame(isAiEnabled: Boolean = false) {
        android.widget.Toast.makeText(this, "Starting Game...", android.widget.Toast.LENGTH_SHORT).show()
        // For now, let's load GameFragment into the container
        val fragment = GameFragment()
        val args = Bundle()
        fragment.arguments = args.apply { putBoolean("isAiEnabled", isAiEnabled) }
        loadFragment(fragment)
    }
}