package com.yugabyte.samples.tradex.api.domain.db;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class AppUserId implements Serializable {
    private static final long serialVersionUID = -4563840026487429109L;
    @NotNull
    private Integer id;
    @NotNull
    @NotBlank
    private String preferredRegion;

}
