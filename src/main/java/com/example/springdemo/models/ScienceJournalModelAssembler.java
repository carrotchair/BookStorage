package com.example.springdemo.models;

import com.example.springdemo.controllers.ScienceJournalController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ScienceJournalModelAssembler implements RepresentationModelAssembler<ScienceJournal, EntityModel<ScienceJournal>> {

    @Override
    public EntityModel<ScienceJournal> toModel(ScienceJournal entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(ScienceJournalController.class).one(entity.getBarcode())).withSelfRel(),
                linkTo(methodOn(ScienceJournalController.class).all()).withRel("sjournal"));
    }
}
