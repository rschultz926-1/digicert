package com.digicert.libraryapi.repositories;

import com.digicert.libraryapi.entities.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<BookEntity, String> {
    /**
     * Find BookEntity by author and title.
     *
     * @param author author of the book to search for.
     * @param title  title of the book to search for.
     * @return Optional of the BookEntity that was found.
     */
    Optional<BookEntity> findOneByAuthorAndTitle(String author, String title);

    /**
     * Delete BookEntity by author and title.
     *
     * @param author author of the book to delete.
     * @param title  title of the book to delete.
     */
    void deleteByAuthorAndTitle(String author, String title);
}
