package com.example.playlistmaker.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.findNavController
import com.example.playlistmaker.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(
            findViewById<BottomNavigationView>(R.id.bottomNavigationView),
            navController
        )

        navigateIfRequested(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navigateIfRequested(intent)
    }

    private fun navigateIfRequested(intent: Intent?) {
        if (intent?.getBooleanExtra("open_create_playlist", false) == true) {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            navHostFragment?.navController?.navigate(R.id.action_library_fragment_to_createPlaylistFragment)
        }
    }
}
