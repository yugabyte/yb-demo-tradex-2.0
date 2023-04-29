package com.yugabyte.samples.tradex.api.domain.business;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class SignUpResponse {

    private Integer id;
    private String prefRegion;
    private String login;
    private String status;
}
