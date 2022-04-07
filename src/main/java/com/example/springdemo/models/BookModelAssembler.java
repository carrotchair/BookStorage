package com.example.springdemo.models;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.springdemo.controllers.BookController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class BookModelAssembler implements RepresentationModelAssembler<Book, EntityModel<Book>> {

    @Override
    public EntityModel<Book> toModel(Book entity) {

        return EntityModel.of(entity, //
                linkTo(methodOn(BookController.class).one(entity.getBarcode())).withSelfRel(),
                linkTo(methodOn(BookController.class).all()).withRel("books"));
    }
}
