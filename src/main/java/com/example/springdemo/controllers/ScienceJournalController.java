package com.example.springdemo.controllers;

import com.example.springdemo.exceptions.BookNotFoundException;
import com.example.springdemo.exceptions.IllegalIdException;
import com.example.springdemo.models.AntiqueBook;
import com.example.springdemo.models.Book;
import com.example.springdemo.models.ScienceJournal;
import com.example.springdemo.models.ScienceJournalModelAssembler;
import com.example.springdemo.repository.ScienceJournalRepository;
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
@RequestMapping("/sjournal")
public class ScienceJournalController implements BookOperations<ScienceJournal> {

    private static final Logger LOGGER = LogManager.getLogger(Book.class);

    private final ScienceJournalRepository repository;
    private final ScienceJournalModelAssembler assembler;

    ScienceJournalController(ScienceJournalRepository repository, ScienceJournalModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @Override
    public CollectionModel<EntityModel<ScienceJournal>> all() {
        List<EntityModel<ScienceJournal>> books = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(books,
                linkTo(methodOn(BookController.class).all()).withSelfRel());
    }

    @Override
    public ResponseEntity<?> newBook(@RequestBody ScienceJournal newBook) {
        Long barcode = newBook.getBarcode();

        if (repository.findById(barcode).isPresent()) {
            LOGGER.error("User tried to add a book with an existing barcode ");
            throw new IllegalIdException(barcode);
        } else {
            EntityModel<ScienceJournal> entityModel = assembler.toModel(repository.save(newBook));

            return ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel);
        }
    }

    @Override
    public Map allGroupedByQuantity() {
        List<ScienceJournal> books = repository.findAll().stream()
                .collect(Collectors.toList());

        Map<Integer, List<ScienceJournal>> groupedByQuantitySortedByTotalPrice =
                books.stream()
                        .sorted(Comparator.comparing(ScienceJournal::totalPrice))
                        .collect(Collectors.groupingBy(ScienceJournal::getQuantity, Collectors.toList()));

        Map<Integer, List<Long>> printsOnlyBarcodes = groupedByQuantitySortedByTotalPrice.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, value -> value.getValue().stream().map(ScienceJournal::getBarcode).collect(Collectors.toList())));

        return printsOnlyBarcodes;
    }

    @Override
    public EntityModel<ScienceJournal> one(@PathVariable Long id) {
        ScienceJournal book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        return assembler.toModel(book);
    }

    @Override
    public double calculateTotalPriceOfSpecificItem(Long id) {
        ScienceJournal book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        return book.totalPrice();
    }

    @Override
    public ResponseEntity<?> replaceBook(@RequestBody ScienceJournal newBook, @PathVariable Long id) {
        Long barcode = newBook.getBarcode();

        if (!repository.findById(barcode).isPresent()) {
            LOGGER.error("User tried to update an unexisting book ");
            throw new BookNotFoundException(barcode);
        } else {
            ScienceJournal updatedBook = repository.findById(id)
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

            EntityModel<ScienceJournal> entityModel = assembler.toModel(updatedBook);

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
