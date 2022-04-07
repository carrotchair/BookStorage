package com.example.springdemo.databaseMethods;

import com.example.springdemo.models.AntiqueBook;
import com.example.springdemo.models.Book;
import com.example.springdemo.models.ScienceJournal;
import com.example.springdemo.repository.AntiqueBookRepository;
import com.example.springdemo.repository.BookRepository;
import com.example.springdemo.repository.ScienceJournalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Year;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    CommandLineRunner initDatabase(BookRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Book("The Hobbit",
                    "J. R. R. Tolkien",
                    11111111, 11, 1.2)));
            log.info("Preloading " + repository.save(new Book("The Little Prince",
                    "Antoine de Saint-Exupéry",
                    11112222, 10, 2.3)));
            log.info("Preloading " + repository.save(new Book("Aaa",
                    "T",
                    11113333, 10, 23.99)));
            log.info("Preloading " + repository.save(new Book("Bbb",
                    "T",
                    11114444, 4, 23.99)));
            log.info("Preloading " + repository.save(new Book("Ccc",
                    "T",
                    11115555, 4, 1.2)));
        };
    }

    CommandLineRunner initDatabase2(AntiqueBookRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new AntiqueBook("Ddd",
                    "J. R. R. Tolkien",
                    22221111, 4, 12, Year.of(1800))));
            log.info("Preloading " + repository.save(new AntiqueBook("Eee",
                    "Antoine de Saint-Exupéry",
                    22222222, 11, 4.5, Year.of(1700))));
        };
    }

    CommandLineRunner initDatabase3(ScienceJournalRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new ScienceJournal("Fff",
                    "H",
                    33331111, 4, 2.3, 4)));
            log.info("Preloading " + repository.save(new ScienceJournal("Ggg",
                    "H",
                    33332222, 11, 44, 5)));
        };
    }
}
