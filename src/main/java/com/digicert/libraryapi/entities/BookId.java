package com.digicert.libraryapi.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class BookId implements Serializable {
    private String author;
    private String title;
}
