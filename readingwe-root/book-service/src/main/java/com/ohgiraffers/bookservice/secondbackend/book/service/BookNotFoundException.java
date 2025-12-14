package com.ohgiraffers.bookservice.secondbackend.book.service;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long bookId) {
        super("Book not found: " + bookId);
    }
}

