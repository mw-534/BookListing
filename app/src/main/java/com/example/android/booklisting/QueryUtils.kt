package com.example.android.booklisting

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.*

/**
 * Helper methods for fetching data from a web API.
 */
object QueryUtils {
    /** Tag used for log messages */
    private val LOG_TAG = QueryUtils::class.java.simpleName

    /**
     * Fetch a list of [Book]s from a web API specified by the given URL string.
     *
     * @param strUrl is the URL to be queried.
     *
     * @return a list of [Book]s or null.
     */
    fun fetchBooks(strUrl: String?): MutableList<Book> {
        val list = mutableListOf<Book>()
        // if URL String is null or empty, then return early.
        if (strUrl.isNullOrEmpty()) {
            return list
        }

        // Create [URL] Object.
        val url = createUrl(strUrl)

        // Make http request to get JSON String.
        var jsonResponse = ""
        try {
            jsonResponse = makeHttpRequest(url)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Error while trying to make http request", e)
            jsonResponse = ""
        }

        // If JSON Response is null or empty, then return null.
        if (jsonResponse.isNullOrEmpty()) {
            return list
        }

        // Return list of books extracted from the JSON response.
        return extractBooks(jsonResponse)
    }

    /**
     * Convert a given String URL into an URL object.
     *
     * @param strUrl is the String of the URL to be converted.
     *
     * @return the converted [URL] object or null.
     */
    private fun createUrl(strUrl: String?): URL? {
        // If String is null or empty, then return early.
        if (strUrl.isNullOrEmpty()) {
            return null
        }

        // Convert String to URL object.
        var url: URL? = null
        try {
            url = URL(strUrl)
        } catch (e: MalformedURLException) {
            Log.e(LOG_TAG, "Error converting String to URL", e)
        }
        return url
    }

    /**
     * Make a http request to fetch data from the given URL.
     *
     * @param url is the URL to be queried.
     *
     * @return the JSON String.
     */
    @Throws(IOException::class)
    private fun makeHttpRequest(url: URL?): String {
        var jsonResponse = ""

        // If URL is null, then return early.
        if (url == null) {
            return jsonResponse
        }

        // Make http request
        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.apply {
                requestMethod = "GET"
                readTimeout = 10000 // milliseconds
                connectTimeout = 15000 // milliseconds
                connect()
            }
            // If response is ok (status code 200) then read input stream.
            if (urlConnection.responseCode == 200) {
                inputStream = urlConnection.inputStream
                jsonResponse = readInputStream(inputStream)
            } else {
                Log.e(LOG_TAG, "Error code response ${urlConnection.responseCode}")
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Error retrieving data from the URL", e)
            jsonResponse = ""
        } finally {
            urlConnection?.disconnect()
            inputStream?.close()
        }
        return jsonResponse
    }

    /**
     * Read given InputStream and return its data.
     *
     * @param inputStream is the InputStream to read from.
     *
     * @return the JSON String.
     */
    @Throws(IOException::class)
    private fun readInputStream(inputStream: InputStream?): String {
        val output = StringBuilder()
        if (inputStream != null) {
            val sr = InputStreamReader(inputStream)
            val reader = BufferedReader(sr)
            var line: String? = reader.readLine()
            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }
        return output.toString()
    }

    /**
     * Extract list of [Book]s from the given JSON response.
     *
     * @param jsonResponse is the JSON String to be extracted
     *
     * @return the list of [Book]s or an empty list.
     */
    private fun extractBooks(jsonResponse: String?): MutableList<Book> {
        val books = mutableListOf<Book>()
        // If JSON String is null or empty, then return early.
        if (jsonResponse.isNullOrEmpty()) {
            return books
        }

        try {
            // Get JSON base object.
            val jsonBase = JSONObject(jsonResponse)

            // Get items Array which contains the JSON objects representing books
            val items = jsonBase.optJSONArray("items")

            // Check if JSON result contains at least one book.
            if (items != null && items.length() > 0) {
                // Retrieve the books.
                for (i in 0 until items.length()) {
                    // Extract volume info for volume at index i.
                    // Volume is googles representation of a book.
                    val volume = items.getJSONObject(i).getJSONObject("volumeInfo")

                    // Extract title.
                    val title = volume.optString("title") ?: continue

                    // Extract authors.
                    val authorsArray = volume.optJSONArray("authors")
                    val authors = StringBuilder()
                    if (authorsArray != null) {
                        for (j in 0 until authorsArray.length()) {
                            // append a , as a delimiter if this is not the first author
                            if (j > 0) {
                                authors.append(", ")
                            }
                            // add author from index j
                            authors.append(authorsArray.getString(j))
                        }
                    }

                    // Extract published date.
                    val publishedDate = volume.optString("publishedDate", "")

                    // Extract page count.
                    val pageCount = volume.optInt("pageCount", 0)

                    // Create book object.
                    val book = Book(title, authors.toString(), publishedDate, pageCount)

                    // Add book to list.
                    books.add(book)
                }
            }
        } catch (e: JSONException) {
            Log.e(LOG_TAG, "Error while parsing JSON", e)
        }
        return books
    }
}