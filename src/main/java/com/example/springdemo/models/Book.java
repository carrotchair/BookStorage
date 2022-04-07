package com.example.springdemo.models;

import com.sun.istack.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Book {

    private String name;
    private String author;
    private @Id long barcode;
    private int quantity;
    private double pricePerUnit;

    Book() {}

    public Book(String name, String author, long barcode, int quantity, double pricePerUnit) {
        this.name = name;
        this.author = author;
        this.barcode = barcode;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NotNull String author) {
        this.author = author;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(@NotNull long barcode) {
        this.barcode = barcode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity cannot be negative.");
        this.quantity = quantity;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(@NotNull double pricePerUnit) {
        if (pricePerUnit < 0)
            throw new IllegalArgumentException("Price cannot be negative.");
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Book))
            return false;
        Book bookModel = (Book) o;
        return Objects.equals(this.name, bookModel.name)
                && Objects.equals(this.author, bookModel.author)
                && Objects.equals(this.barcode, bookModel.barcode)
                && Objects.equals(this.quantity, bookModel.quantity)
                && Objects.equals(this.pricePerUnit, bookModel.pricePerUnit);
    }

    public double totalPrice() {
        return quantity * pricePerUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.author,
                this.barcode, this.quantity, this.pricePerUnit);
    }

    @Override
    public String toString() {
        return "Book: " + "name='" + this.name + '\''
                + ", author='" + this.author + '\''
                + ", barcode='" + this.barcode + '\''
                + ", quantity='" + this.quantity + '\''
                + ", price per unit='" + this.pricePerUnit + '\'';
    }
}
