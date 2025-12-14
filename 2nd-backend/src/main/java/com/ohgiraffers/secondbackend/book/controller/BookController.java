package com.ohgiraffers.secondbackend.book.controller;

import com.ohgiraffers.secondbackend.book.dto.request.AuthorRequestDTO;
import com.ohgiraffers.secondbackend.book.dto.request.CategoryRequestDTO;
import com.ohgiraffers.secondbackend.book.dto.request.TitleRequestDTO;
import com.ohgiraffers.secondbackend.book.dto.response.BookResponseDTO;
import com.ohgiraffers.secondbackend.book.entity.Book;
import com.ohgiraffers.secondbackend.book.entity.BookCategory;
import com.ohgiraffers.secondbackend.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;


    @GetMapping("/booklist")
    public List<Book> printBookList() {
        return bookService.findAll();
    }

    @GetMapping("/booklist/{bookid}")
    public BookResponseDTO printBookById(@PathVariable Long bookid){
        return bookService.findById(bookid);
    }

    @GetMapping("/booklist/title/{booktitle}")
    public List<BookResponseDTO> printBookByTitle(@PathVariable String booktitle){
        return bookService.findByTitle(new TitleRequestDTO(booktitle));
    }

    @GetMapping("/booklist/category/{category}")
    public List<BookResponseDTO> printBookByCategory(@PathVariable String category){
        BookCategory bookcategory=BookCategory.valueOf(category);
        return bookService.findByCategory(new CategoryRequestDTO(bookcategory));
    }

    @GetMapping("/booklist/author/{author}")
    public List<BookResponseDTO>printBookByAuthor(@PathVariable String author){
        return bookService.findByAuthor(new AuthorRequestDTO(author));
    }



}
