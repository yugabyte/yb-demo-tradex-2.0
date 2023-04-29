package com.yugabyte.samples.tradex.api.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
public class VerifyPinRequest {
    Integer pin;
}
