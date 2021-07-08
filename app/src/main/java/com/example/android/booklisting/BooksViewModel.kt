package com.example.android.booklisting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BooksViewModel : ViewModel() {
    /** Tag for log messages */
    val LOG_TAG = BooksViewModel::class.java.simpleName

    /** List of [Book]s for [ListView]. */
    val books: MutableLiveData<MutableList<Book>> by lazy {
        MutableLiveData<MutableList<Book>>()
    }

    /**
     * Fetches books from web server async
     *
     * @param strUrl The URL to fetch the books from the web API.
     *
     * @return a list of [Book]s
     */
    fun fetchBooksAsync(strUrl: String?) {
        Thread {
            val result = QueryUtils.fetchBooks(strUrl)
            books.postValue(result)
        }.start()
    }
}




