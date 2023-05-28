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
    List<String> queries = new ArrayList<>(0);
    List<String> explainResults = new ArrayList<>(0);
    long latencyMillis;
    ConnectionInfo connectionInfo;
}
