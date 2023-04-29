package com.yugabyte.samples.tradex.api.domain.business;

import com.yugabyte.samples.tradex.api.utils.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonalDetails {
    String fullName;
    String phone;
    String country;
    Gender gender;
    String address;
}
