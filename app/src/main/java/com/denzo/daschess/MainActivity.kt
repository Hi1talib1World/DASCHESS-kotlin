package com.denzo.daschess

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.denzo.daschess.customviews.ChessboardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View
import android.widget.Button
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var matchmakingOverlay: View
    private lateinit var fullScreenMenu: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fullScreenMenu = findViewById(R.id.full_screen_menu)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            fullScreenMenu.visibility = View.VISIBLE
        }

        findViewById<View>(R.id.btn_close_menu).setOnClickListener {
            fullScreenMenu.visibility = View.GONE
        }

        setupMenuClickListeners()

        matchmakingOverlay = findViewById(R.id.matchmaking_overlay)
        findViewById<Button>(R.id.btn_cancel_matchmaking).setOnClickListener {
            matchmakingOverlay.visibility = View.GONE
        }

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            
            if (item.itemId == R.id.nav_play) {
                if (currentFragment !is HomeFragment && currentFragment !is GameFragment) {
                    loadFragment(HomeFragment())
                    true
                } else {
                    false
                }
            } else if (item.itemId == R.id.nav_history) {
                if (currentFragment !is HistoryFragment) {
                    loadFragment(HistoryFragment())
                    true
                } else {
                    false
                }
            } else if (item.itemId == R.id.nav_puzzles) {
                if (currentFragment !is PuzzlesFragment) {
                    loadFragment(PuzzlesFragment())
                    true
                } else {
                    false
                }
            } else if (item.itemId == R.id.nav_profile) {
                if (currentFragment !is ProfileFragment) {
                    loadFragment(ProfileFragment())
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun setupMenuClickListeners() {
        val menuItems = mapOf(
            R.id.menu_collections to "Collections",
            R.id.menu_games_db to "Games Database",
            R.id.menu_chess_terms to "Chess Terms",
            R.id.menu_rules to "Rules",
            R.id.menu_explore to "Explore",
            R.id.menu_vote_chess to "Vote Chess",
            R.id.menu_solo_chess to "Solo Chess",
            R.id.menu_comp_champ to "Computer Championship",
            R.id.menu_chesskid to "ChessKid",
            R.id.menu_tools to "Tools",
            R.id.menu_vision to "Vision",
            R.id.menu_shop to "Shop",
            R.id.menu_merch to "Merch",
            R.id.menu_gift to "Gift"
        )

        menuItems.forEach { (id, name) ->
            findViewById<View>(id).setOnClickListener {
                android.widget.Toast.makeText(this, "$name coming soon!", android.widget.Toast.LENGTH_SHORT).show()
                fullScreenMenu.visibility = View.GONE
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitAllowingStateLoss()
    }

    fun startNewGame(isAiEnabled: Boolean = false) {
        if (isAiEnabled) {
            android.widget.Toast.makeText(this, "Starting AI Match...", android.widget.Toast.LENGTH_SHORT).show()
            loadGameFragment(true)
        } else {
            startMatchmaking()
        }
    }

    private fun startMatchmaking() {
        matchmakingOverlay.visibility = View.VISIBLE
        // Simulate searching for 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            if (matchmakingOverlay.visibility == View.VISIBLE) {
                matchmakingOverlay.visibility = View.GONE
                android.widget.Toast.makeText(this, "Match Found! You are playing as White.", android.widget.Toast.LENGTH_LONG).show()
                loadGameFragment(false)
            }
        }, 3000)
    }

    private fun loadGameFragment(isAiEnabled: Boolean) {
        val fragment = GameFragment()
        val args = Bundle()
        fragment.arguments = args.apply { putBoolean("isAiEnabled", isAiEnabled) }
        loadFragment(fragment)
    }

    fun navigateToHistory() {
        bottomNavigation.selectedItemId = R.id.nav_history
    }

    fun navigateToPuzzles() {
        bottomNavigation.selectedItemId = R.id.nav_puzzles
    }
}