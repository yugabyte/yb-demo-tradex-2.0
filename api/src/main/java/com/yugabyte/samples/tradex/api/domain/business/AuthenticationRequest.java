package com.yugabyte.samples.tradex.api.domain.business;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class AuthenticationRequest {

    @NotBlank
    private String login;
    @NotBlank
    private String credentials;
}
