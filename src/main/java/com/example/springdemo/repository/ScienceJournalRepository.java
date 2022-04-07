package com.example.springdemo.repository;

import com.example.springdemo.models.ScienceJournal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScienceJournalRepository extends JpaRepository<ScienceJournal, Long> {
}
