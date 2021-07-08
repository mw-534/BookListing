package com.example.android.booklisting

import android.app.Service
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    /** Adapter to fill ListView with Books. */
    private var mAdapter: BookAdapter? = null

    /** View whichi will be shown when the [ListView] is empty. */
    private var mEmptyStateView: TextView? = null

    /** [ViewModel] of the activity. */
    private val model by viewModels<BooksViewModel>()

    /** Indicator which should be shown while data is fetched from the web API. */
    private var mLoadingIndicator: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find ListView for books.
        val booksListView = findViewById<ListView>(R.id.books_list)

        // Create Adapter with an empty list
        mAdapter = BookAdapter(this, mutableListOf())

        // Set Adapter
        booksListView.adapter = mAdapter

        // Find empty state TextView
        mEmptyStateView = findViewById(R.id.empty_view)

        // Set empty state view.
        booksListView.emptyView = mEmptyStateView

        // Find loading indicator.
        mLoadingIndicator = findViewById(R.id.loading_indicator)

        // Set observer to update ListView if data of list changes in the ViewModel.
        model.books.observe(this, Observer {
            // Set text for empty state view.
            mEmptyStateView?.text = getString(R.string.no_books_found)

            // Hide loading indicator.
            mLoadingIndicator?.visibility = View.GONE

            // Clear old data from the adapter.
            mAdapter?.clear()

            // Add new books.
            mAdapter?.addAll(it)
        })

        // Find search button.
        val btnSearch = findViewById<Button>(R.id.btn_search)

        // Set listener to listen for click events.
        btnSearch.setOnClickListener {
            // Find edit box.
            val editView = findViewById<EditText>(R.id.search_box)

            // Get title searched for.
            val title = editView.text.toString()

            // If no title is entered, then return early.
            if (title.isNullOrEmpty()) {
                return@setOnClickListener
            }

            // Query URL to request data from google books web API.
            val url = "https://www.googleapis.com/books/v1/volumes?q=$title&maxResults=10"

            if (hasConnection()) {
                // Show loading indicator
                mLoadingIndicator?.visibility = View.VISIBLE

                // Update list of books asynchronously.
                model.fetchBooksAsync(url)
            } else {
                // Delete old entries in list.
                mAdapter?.clear()

                // Set empty view to no network connection.
                mEmptyStateView?.text = getString(R.string.no_network_connection)
            }

        }
    }

    /**
     * Check if device is connected to the internet.
     */
    private fun hasConnection(): Boolean {
        var isConnected = false
        val cm = getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        isConnected = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val nwCap = cm.getNetworkCapabilities(network) ?: return false
            when {
                nwCap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                nwCap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                nwCap.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            cm.activeNetworkInfo?.isConnectedOrConnecting ?: false
        }
        return isConnected
    }
}