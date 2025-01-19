package com.alura.LiterAlura.repositories;

import com.alura.LiterAlura.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Author findByName(String name);
}
