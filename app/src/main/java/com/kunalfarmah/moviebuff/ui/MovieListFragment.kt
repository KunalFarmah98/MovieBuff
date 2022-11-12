package com.kunalfarmah.moviebuff.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.*
import com.kunalfarmah.moviebuff.R
import com.kunalfarmah.moviebuff.adapter.MoviesAdapter
import com.kunalfarmah.moviebuff.databinding.FragmentMovieListBinding
import com.kunalfarmah.moviebuff.listener.MovieClickListener
import com.kunalfarmah.moviebuff.listener.MovieListListener
import com.kunalfarmah.moviebuff.room.MovieEntity
import com.kunalfarmah.moviebuff.viewmodel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MovieListFragment() : Fragment(), MovieListListener, MovieClickListener, OnClickListener {

    lateinit var binding: FragmentMovieListBinding

    companion object {
        var isGrid = true
        var TAG = "MovieListFragment"
    }

    private val viewModel: MoviesViewModel by viewModels()
    private lateinit var mAdapter: MoviesAdapter
    private var movieList = ArrayList<MovieEntity>()
    private var movieListCopy = ArrayList<MovieEntity>()
    private var genreMap = HashMap<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentMovieListBinding.inflate(layoutInflater)

        fetchData()
        populateGenres()
        setFilterView()

        viewModel.movies.observe(viewLifecycleOwner) {
            if (!movieList.isNullOrEmpty()) {
                movieList = viewModel.movies.value as ArrayList<MovieEntity>
                binding.shimmerFrameLayout.stopShimmerAnimation()
                binding.shimmerFrameLayout.visibility = View.GONE
                binding.noInternet.visibility = View.GONE

                mAdapter = MoviesAdapter(context, movieList, this)
                if (isGrid)
                    binding.movieList.layoutManager = GridLayoutManager(context, 2)
                else
                    binding.movieList.layoutManager = GridLayoutManager(context, 1)

                binding.movieList.setHasFixedSize(true)
                binding.movieList.setItemViewCacheSize(10)
                binding.movieList.adapter = mAdapter
                binding.movieList.visibility = View.VISIBLE
            } else {
                setNoInternetView()
            }
        }

        binding.retry.setOnClickListener { fetchData() }
        return binding.root
    }

    @Deprecated("Deprecated in Java")
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
            val et = it.findViewById<EditText>(R.id.search_src_text);
            et.setText("");
            searchView.setQuery("", false);
            searchView.onActionViewCollapsed()
            fetchData()
        }

        return
    }


    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.switchView -> {
                isGrid = !isGrid
                var span = 1
                if (isGrid) ++span
                binding.movieList.layoutManager = GridLayoutManager(context, span)
            }
            R.id.acton_sort_popularity -> {
                Toast.makeText(activity, "popularity", Toast.LENGTH_SHORT).show()
            }
            R.id.action_sort_date -> {
                Toast.makeText(activity, "date", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    override fun setView(list: ArrayList<MovieEntity>) {
        movieList = list
        movieListCopy = list
        binding.noInternet.visibility = View.GONE
        binding.shimmerFrameLayout.stopShimmerAnimation()
        binding.shimmerFrameLayout.visibility = View.GONE
        mAdapter = MoviesAdapter(context, list, this)
        if (isGrid)
            binding.movieList.layoutManager = GridLayoutManager(context, 2)
        else
            binding.movieList.layoutManager = GridLayoutManager(context, 1)
        binding.movieList.setHasFixedSize(true)
        binding.movieList.setItemViewCacheSize(10)
        binding.movieList.adapter = mAdapter
        binding.movieList.visibility = View.VISIBLE
    }

    override fun setNoInternetView() {
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.movieList.visibility = View.GONE
        binding.noInternet.visibility = View.VISIBLE
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

    private fun fetchData() {
        binding.shimmerFrameLayout.visibility = View.VISIBLE
        binding.shimmerFrameLayout.startShimmerAnimation()
        if (isNetworkAvailable(requireContext())) {
            viewModel.fetchAllMovies(this)
        } else {
            viewModel.getAllMovies()
        }
    }

    private fun populateGenres() {
        genreMap["Action"] = 28
        genreMap["Adventure"] = 12
        genreMap["Animation"] = 16
        genreMap["Comedy"] = 35
        genreMap["Crime"] = 80
        genreMap["Documentary"] = 99
        genreMap["Drama"] = 18
        genreMap["Family"] = 10751
        genreMap["Fantasy"] = 14
        genreMap["History"] = 36
        genreMap["Horror"] = 27
        genreMap["Music"] = 10402
        genreMap["Mystery"] = 9648
        genreMap["Romance"] = 10749
        genreMap["Science Fiction"] = 878
        genreMap["Thriller"] = 53
        genreMap["War"] = 10752
        genreMap["Western"] = 37
    }


    private fun setFilterView(){
        binding.genreFiler.all.setCardBackgroundColor(R.color.colorPrimary)
        binding.genreFiler.nameAll.setTextColor(R.color.white)

        binding.genreFiler.all.setOnClickListener(this)
        binding.genreFiler.action.setOnClickListener(this)
        binding.genreFiler.adventure.setOnClickListener(this)
        binding.genreFiler.comedy.setOnClickListener(this)
        binding.genreFiler.drama.setOnClickListener(this)
        binding.genreFiler.horror.setOnClickListener(this)
        binding.genreFiler.thriller.setOnClickListener(this)
        binding.genreFiler.mystery.setOnClickListener(this)
        binding.genreFiler.scifi.setOnClickListener(this)
    }

    private fun getGenreId(genre: String) : Int {
        return genreMap[genre] ?: 53
    }

    private fun filterMovies(genre: Int){
        movieList = movieListCopy
        if(movieList.isNullOrEmpty())
            return
        if(genre == 0) {
            mAdapter = MoviesAdapter(context, movieList, this)
            mAdapter.notifyDataSetChanged()
            return
        }
        mAdapter.movieList = movieList.filter { movie ->
            movie.genreIds.contains(genre.toString(), false)
        }
        mAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.all -> {
                resetColors()
                binding.genreFiler.all.setCardBackgroundColor(R.color.colorPrimary)
                binding.genreFiler.nameAll.setTextColor(R.color.white)
                filterMovies(0)
            }
            R.id.action -> {
                resetColors()
                binding.genreFiler.action.setCardBackgroundColor(R.color.colorPrimary)
                binding.genreFiler.nameAction.setTextColor(R.color.white)
                setSelectedGenre("action")
            }
            R.id.adventure -> {
                resetColors()
                binding.genreFiler.adventure.setCardBackgroundColor(R.color.colorPrimary)
                binding.genreFiler.nameAdventure.setTextColor(R.color.white)
                setSelectedGenre("adventure")
            }
            R.id.comedy -> {
                resetColors()
                binding.genreFiler.comedy.setCardBackgroundColor(R.color.colorPrimary)
                binding.genreFiler.nameComedy.setTextColor(R.color.white)
                setSelectedGenre("comedy")
            }
            R.id.drama -> {
                resetColors()
                binding.genreFiler.drama.setCardBackgroundColor(R.color.colorPrimary)
                binding.genreFiler.nameDrama.setTextColor(R.color.white)
                setSelectedGenre("drama")
            }
            R.id.horror -> {
                resetColors()
                binding.genreFiler.horror.setCardBackgroundColor(R.color.colorPrimary)
                binding.genreFiler.nameHorror.setTextColor(R.color.white)
                setSelectedGenre("horror")
            }
            R.id.thriller -> {
                resetColors()
                binding.genreFiler.thriller.setCardBackgroundColor(R.color.colorPrimary)
                binding.genreFiler.nameThriller.setTextColor(R.color.white)
                setSelectedGenre("thriller")
            }
            R.id.mystery -> {
                resetColors()
                binding.genreFiler.mystery.setCardBackgroundColor(R.color.colorPrimary)
                binding.genreFiler.nameMystery.setTextColor(R.color.white)
                setSelectedGenre("mystery")
            }
            R.id.scifi -> {
                resetColors()
                binding.genreFiler.scifi.setCardBackgroundColor(R.color.colorPrimary)
                binding.genreFiler.nameScifi.setTextColor(R.color.white)
                setSelectedGenre("scifi")
            }

        }
    }


    private fun resetColors(){
        binding.genreFiler.action.setCardBackgroundColor(R.color.white)
        binding.genreFiler.nameAction.setTextColor(R.color.black)

        binding.genreFiler.adventure.setCardBackgroundColor(R.color.white)
        binding.genreFiler.nameAdventure.setTextColor(R.color.black)

        binding.genreFiler.comedy.setCardBackgroundColor(R.color.white)
        binding.genreFiler.nameComedy.setTextColor(R.color.black)

        binding.genreFiler.drama.setCardBackgroundColor(R.color.white)
        binding.genreFiler.nameDrama.setTextColor(R.color.black)

        binding.genreFiler.thriller.setCardBackgroundColor(R.color.white)
        binding.genreFiler.nameThriller.setTextColor(R.color.black)

        binding.genreFiler.horror.setCardBackgroundColor(R.color.white)
        binding.genreFiler.nameHorror.setTextColor(R.color.black)

        binding.genreFiler.mystery.setCardBackgroundColor(R.color.white)
        binding.genreFiler.nameMystery.setTextColor(R.color.black)

        binding.genreFiler.scifi.setCardBackgroundColor(R.color.white)
        binding.genreFiler.nameScifi.setTextColor(R.color.black)

    }

    private fun setSelectedGenre(genre: String) {
        filterMovies(getGenreId(genre))
    }

}