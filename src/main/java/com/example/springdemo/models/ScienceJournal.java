package com.example.springdemo.models;

import com.sun.istack.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Entity;

@Entity
public class ScienceJournal extends Book {

    private static final Logger LOGGER = LogManager.getLogger(ScienceJournal.class);

    private final int min = 1;
    private final int max = 10;
    int scienceIndex;

    ScienceJournal() {
    }

    public ScienceJournal(String name, String author, long barcode, int quantity, double pricePerUnit, int scienceIndex) {
        super(name, author, barcode, quantity, pricePerUnit);
        rangeOfScienceIndex(scienceIndex);
    }

    private void rangeOfScienceIndex(int scienceIndex) {
        if (scienceIndex >= min && scienceIndex <= max) {
            this.scienceIndex = scienceIndex;
        } else {
            this.scienceIndex = 1;
            LOGGER.error("Science index should be between 1 and 10 ");
        }
    }

    public int getScienceIndex() {
        return scienceIndex;
    }

    public void setScienceIndex(@NotNull int scienceIndex) {
        rangeOfScienceIndex(scienceIndex);
    }

    @Override
    public double totalPrice() {
        if (this.getScienceIndex() < 1 || this.getScienceIndex() > 10) {
            return super.totalPrice() * getScienceIndex();
        }
        return super.totalPrice();
        // if index is other than expected, we use counting for simple book
    }

    @Override
    public String toString() {
        String str1 = super.toString();
        String str2 = ", science index=" + getScienceIndex();
        return str1 + str2;
    }
}
