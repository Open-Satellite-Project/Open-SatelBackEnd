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
@Table(name = "stop")
@Getter
@Setter
@NoArgsConstructor
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int stopid;

    @Column
    private int routeid;

    @Column
    private String stopname;

    // BUS GPS 좌표 , 소수점 관리 , 카카오맵 정류장 위치 표시할 때 사용
    @Column
    private double latitude;

    @Column
    private double longitude;
    
    // 버스 노선 정류장 순서 표시 (근데 어차피 단일이라 별 상관 없음) , 순차적인 정류장 순서 관리,
    // 경로 표시할 때 정류장 순서 결정
    @Column
    private int sequencenumber;

    //정류장 간 예상 소요 시간 , 도착 예정 시간 계산에 사용 , 분 단위로 저장하기 위해 int 사용
    @Column
    private int estimatedtime;

}
