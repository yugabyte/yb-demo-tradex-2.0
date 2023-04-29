package com.yugabyte.samples.tradex.api.domain.business;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {

    private String token;
    private Integer id;
    private String preferredRegion;
    private String type;
    private String status;
    private String message;
}
