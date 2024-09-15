package org.example.model.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Table02 {
    private String customerId;
    private Double salary;
    private String address;
    private String city;
    private String province;
    private String postalCode;
}
