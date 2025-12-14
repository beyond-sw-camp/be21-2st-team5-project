package com.ohgiraffers.bookservice.secondbackend.book.controller;

import com.ohgiraffers.bookservice.secondbackend.book.dto.request.AuthorRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.book.dto.request.CategoryRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.book.dto.request.TitleRequestDTO;
import com.ohgiraffers.bookservice.secondbackend.book.dto.response.BookResponseDTO;
import com.ohgiraffers.bookservice.secondbackend.book.entity.Book;
import com.ohgiraffers.bookservice.secondbackend.book.entity.BookCategory;
import com.ohgiraffers.bookservice.secondbackend.book.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "Book API", description = "책 등록, 조회, 카테고리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;


    @GetMapping("/booklist")
    public ResponseEntity<Page<Book>> printBookList(
            @PageableDefault(page=0, size=10) Pageable pageable
    ) {
        return ResponseEntity.ok(bookService.findAll(pageable));
    }

    @GetMapping("/booklist/{bookid}")
    public BookResponseDTO printBookById(@PathVariable Long bookid){

        return bookService.findById(bookid);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveBook(
            HttpServletRequest req,
            @RequestBody Book book) {
        System.out.println("===== BOOK SERVICE HEADER CHECK =====");
        System.out.println("X-User-Name = " + req.getHeader("X-User-Name"));
        System.out.println("X-User-Role = " + req.getHeader("X-User-Role"));
        System.out.println("X-User-Id   = " + req.getHeader("X-User-Id"));
        System.out.println("======================================");


        String role = req.getHeader("X-User-Role");

        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }


        if (!role.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 접근할 수 있습니다.");
        }


        bookService.createBook(book);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{bookid}")
    public ResponseEntity<?> deleteBook(
            HttpServletRequest req,
            @PathVariable Long bookid) {


        String role = req.getHeader("X-User-Role");

        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }


        if (!role.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 접근할 수 있습니다.");
        }


        bookService.deleteBook(bookid);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/booklist/title/{booktitle}")
    public Page<BookResponseDTO> printBookByTitle(
            @PathVariable String booktitle,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return bookService.findByTitle(booktitle, pageable);
    }


    @GetMapping("/booklist/category/{category}")
    public Page<BookResponseDTO> printBookByCategory(
            @PathVariable String category,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        BookCategory bookcategory = BookCategory.valueOf(category);
        return bookService.findByCategory(bookcategory, pageable);
    }


    @GetMapping("/booklist/author/{author}")
    public Page<BookResponseDTO> printBookByAuthor(
            @PathVariable String author,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return bookService.findByAuthor(author, pageable);
    }



    //Feign을 위한 api
    @GetMapping("/categories")
    public List<String>getAllCategories(){
        return Arrays.stream(BookCategory.values())
                .map(Enum::name)
                .toList();
    }
}
