package com.ohgiraffers.bookservice.secondbackend.book.service;

public class BookQueryException extends RuntimeException {
    public BookQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}