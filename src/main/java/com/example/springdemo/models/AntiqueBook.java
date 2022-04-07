package com.example.springdemo.models;

import com.sun.istack.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Entity;
import java.time.Year;

@Entity
public class AntiqueBook extends Book {

    private static final Logger LOGGER = LogManager.getLogger(AntiqueBook.class);

    private final Year lastYear = Year.of(1900);
    Year releaseYear;

    AntiqueBook() {
    }

    public AntiqueBook(String name, String author, long barcode, int quantity, double pricePerUnit, Year releaseYear) {
        super(name, author, barcode, quantity, pricePerUnit);
        releaseYearRange(releaseYear);
    }

    private void releaseYearRange(Year releaseYear) {
        if (releaseYear.isBefore(lastYear)) {
            this.releaseYear = releaseYear;
        } else {
            this.releaseYear = Year.of(1899);
            LOGGER.error("Antique book can be released no more recent than 1900 ");
        }
    }

    public Year getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(@NotNull Year releaseYear) {
        releaseYearRange(releaseYear);
    }

    @Override
    public double totalPrice() {
        if (this.releaseYear != null) {
            return super.totalPrice() * (Year.now().getValue() - this.releaseYear.getValue()) / 10;
        }
        return super.totalPrice();
        // if year is other than expected, we use counting for simple book
    }

    @Override
    public String toString() {
        String str1 = super.toString();
        String str2 = ", release year=" + getReleaseYear();
        return str1 + str2;
    }
}
