package com.kunalfarmah.moviebuff.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kunalfarmah.moviebuff.adapter.ImageAdapter
import com.kunalfarmah.moviebuff.databinding.FragmentMovieDetailBinding
import com.kunalfarmah.moviebuff.viewmodel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MovieDetailFragment() : Fragment() {

    companion object {
        val TAG = "MoviesDetailFragment"
    }
    val viewModel: MoviesViewModel by viewModels()
    var binding: FragmentMovieDetailBinding? = null
    var movieId: String? = null
    var IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    lateinit var imageAdapter: ImageAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        movieId = viewModel.getSelectedMovie()
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
        binding = FragmentMovieDetailBinding.inflate(layoutInflater)
        fetchData()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.movieDetails.observe(viewLifecycleOwner) {
            var details = viewModel.movieDetails.value
            if (null == details) {
                setNoInternetView()
                return@observe
            }
            binding?.noInternet?.visibility = View.GONE
            binding?.mainLayout?.visibility = View.VISIBLE
            binding?.title?.text = details.title
            binding?.rating?.text = String.format("Rating: %s", details.voteAverage.toString())
            binding?.language?.text = String.format(
                "Original Language: %s",
                details.originalLanguage
            )
            binding?.releaseDate?.text = String.format("Released on: %s", details.releaseDate)
            binding?.runtime?.text = String.format("Runtime: %s Min", details.runtime)
            binding?.banner?.let { view ->
                Glide.with(requireContext()).load(IMAGE_BASE_URL + details.backdropPath)
                    .into(view)
            }
            binding?.image?.let { view ->
                Glide.with(requireContext()).load(IMAGE_BASE_URL + details.posterPath)
                    .into(view)
            }
            binding?.overview?.text = details.overview
        }

        viewModel.movieImages.observe(viewLifecycleOwner) {
            if (viewModel.movieImages.value.isNullOrEmpty()) {
                binding?.imagesLayout?.visibility = View.GONE
                return@observe
            }
            binding?.imagesRecycler?.setHasFixedSize(true)
            binding?.imagesRecycler?.setItemViewCacheSize(10)
            binding?.imagesRecycler?.layoutManager = LinearLayoutManager(
                context,
                RecyclerView.HORIZONTAL,
                false
            )
            imageAdapter = ImageAdapter(requireContext(), viewModel.movieImages.value)
            binding?.imagesRecycler?.adapter = imageAdapter
        }

        viewModel.movieReviews.observe(viewLifecycleOwner) {
            if (viewModel.movieReviews.value.isNullOrEmpty()) {
                binding?.reviewsLayout?.visibility = View.GONE
                return@observe
            }
            var review = ""
            for (reviews in viewModel.movieReviews.value!!) {
                review = review + reviews.content + "\n\n"
            }
            binding?.reviews?.text = review
        }

        binding?.back?.setOnClickListener {
            (activity as AppCompatActivity?)?.supportActionBar?.show()
            requireActivity().onBackPressed() }

        binding?.retry?.setOnClickListener { fetchData() }
    }

    fun setNoInternetView() {
        binding?.mainLayout?.visibility = View.GONE
        binding?.noInternet?.visibility = View.VISIBLE
    }

    private fun fetchData(){
        if(isNetworkAvailable(requireContext())) {
            viewModel.getMovieDetail(movieId.toString())
            viewModel.getMovieImages(movieId.toString())
            viewModel.getMovieReviews(movieId.toString())
        }
        else{
            setNoInternetView()
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}