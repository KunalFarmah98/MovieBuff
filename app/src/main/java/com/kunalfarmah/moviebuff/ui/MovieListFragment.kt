package com.kunalfarmah.moviebuff.ui

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.*
import com.kunalfarmah.moviebuff.R
import com.kunalfarmah.moviebuff.adapter.FilterAdapter
import com.kunalfarmah.moviebuff.adapter.MoviesAdapter
import com.kunalfarmah.moviebuff.databinding.FragmentMovieListBinding
import com.kunalfarmah.moviebuff.listener.FilterClickListener
import com.kunalfarmah.moviebuff.listener.MovieClickListener
import com.kunalfarmah.moviebuff.listener.MovieListListener
import com.kunalfarmah.moviebuff.model.FilterItem
import com.kunalfarmah.moviebuff.preferences.PreferenceManager
import com.kunalfarmah.moviebuff.model.Movie
import com.kunalfarmah.moviebuff.util.Constants
import com.kunalfarmah.moviebuff.viewmodel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MovieListFragment() : Fragment(), MovieClickListener, FilterClickListener {

    lateinit var binding: FragmentMovieListBinding

    companion object {
        var TAG = "MovieListFragment"
    }

    private val viewModel: MoviesViewModel by viewModels()
    private lateinit var mAdapter: MoviesAdapter
    private lateinit var filterAdapter: FilterAdapter
    private var movieList = ArrayList<Movie>()
    private var movieListCopy = ArrayList<Movie>()
    private var genreList = ArrayList<FilterItem>()
    private var genreMap = HashMap<String, Int>()
    private var selectedGenre = 0
    private var selectedOrder = ""
    private var display = Constants.Display.GRID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Default).launch {
            PreferenceManager.getValue(Constants.SELECTED_FILTER, 0)?.collect{
                Timber.d("Datastore: Collected ${Constants.SELECTED_FILTER} -> ${it?:""}")
                selectedGenre = it as Int
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            PreferenceManager.getValue(Constants.SORT_ORDER, "")?.collect {
                Timber.d("Datastore: Collected ${Constants.SORT_ORDER} -> ${it?:""}")
                selectedOrder = it as String
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            PreferenceManager.getValue(Constants.DISPLAY, Constants.Display.GRID)?.collect {
                Timber.d("Datastore: Collected ${Constants.DISPLAY} -> ${it?:""}")
                display = it as String
            }
        }
    }
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
            if (!it.isNullOrEmpty()) {
                setViews(it as ArrayList<Movie>)
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
                viewModel.searchAllMovies(query)
                return true
            }


        })

        searchView.setOnCloseListener(SearchView.OnCloseListener {
            fetchData()
            return@OnCloseListener false
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
                if(display == Constants.Display.GRID){
                    Toast.makeText(activity, "Switching to cards", Toast.LENGTH_SHORT).show()
                    PreferenceManager.putValue(Constants.DISPLAY, Constants.Display.CARDS)
                    setLayout(Constants.Display.CARDS)
                }
                else{
                    Toast.makeText(activity, "Switching to grid", Toast.LENGTH_SHORT).show()
                    PreferenceManager.putValue(Constants.DISPLAY, Constants.Display.GRID)
                    setLayout(Constants.Display.GRID)
                }
            }
            R.id.acton_sort_popularity -> {
                PreferenceManager.putValue(Constants.SORT_ORDER, Constants.SortOrder.POPULAIRTY)
                Toast.makeText(activity, "Sorting by popularity", Toast.LENGTH_SHORT).show()
                sortMovies(Constants.SortOrder.POPULAIRTY)
            }
            R.id.action_sort_date -> {
                PreferenceManager.putValue(Constants.SORT_ORDER, Constants.SortOrder.RELEASE_DATE)
                Toast.makeText(activity, "Sorting by date", Toast.LENGTH_SHORT).show()
                sortMovies(Constants.SortOrder.RELEASE_DATE)
            }
            R.id.acton_sort_rating -> {
                PreferenceManager.putValue(Constants.SORT_ORDER, Constants.SortOrder.RATING)
                Toast.makeText(activity, "Sorting by rating", Toast.LENGTH_SHORT).show()
                sortMovies(Constants.SortOrder.RATING)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setLayout(layout: String){
        if (layout == Constants.Display.GRID)
            binding.movieList.layoutManager = GridLayoutManager(context, 2)
        else
            binding.movieList.layoutManager = LinearLayoutManager(context)
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun setViews(list: ArrayList<Movie>){
        if(list.isEmpty()){
            setNoInternetView()
            return
        }
        movieList = list
        movieListCopy = list
        binding.noInternet.visibility = View.GONE
        binding.shimmerFrameLayout.stopShimmerAnimation()
        binding.shimmerFrameLayout.visibility = View.GONE
        mAdapter = MoviesAdapter(context, list, this)
        if (display == Constants.Display.GRID)
            binding.movieList.layoutManager = GridLayoutManager(context, 2)
        else
            binding.movieList.layoutManager = LinearLayoutManager(context)
        binding.movieList.setHasFixedSize(true)
        binding.movieList.setItemViewCacheSize(10)
        binding.movieList.adapter = mAdapter
        binding.movieList.visibility = View.VISIBLE

        // applying filtering based on user's preference
        setGenre(selectedGenre)
        filterMovies(getGenreId(genreList[selectedGenre].genre))

        //sorting based on user preferences
        sortMovies(selectedOrder)
    }
    private fun setNoInternetView() {
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.movieList.visibility = View.GONE
        binding.noInternet.visibility = View.VISIBLE
    }

    override fun onMovieClick(id: Int, image: ImageView) {

        var movieDetailFragment = MovieDetailFragment()
        var args = Bundle()
        args.putString(Constants.MOVIE_ID, id.toString())
        movieDetailFragment.arguments = args

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
            viewModel.fetchAllMovies()
        } else {
            viewModel.getAllMovies()
        }
    }

    private fun populateGenres() {
        genreMap["All"] = 0
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

        var genres = genreMap.keys.toList()
        genres.map {
            genreList.add(FilterItem(it, false))
        }
    }


    private fun setFilterView(){
        binding.genreFilter.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.genreFilter.itemAnimator = DefaultItemAnimator()
        filterAdapter = FilterAdapter(context, genreList, this)
        binding.genreFilter.adapter = filterAdapter
    }

    private fun getGenreId(genre: String) : Int {
        return genreMap[genre] ?: 0
    }

    private fun filterMovies(genre: Int){
        movieList = movieListCopy
        if(movieList.isNullOrEmpty())
            return
        if(genre == 0) {
            mAdapter.movieList = movieListCopy
        }
        else {
            mAdapter.movieList = movieList.filter { movie ->
                movie.genreIds.contains(genre.toString(), false)
            }
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun setGenre(pos: Int){
        filterAdapter.list[selectedGenre].selected = false
        filterAdapter.list[pos].selected = true
        filterAdapter.notifyItemChanged(selectedGenre)
        filterAdapter.notifyItemChanged(pos)
        PreferenceManager.putValue(Constants.SELECTED_FILTER, pos)
    }

    private fun sortMovies(order: String){
        if(order == Constants.SortOrder.POPULAIRTY){
            mAdapter.movieList = mAdapter.movieList.sortedWith(Comparator { o1, o2 ->
                if (o1.popularity > o2.popularity)
                    return@Comparator -1
                else if(o1.popularity == o2.popularity)
                    return@Comparator 0
                else
                    return@Comparator 1
            })
        }
        else if(order == Constants.SortOrder.RELEASE_DATE){
            mAdapter.movieList = mAdapter.movieList.sortedWith(Comparator { o1, o2 ->
                if (o1.releaseDate > o2.releaseDate)
                    return@Comparator -1
                else if(o1.releaseDate == o2.releaseDate)
                    return@Comparator 0
                else
                    return@Comparator 1
            })
        }
        else if(order == Constants.SortOrder.RATING){
            mAdapter.movieList = mAdapter.movieList.sortedWith(Comparator { o1, o2 ->
                if (o1.voteAverage > o2.voteAverage)
                    return@Comparator -1
                else if(o1.voteAverage == o2.voteAverage)
                    return@Comparator 0
                else
                    return@Comparator 1
            })
        }
        mAdapter.notifyDataSetChanged()
    }
    override fun onFilterClick(genre: FilterItem, pos: Int) {
        setGenre(pos)
        filterMovies(getGenreId(genre.genre))
        sortMovies(selectedOrder)
    }

}