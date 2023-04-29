package com.yugabyte.samples.tradex.api.domain.business;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Location {
    int id;
    String name;
    String country;
    double latitude;
    double longitude;
    String website = "";

    public Location(int id, String name, String country, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
