package com.yugabyte.samples.tradex.api.domain.db;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class RefDatum extends BaseEntity {
    private RefDatumId id;
    private String keyValue;
}
