package com.ohgiraffers.secondbackend.book.service;

import com.ohgiraffers.secondbackend.book.dto.request.AuthorRequestDTO;
import com.ohgiraffers.secondbackend.book.dto.request.CategoryRequestDTO;
import com.ohgiraffers.secondbackend.book.dto.request.TitleRequestDTO;
import com.ohgiraffers.secondbackend.book.dto.response.BookResponseDTO;
import com.ohgiraffers.secondbackend.book.entity.Book;
import com.ohgiraffers.secondbackend.book.entity.BookCategory;
import com.ohgiraffers.secondbackend.book.repository.BookRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService( BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    private BookResponseDTO convert(Book book) {
        return BookResponseDTO.builder()
                .bookid(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .publishedDate(book.getPublishedDate())
                .category(book.getCategory())
                .build();
    }


    public List<Book> findAll(){
        return bookRepository.findAll();
    }

    public BookResponseDTO findById(Long bookId){
        return convert(bookRepository.findById(bookId).orElseThrow());
    }

    public List<BookResponseDTO> findByTitle(TitleRequestDTO req){
        String title=req.getTitle();
        List<Book>books=bookRepository.findByTitle(title);

        return books.stream().map(this::convert).toList();
    }

    public List<BookResponseDTO>findByCategory(CategoryRequestDTO req){
        BookCategory category=req.getCategory();
        List<Book>books=bookRepository.findByCategory(category);

        return books.stream().map(this::convert).toList();
    }

    public List<BookResponseDTO>findByAuthor(AuthorRequestDTO req){
        String author=req.getAuthor();
        List<Book>books=bookRepository.findByAuthor(author);
        return books.stream().map(this::convert).toList();
    }



    public List<BookResponseDTO> getSortedBooks(String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return bookRepository.findAll(sort).stream()
                .map(this::convert)
                .toList();
    }



}
