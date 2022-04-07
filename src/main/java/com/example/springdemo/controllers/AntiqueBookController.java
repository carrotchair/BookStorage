package com.example.springdemo.controllers;

import com.example.springdemo.exceptions.BookNotFoundException;
import com.example.springdemo.exceptions.IllegalIdException;
import com.example.springdemo.models.AntiqueBook;
import com.example.springdemo.models.AntiqueBookModelAssembler;
import com.example.springdemo.models.Book;
import com.example.springdemo.repository.AntiqueBookRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/antbooks")
public class AntiqueBookController implements BookOperations<AntiqueBook> {

    private static final Logger LOGGER = LogManager.getLogger(Book.class);

    private final AntiqueBookRepository repository;
    private final AntiqueBookModelAssembler assembler;

    AntiqueBookController(AntiqueBookRepository repository, AntiqueBookModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @Override
    public CollectionModel<EntityModel<AntiqueBook>> all() {
        List<EntityModel<AntiqueBook>> books = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(books,
                linkTo(methodOn(BookController.class).all()).withSelfRel());
    }

    @Override
    public ResponseEntity<?> newBook(@RequestBody AntiqueBook newBook) {
        Long barcode = newBook.getBarcode();

        if (repository.findById(barcode).isPresent()) {
            LOGGER.error("User tried to add a book with an existing barcode ");
            throw new IllegalIdException(barcode);
        } else {
            EntityModel<AntiqueBook> entityModel = assembler.toModel(repository.save(newBook));

            return ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel);
        }
    }

    @Override
    public Map allGroupedByQuantity() {
        List<AntiqueBook> books = repository.findAll().stream()
                .collect(Collectors.toList());

        Map<Integer, List<AntiqueBook>> groupedByQuantitySortedByTotalPrice =
                books.stream()
                        .sorted(Comparator.comparing(AntiqueBook::totalPrice))
                        .collect(Collectors.groupingBy(AntiqueBook::getQuantity, Collectors.toList()));

        Map<Integer, List<Long>> printsOnlyBarcodes = groupedByQuantitySortedByTotalPrice.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, value -> value.getValue().stream().map(AntiqueBook::getBarcode).collect(Collectors.toList())));

        return printsOnlyBarcodes;
    }

    @Override
    public EntityModel<AntiqueBook> one(@PathVariable Long id) {
        AntiqueBook book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        return assembler.toModel(book);
    }

    @Override
    public double calculateTotalPriceOfSpecificItem(Long id) {
        AntiqueBook book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        return book.totalPrice();
    }

    @Override
    public ResponseEntity<?> replaceBook(@RequestBody AntiqueBook newBook, @PathVariable Long id) {
        Long barcode = newBook.getBarcode();

        if (!repository.findById(barcode).isPresent()) {
            LOGGER.error("User tried to update an unexisting book ");
            throw new BookNotFoundException(barcode);
        } else {
            AntiqueBook updatedBook = repository.findById(id)
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

            EntityModel<AntiqueBook> entityModel = assembler.toModel(updatedBook);

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
