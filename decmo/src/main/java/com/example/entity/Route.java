package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "route")
@Getter
@Setter
@NoArgsConstructor
public class Route {  // route -> Route로 수정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int routeid;

    @Column
    private String routename;

    @Column
    private String description;

    @Column
    private String pathcoordinates;

    @Column
    private boolean isactive;
}