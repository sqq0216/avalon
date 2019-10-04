/*****************************************************************************
 * Copyright 2019 iExec Blockchain Tech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************/
package org.eea.tcf.listener.chain.services;

import feign.FeignException;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.eea.tcf.listener.chain.events.WorkOrderSubmittedEvent;
import org.eea.tcf.listener.chain.model.ChainWorkOrder;
import org.eea.tcf.listener.feign.WorkOrderClient;
import org.eea.tcf.listener.feign.WorkOrderRequest;
import org.eea.tcf.listener.feign.WorkOrderRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

@Slf4j
@Service
public class ListenerService {

    private Web3jService web3jService;
    private WorkOrderRegistryService workOrderRegistryService;
    private WorkOrderClient workOrderClient;


    @Autowired
    public ListenerService(Web3jService web3jService,
                           WorkOrderRegistryService workOrderRegistryService,
                           WorkOrderClient workOrderClient) {

        this.web3jService = web3jService;
        this.workOrderRegistryService = workOrderRegistryService;
        this.workOrderClient = workOrderClient;
    }

    public void startListening() {
        BigInteger latestBlockNumber =
                BigInteger.valueOf(web3jService.getLatestBlockNumber());

        Flowable<WorkOrderSubmittedEvent> eventSource =
                workOrderRegistryService.getWorkOrderEventObservable(latestBlockNumber, null);

        eventSource.subscribe((event) -> {
            Optional<ChainWorkOrder> optionalChainWorkOrder =
                    workOrderRegistryService.workOrderGet(event.getWorkOrderId());
            if (optionalChainWorkOrder.isEmpty()) {
                log.error("Cannot get workOrder [workOrderId:{}]", event.getWorkOrderId());
                return;
            }

            ChainWorkOrder workOrder = optionalChainWorkOrder.get();

            log.info("Received WorkOrderSubmitted event [workOrderId:{}, status:{}]",
                    workOrder.getWorkOrderId(),
                    workOrder.getStatus());

            // Translate and forward to JSON-RPC service
            WorkOrderRequestData params = WorkOrderRequestData.builder()
                    .workOrderId(workOrder.getWorkerId())
                    .workerId(workOrder.getWorkerId())
                    .workloadId(workOrder.getParams().getAppUri())
                    .requesterId(workOrder.getRequesterId())
                    .inData(workOrder.getRequest())
                    .build();
            WorkOrderRequest request = new WorkOrderRequest(params);
            workOrderClient.submitWorkOrder(request);

        },
                (error) -> {
            try {
                throw error;
            } catch(FeignException e) {
                String content = new String(e.content());

                log.error("The TCF web service returned an error " +
                        "[status: {}, message: {}, body:{}]",
                        e.status(),
                        e.getMessage(),
                        content);
            } catch(Throwable e) {
                log.error("Cannot call TCF web service [%s]", e.getMessage());
            }
            log.warn("Skipping Work Order");
                });
    }
}
