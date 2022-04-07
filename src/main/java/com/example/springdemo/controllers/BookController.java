package com.example.springdemo.controllers;

import java.util.*;
import java.util.stream.Collectors;

import com.example.springdemo.exceptions.BookNotFoundException;
import com.example.springdemo.exceptions.IllegalIdException;
import com.example.springdemo.models.Book;
import com.example.springdemo.models.BookModelAssembler;
import com.example.springdemo.repository.BookRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/books")
public class BookController implements BookOperations<Book> {

    private static final Logger LOGGER = LogManager.getLogger(Book.class);

    private final BookRepository repository;
    private final BookModelAssembler assembler;

    BookController(BookRepository repository, BookModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @Override
    public CollectionModel<EntityModel<Book>> all() {
        List<EntityModel<Book>> books = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(books,
                linkTo(methodOn(BookController.class).all()).withSelfRel());
    }

    @Override
    public ResponseEntity<?> newBook(@RequestBody Book newBook) {
        Long barcode = newBook.getBarcode();

        if (repository.findById(barcode).isPresent()) {
            LOGGER.error("User tried to add a book with an existing barcode ");
            throw new IllegalIdException(barcode);
        } else {
            EntityModel<Book> entityModel = assembler.toModel(repository
                    .save(newBook)
            );

            return ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel);
        }
    }

    @Override
    public Map allGroupedByQuantity() {
        List<Book> books = repository.findAll().stream()
                .collect(Collectors.toList());

        Map<Integer, List<Book>> groupedByQuantitySortedByTotalPrice =
                books.stream()
                        .sorted(Comparator.comparing(Book::totalPrice))
                        .collect(Collectors.groupingBy(Book::getQuantity, Collectors.toList()));

        Map<Integer, List<Long>> printsOnlyBarcodes = groupedByQuantitySortedByTotalPrice.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, value -> value.getValue().stream().map(Book::getBarcode).collect(Collectors.toList())));

        return printsOnlyBarcodes;
    }

    @Override
    public EntityModel<Book> one(@PathVariable Long id) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        return assembler.toModel(book);
    }

    @Override
    public double calculateTotalPriceOfSpecificItem(@PathVariable Long id) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        return book.totalPrice();
    }

    @Override
    public ResponseEntity<?> replaceBook(@RequestBody Book newBook, @PathVariable Long id) {
        Long barcode = newBook.getBarcode();

        if (!repository.findById(barcode).isPresent()) {
            LOGGER.error("User tried to update an unexisting book ");
            throw new BookNotFoundException(barcode);
        } else {
            Book updatedBook = repository.findById(id)
                    .map(book -> {
                        book.setName(newBook.getName());
                        book.setAuthor(newBook.getAuthor());
                        book.setBarcode(newBook.getBarcode());
                        book.setQuantity(newBook.getQuantity());
                        book.setPricePerUnit(newBook.getPricePerUnit());
                        return repository.save(book);
                    })
                    .orElseGet(() -> {
                        newBook.setBarcode(id);
                        return repository.save(newBook);
                    });

            EntityModel<Book> entityModel = assembler.toModel(updatedBook);

            return ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel);
        }
    }

    @Override
    public void deleteBook(@PathVariable Long id) {
        repository.deleteById(id);
    }
}