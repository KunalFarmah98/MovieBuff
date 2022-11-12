package com.kunalfarmah.moviebuff.ui

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.kunalfarmah.moviebuff.R
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

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment, MovieListFragment(), MovieListFragment.TAG)
            .commit()

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