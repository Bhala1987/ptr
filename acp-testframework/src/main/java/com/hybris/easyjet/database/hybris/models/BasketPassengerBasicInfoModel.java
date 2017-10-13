package com.hybris.easyjet.database.hybris.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by vijayapalkayyam on 30/05/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasketPassengerBasicInfoModel {

    private String title;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String phone;
    private String ejNumber;
}
