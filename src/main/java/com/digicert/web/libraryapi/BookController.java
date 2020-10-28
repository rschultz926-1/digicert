package com.digicert.web.libraryapi;

import com.digicert.libraryapi.exceptions.BookAlreadyExistsException;
import com.digicert.libraryapi.exceptions.BookNotFoundException;
import com.digicert.libraryapi.services.BookService;
import com.digicert.libraryapi.views.BookView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/library/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * Returns a list of all books in the library.
     *
     * @return A list of BookView objects containing information about every book in the library.
     */
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BookView> getAllBooks() {
        return bookService.getAllBooks();
    }

    /**
     * Returns the requested book.
     *
     * @param author   The author of the requested book.
     * @param title    The title of the requested book.
     *
     * @return The BookView object containing information about the requested book.
     */
    @GetMapping(path = "/{author}/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BookView getBook(@PathVariable("author") String author, @PathVariable("title") String title) {
        Optional<BookView> book = bookService.getBook(author, title);
        if (!book.isPresent()) {
            throw new BookNotFoundException("No book found with author: '" + author + "' and title: '" + title + "'.");
        }
        return book.get();
    }

    /**
     * Adds a new book to the library.
     *
     * @param author    The author of the new book.
     * @param title     The title of the new book.
     * @param numCopies The number of copies of the new book to add.
     */
    @PostMapping(path = "/{author}/{title}")
    public void createBook(
            @PathVariable("author") String author,
            @PathVariable("title") String title,
            @RequestParam(value = "numCopies", required = false, defaultValue = "0") int numCopies
    ) {
        if (bookService.getBook(author, title).isPresent()) {
            throw new BookAlreadyExistsException("The book with author: '" + author + "' and title: '" + title + "' already exists.");
        }
        bookService.createBook(author, title, numCopies);
    }

    /**
     * Updates the information of a selected book.
     *
     * @param author    The author of the book to update.
     * @param title     The title of the book to update.
     * @param numCopies The new number of copies of the book to update.
     */
    @PutMapping(path = "/{author}/{title}/{numCopies}")
    public void updateBook(
            @PathVariable("author") String author,
            @PathVariable("title") String title,
            @PathVariable("numCopies") int numCopies
    ) {
        if (!bookService.updateBook(author, title, numCopies)) {
            throw new BookNotFoundException("No book found with author: '" + author + "' and title: '" + title + "'.");
        }
    }

    /**
     * Removes a selected book from the library.
     *
     * @param author    The author of the book to remove.
     * @param title     The title of the book to remove.
     */
    @Transactional
    @DeleteMapping(path = "{author}/{title}")
    public void deleteBook(@PathVariable("author") String author, @PathVariable("title") String title) {
        if (!bookService.deleteBook(author, title)) {
            throw new BookNotFoundException("No book found with author: '" + author + "' and title: '" + title + "'.");
        }
    }
}