package com.example.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "departuretime")
@Getter
@Setter
@NoArgsConstructor
public class Departuretime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int departureid;

    @ManyToOne
    @JoinColumn(name = "slotid")
    private Timeslot timeslot;

    @Column
    private String minute;

    @Column
    private boolean isactive;

    @Column
    private LocalDateTime lastmodified;

}
