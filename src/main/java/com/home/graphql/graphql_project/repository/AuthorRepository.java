package com.home.graphql.graphql_project.repository;

import com.home.graphql.graphql_project.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
