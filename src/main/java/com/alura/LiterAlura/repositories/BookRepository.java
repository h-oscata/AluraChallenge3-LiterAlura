package com.alura.LiterAlura.repositories;

import com.alura.LiterAlura.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("SELECT b FROM Book b JOIN b.languages l WHERE l = :language")
    List<Book> findByLanguage(@Param("language") String language);

    Book findByTitle(String title);
}
