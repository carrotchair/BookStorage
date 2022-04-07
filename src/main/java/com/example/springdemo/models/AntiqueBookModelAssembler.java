package com.example.springdemo.models;

import com.example.springdemo.controllers.AntiqueBookController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AntiqueBookModelAssembler implements RepresentationModelAssembler<AntiqueBook, EntityModel<AntiqueBook>> {

    @Override
    public EntityModel<AntiqueBook> toModel(AntiqueBook entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(AntiqueBookController.class).one(entity.getBarcode())).withSelfRel(),
                linkTo(methodOn(AntiqueBookController.class).all()).withRel("antbooks"));
    }
}
