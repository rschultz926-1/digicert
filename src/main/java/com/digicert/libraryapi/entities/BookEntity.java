package com.digicert.libraryapi.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Column;

@Entity
@Data
@Table(name = "library_books", uniqueConstraints = @UniqueConstraint(columnNames = {"author", "title"}))
@IdClass(BookId.class)
public class BookEntity {
    @Id
    @Column(name = "author", nullable = false)
    private String author;

    @Id
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "num_copies", nullable = false)
    private int numCopies;
}
