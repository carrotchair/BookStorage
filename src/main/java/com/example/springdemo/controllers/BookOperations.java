package com.example.springdemo.controllers;

import com.example.springdemo.models.Book;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/default")
public interface BookOperations<T> {

    @GetMapping("")
    CollectionModel<EntityModel<T>> all();

    @PostMapping("")
    ResponseEntity<?> newBook(@RequestBody T newT);

    @GetMapping("/groupby")
    Map allGroupedByQuantity();

    @GetMapping("/{id}")
    EntityModel<T> one(@PathVariable Long id);

    @GetMapping("/price/{id}")
    double calculateTotalPriceOfSpecificItem(@PathVariable Long id);

    @PutMapping("/{id}")
    ResponseEntity<?> replaceBook(@RequestBody T newT, @PathVariable Long id);

    @DeleteMapping("/{id}")
    void deleteBook(@PathVariable Long id);
}
