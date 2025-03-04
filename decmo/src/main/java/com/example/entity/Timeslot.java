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

    // 새로 추가한 type 컬럼
    @Column
    private String type;

    // Lombok @Getter, @Setter가 있지만, 
    // isactive 필드에 대한 Getter/Setter를 직접 작성해두셨으므로 그대로 둡니다.
    public Boolean getIsactive() {
        return isactive;
    }

    public void setIsactive(Boolean isactive) {
        this.isactive = isactive;
    }
}
