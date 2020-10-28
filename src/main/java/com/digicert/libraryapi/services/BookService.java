package com.digicert.libraryapi.services;

import com.digicert.libraryapi.entities.BookEntity;
import com.digicert.libraryapi.repositories.BookRepository;
import com.digicert.libraryapi.views.BookView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookView> getAllBooks() {
        return bookRepository.findAll().stream()
            .map(book-> BookView.convertToView(book))
            .collect(Collectors.toList());
    }

    public Optional<BookView> getBook(String author, String title) {
        Optional<BookEntity> bookEntity = bookRepository.findOneByAuthorAndTitle(author, title);
        if (bookEntity.isPresent()) {
            return Optional.of(BookView.convertToView(bookEntity.get()));
        }
        return Optional.empty();
    }

    public void createBook(String author, String title, int numCopies) {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setAuthor(author);
        bookEntity.setTitle(title);
        bookEntity.setNumCopies(numCopies);
        bookRepository.save(bookEntity);
    }

    public boolean updateBook(String author, String title, int numCopies) {
        boolean bookExists = false;
        Optional<BookEntity> book = bookRepository.findOneByAuthorAndTitle(author, title);
        if (book.isPresent()) {
            bookExists = true;
            BookEntity bookEntity = book.get();
            bookEntity.setNumCopies(numCopies);
            bookRepository.save(bookEntity);
        }
        return bookExists;
    }

    public boolean deleteBook(String author, String title) {
        boolean bookExists = false;
        Optional<BookEntity> book = bookRepository.findOneByAuthorAndTitle(author, title);
        if (book.isPresent()) {
            bookExists = true;
            bookRepository.deleteByAuthorAndTitle(author, title);
        }
        return bookExists;
    }
}
