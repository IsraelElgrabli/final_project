package com.example.final_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.final_project.auth.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav)
        session = SessionManager(this)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        // Choose start destination based on login status
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.nav)
        graph.setStartDestination(
            if (session.username == null) R.id.simpleLoginFragment else R.id.feedFragment
        )
        navController.graph = graph

        // Hook up BottomNavigationView with NavController
        bottomNav.setupWithNavController(navController)

        // Show bottom nav only on top-level destinations
        val topLevel = setOf(
            R.id.feedFragment, R.id.createPostFragment, R.id.profileFragment
        )
        navController.addOnDestinationChangedListener { _, dest, _ ->
            bottomNav.isVisible = dest.id in topLevel
        }
    }
}
