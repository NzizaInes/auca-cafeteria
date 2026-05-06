package com.auca.cafeteria.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

    @Column(unique = true)
    private String studentId;

    private String faculty;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;
}