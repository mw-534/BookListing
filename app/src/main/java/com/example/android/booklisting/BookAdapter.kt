package com.example.android.booklisting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.ParseException
import java.text.SimpleDateFormat


/**
 * Adapter to fill a list items with values from a [Book] object.
 *
 * @constructor Create a new BookAdapter.
 *
 * @param context The current Context
 * @param list The list of [Book]s from which the list items should be filled.
 */
class BookAdapter(context: Context, list: MutableList<Book>)
    : ArrayAdapter<Book>(context, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // If a view is available recycle it, otherwise create new one.
        val bookListItem = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.book_list_item, parent, false)

        // Get current book.
        val book = getItem(position) as Book

        // Find title view and set title
        bookListItem.findViewById<TextView>(R.id.title).text = book.title

        // Find authors view and set authors.
        bookListItem.findViewById<TextView>(R.id.authors).text = book.authors

        // Find published date view and set published date.
        bookListItem.findViewById<TextView>(R.id.published_date).text =
                formatDate(book.publishedDate)

        // Find page count view and set page count.
        bookListItem.findViewById<TextView>(R.id.page_count).text =
            context.getString(R.string.page_count, book.pageCount.toString())

        return bookListItem
    }

    /**
     * Format the date of type YYYY-MM-DD into local date format.
     *
     * @param date The date string as fetched from web API.
     *
     * @return the local date as a string.
     */
    private fun formatDate(date: String): String {
        try {
            // Create input formatter with format pattern from web API.
            val inFormatter = SimpleDateFormat("yyyy-MM-DD")
            // Parse the date input string to a Date object.
            val outDate = inFormatter.parse(date)
            // Create output formatter based on the locale.
            val outFormatter = SimpleDateFormat.getDateInstance()
            // If no date was created from the inFormatter then return the original date string.
            if (outDate == null) {
                return date
            }
            // Convert the Date object into a local date string.
            return outFormatter.format(outDate)
        } catch (e: ParseException) {
            // If an error occurs while parsing the date, then return original date string
            return date
        }
    }
}