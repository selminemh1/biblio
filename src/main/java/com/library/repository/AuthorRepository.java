package com.library.repository;

import com.library.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    List<Author> findByLastNameContainingIgnoreCase(String lastName);
    
    @Query("SELECT a FROM Author a WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Author> searchByName(String name);
    
    List<Author> findByNationality(String nationality);
}
