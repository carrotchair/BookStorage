package com.example.springdemo.repository;

import com.example.springdemo.models.AntiqueBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AntiqueBookRepository extends JpaRepository<AntiqueBook, Long> {
}
