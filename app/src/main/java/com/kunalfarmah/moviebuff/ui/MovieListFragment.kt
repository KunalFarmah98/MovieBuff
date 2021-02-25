package com.kunalfarmah.moviebuff.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.*
import com.kunalfarmah.moviebuff.R
import com.kunalfarmah.moviebuff.databinding.FragmentMovieListBinding
import com.kunalfarmah.moviebuff.listener.MovieListListener
import com.kunalfarmah.moviebuff.room.MovieEntity
import com.kunalfarmah.moviebuff.adapter.MoviesAdapter
import com.kunalfarmah.moviebuff.listener.MovieClickListener
import com.kunalfarmah.moviebuff.viewmodel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MovieListFragment() : Fragment(), MovieListListener, MovieClickListener {

    var binding: FragmentMovieListBinding? = null

    companion object {
        var isGrid = true
        final var TAG = "MovieListFragment"
    }

    private val viewModel: MoviesViewModel by viewModels()
    private lateinit var mAdapter: MoviesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentMovieListBinding.inflate(layoutInflater)

        fetchData()

        viewModel.movies.observe(viewLifecycleOwner) {
            var movieList = viewModel.movies.value
            if (null != movieList && !movieList.isEmpty()) {
                binding!!.shimmerFrameLayout.stopShimmerAnimation()
                binding!!.shimmerFrameLayout.visibility = View.GONE
                binding!!.noInternet.visibility = View.GONE

                mAdapter = MoviesAdapter(context, movieList, this)
                if (isGrid)
                    binding!!.movieList.layoutManager = GridLayoutManager(context, 2)
                else
                    binding!!.movieList.layoutManager = GridLayoutManager(context, 1)

                binding!!.movieList.setHasFixedSize(true)
                binding!!.movieList.setItemViewCacheSize(10)
                binding!!.movieList.adapter = mAdapter
                binding!!.movieList.visibility = View.VISIBLE
            } else {
                setNoInternetView()
            }
        }

        binding!!.retry.setOnClickListener { fetchData() }
        return binding?.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.movies, menu)

        val searchItem = menu.findItem(R.id.search)
        var searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Enter Title to Search"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchAllMovies(this@MovieListFragment, query)
                return true
            }


        })

        searchView.findViewById<ImageView>(R.id.search_close_btn)?.setOnClickListener {
            var et = it.findViewById<EditText>(R.id.search_src_text);
            et.setText("");
            searchView.setQuery("", false);
            searchView.onActionViewCollapsed()
            fetchData()
        }

        return
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.switchView -> {
                isGrid = !isGrid
                var span = 1
                if (isGrid) ++span
                binding?.movieList?.layoutManager = GridLayoutManager(context, span)
            }
            R.id.action_about -> {
                var intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://kunal-farmah.jimdosite.com")
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    override fun setView(list: ArrayList<MovieEntity>) {
        binding!!.noInternet.visibility = View.GONE
        binding!!.shimmerFrameLayout.stopShimmerAnimation()
        binding!!.shimmerFrameLayout.visibility = View.GONE
        mAdapter = MoviesAdapter(context, list, this)
        if (isGrid)
            binding!!.movieList.layoutManager = GridLayoutManager(context, 2)
        else
            binding!!.movieList.layoutManager = GridLayoutManager(context, 1)
        binding!!.movieList.setHasFixedSize(true)
        binding!!.movieList.setItemViewCacheSize(10)
        binding!!.movieList.adapter = mAdapter
        binding!!.movieList.visibility = View.VISIBLE
    }

    override fun setNoInternetView() {
        binding!!.shimmerFrameLayout.visibility = View.GONE
        binding!!.movieList.visibility = View.GONE
        binding!!.noInternet.visibility = View.VISIBLE
    }

    override fun onMovieClick(id: Int, image: ImageView) {
        viewModel.setSelectedMovie(id)
        var movieDetailFragment = MovieDetailFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            movieDetailFragment.sharedElementEnterTransition = DetailsTransition()
            movieDetailFragment.enterTransition = Fade()
            exitTransition = Fade()
            movieDetailFragment.sharedElementReturnTransition = DetailsTransition()
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.nav_host_fragment, movieDetailFragment)
                .addSharedElement(image, "imageTransition")
                .addToBackStack(MovieDetailFragment.TAG).commit()
        }
        else{
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.nav_host_fragment, movieDetailFragment)
                .addToBackStack(MovieDetailFragment.TAG).commit()
        }

    }

    class DetailsTransition : TransitionSet() {
        init {
            ordering = ORDERING_TOGETHER
            addTransition(ChangeBounds()).addTransition(ChangeTransform())
                .addTransition(ChangeImageTransform())
            duration = 2000
        }
    }

    fun fetchData() {
        binding!!.shimmerFrameLayout.visibility = View.VISIBLE
        binding!!.shimmerFrameLayout.startShimmerAnimation()
        if (isNetworkAvailable(requireContext())) {
            viewModel.fetchAllMovies(this)
        } else {
            viewModel.getAllMovies()
        }
    }

}