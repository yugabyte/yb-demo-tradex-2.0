package com.yugabyte.samples.tradex.api.service;

import com.yugabyte.samples.tradex.api.web.dto.ConnectionInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DBOperationResult {
    Object data;
    List<String> queries = new ArrayList<>();
    List<String> explainResults = new ArrayList<>();
    long latencyMillis;
    ConnectionInfo connectionInfo;
}
