package com.yugabyte.samples.tradex.api.domain.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DBNode {

    String id;
    String region;
    String zone;
    String nodeType;
    Location location;

}
