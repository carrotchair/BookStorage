package com.example.springdemo.exceptions;

public class IllegalIdException extends IllegalArgumentException {

    public IllegalIdException(Long id) {
        super("There already is a book with barcode: " + id);
    }
}
