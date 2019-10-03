package org.eea.tcf.listener.feign;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class WorkOrderRequest {
    @JsonProperty("params") private WorkOrderRequestData workOrderRequestData;
    @JsonProperty("method") private final String method = "WorkOrderSubmit";
    @JsonProperty("jsonrpc") private final String jsonRpc = "2.0";
    @JsonProperty("id") private final int id = 11;

    public WorkOrderRequest(WorkOrderRequestData workOrderRequestData) {
        this.workOrderRequestData = workOrderRequestData;
    }
}
