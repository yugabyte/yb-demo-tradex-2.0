package com.yugabyte.samples.tradex.api.domain.business;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DBClusterInfo {
    int id;
    String title;
    String subtitle;
}
