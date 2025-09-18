package com.home.graphql.graphql_project.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.mapping.ToOne;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @OneToOne
    @JoinColumn(name = "author_id")
    private Author author;

}
