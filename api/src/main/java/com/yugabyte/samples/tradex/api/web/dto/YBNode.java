package com.yugabyte.samples.tradex.api.web.dto;

import lombok.Data;

@Data
public class YBNode {
    private String nodeName;
    private boolean inUse;
    private String ip;

}
