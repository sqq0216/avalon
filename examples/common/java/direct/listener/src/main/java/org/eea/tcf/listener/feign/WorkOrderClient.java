package org.eea.tcf.listener.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "WorkOrderClient",
        url = "${listener.apiEndpoint}",
        configuration = FeignConfiguration.class)

public interface WorkOrderClient {
    @PostMapping("/")
    WorkOrderResponse submitWorkOrder(@RequestBody WorkOrderRequest request) throws FeignException;
}
