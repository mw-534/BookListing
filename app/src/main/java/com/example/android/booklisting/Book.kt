package com.example.android.booklisting

/**
 * Represents information about a book.
 *
 * @constructor Creates a new [Book] object.
 *
 * @property title is the title of the book.
 * @property authors are the authors of the book.
 * @property publishedDate when the book was published.
 * @property pageCount is the number of pages of the book.
 */
class Book(val title: String, val authors: String, val publishedDate: String, val pageCount: Int)