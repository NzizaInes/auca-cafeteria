package com.auca.cafeteria.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("KITCHEN_STAFF")
public class KitchenStaff extends User {

    private String staffId;

    private String shift;
}