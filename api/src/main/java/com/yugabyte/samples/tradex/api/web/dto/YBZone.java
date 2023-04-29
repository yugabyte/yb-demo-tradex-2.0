package com.yugabyte.samples.tradex.api.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties
public class YBZone {
    private boolean active;
    private String code;
    private String name;
    private UUID uuid;
}
