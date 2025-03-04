package com.example.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.entity.Route;

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
@Table(name = "operationtime")
@Getter
@Setter
@NoArgsConstructor
public class Operationtime {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int operationid;

   @ManyToOne
   @JoinColumn(name = "routeid")
   private Route route;

   @Column
   private LocalTime starttime;

   @Column
   private LocalTime endtime; // endTime -> endtime으로 변경

   @Column
   private boolean weekdayonly;

   @Column
   private LocalDateTime lastmodified;
}