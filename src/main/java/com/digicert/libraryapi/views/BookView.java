package com.digicert.libraryapi.views;

import com.digicert.libraryapi.entities.BookEntity;
import lombok.Data;

@Data
public class BookView {
    private String author;
    private String title;
    private int numCopies;

    public static BookView convertToView(BookEntity bookEntity) {
        BookView bookView = new BookView();

        bookView.setAuthor(bookEntity.getAuthor());
        bookView.setTitle(bookEntity.getTitle());
        bookView.setNumCopies(bookEntity.getNumCopies());
        return bookView;
    }
}
