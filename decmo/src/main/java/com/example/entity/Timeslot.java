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
@Table(name = "timeslot")
@Getter
@Setter
@NoArgsConstructor
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int slotid;

    @ManyToOne
    @JoinColumn(name = "stopid")
    private Stop stop;

    @Column
    private String hourrange;

    @Column
    private Boolean isactive;

    @Column
    private LocalDateTime lastmodified;

    @Column
    private String type;

    public Boolean getIsactive() {
        return isactive;
    }

    public void setIsactive(Boolean isactive) {
        this.isactive = isactive;
    }

    @Override
    public String toString() {
        return "Timeslot{" +
                "slotid=" + slotid +
                ", hourrange='" + hourrange + '\'' +
                ", type='" + type + '\'' +
                ", isactive=" + isactive +
                ", stop=" + (stop != null ? stop.getStopname() : "null") +
                '}';
    }
}
