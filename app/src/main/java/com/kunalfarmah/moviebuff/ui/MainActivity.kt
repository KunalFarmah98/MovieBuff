package com.kunalfarmah.moviebuff.ui

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.transition.*
import androidx.transition.TransitionSet.ORDERING_TOGETHER
import com.google.android.material.navigation.NavigationView
import com.kunalfarmah.moviebuff.R
import com.kunalfarmah.moviebuff.viewmodel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Popular Movies"


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        this.supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment, MovieListFragment(), MovieListFragment.TAG)
            .commit()

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> {
                    this.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, MovieListFragment(), MovieListFragment.TAG)
                        .commit()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.nav_about -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    var intent = Intent(ACTION_VIEW)
                    intent.data = Uri.parse("https://kunal-farmah.jimdosite.com")
                    startActivity(intent)
                }

            }

            return@setNavigationItemSelectedListener true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.movies, menu)
        return true
    }

    override fun onBackPressed() {
        supportActionBar?.show()
        if(null!=supportFragmentManager.findFragmentByTag(MovieDetailFragment.TAG))
            supportFragmentManager.popBackStack()
        else
            super.onBackPressed()
    }
}