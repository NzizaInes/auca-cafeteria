package com.auca.cafeteria.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("CASHIER")
public class Cashier extends User {

    private String cashierId;

    private String shift;
}