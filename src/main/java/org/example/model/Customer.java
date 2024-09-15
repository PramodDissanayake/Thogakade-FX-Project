package org.example.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Customer {
    private String customerId;
    private String customerTitle;
    private String customerName;
    private Date dob;
    private Double salary;
    private String address;
    private String city;
    private String province;
    private String postalCode;

}
