package com.denzo.daschess

import android.os.Bundle
import android.view.MotionEvent
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
            when (item.itemId) {
                R.id.nav_play -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                R.id.nav_puzzles -> {
                    loadFragment(PuzzlesFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
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
            .commit()
    }

    fun startNewGame(isAiEnabled: Boolean = false) {
        bottomNavigation.selectedItemId = R.id.nav_play // Or we could have a separate "Game" screen
        // For now, let's load GameFragment into the container
        val fragment = GameFragment()
        val args = Bundle()
        fragment.arguments = args.apply { putBoolean("isAiEnabled", isAiEnabled) }
        loadFragment(fragment)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is GameFragment) {
            val chessboard = currentFragment.view?.findViewById<ChessboardView>(R.id.chessboard)
            if (chessboard != null) {
                currentFragment.sendInputToPresenter(chessboard.currentChosenPos, chessboard.previousChosenPos)
            }
        }
        return super.onTouchEvent(event)
    }
}