package com.edu.hutech.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "trainee_course")
public class TraineeCourse extends BaseEntity implements Serializable {

    @Column(name = "code")
    private String code;

    @Column(name = "score")
    private Double score;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER )
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER )
    @JoinColumn(name = "course_id")
    private Course course;

}
