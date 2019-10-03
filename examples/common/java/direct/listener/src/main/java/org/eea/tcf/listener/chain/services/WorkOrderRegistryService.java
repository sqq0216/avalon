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

import org.eea.tcf.listener.chain.events.WorkOrderSubmittedEvent;
import org.eea.tcf.listener.chain.model.ChainWorkOrder;
import org.eea.tcf.listener.config.ConfigurationService;
import org.eea.tcf.listener.contract.generated.WorkOrderRegistry;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.ens.EnsResolutionException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.Optional;

import static org.eea.tcf.listener.utils.BytesUtils.*;

@Slf4j
@Service
public class WorkOrderRegistryService {

    private org.web3j.protocol.Web3jService web3jService;
    private final Web3j web3j;
    private final Web3jService web3JService;
    private final String registryAddress;

    @Autowired
    public WorkOrderRegistryService(Web3jService web3JService,
                                    ConfigurationService configurationService) {
        this.web3j = web3JService.getWeb3j();
        this.web3JService = web3JService;
        this.registryAddress =  configurationService.getWorkOrderRegistryAddress();
    }

    public WorkOrderRegistry getWorkOrderRegistryContract(ContractGasProvider contractGasProvider) {
        ExceptionInInitializerError exceptionInInitializerError =
                new ExceptionInInitializerError("Failed to load WorkOrderRegistry contract from address "
                        + registryAddress);

        if (registryAddress != null && !registryAddress.isEmpty()) {
            try {
                return WorkOrderRegistry.load(
                        registryAddress, web3j,
                        new ReadonlyTransactionManager(web3j, "0x0"),
                        contractGasProvider);
            } catch (EnsResolutionException e) {
                throw exceptionInInitializerError;
            }
        } else {
            throw exceptionInInitializerError;
        }
    }

    public WorkOrderRegistry getWorkOrderRegistryContract() {
        return getWorkOrderRegistryContract(new DefaultGasProvider());
    }


    Flowable<WorkOrderSubmittedEvent> getWorkOrderEventObservableToLatest(BigInteger from) {
        return getWorkOrderEventObservable(from, null);
    }

    Flowable<WorkOrderSubmittedEvent> getWorkOrderEventObservable(BigInteger from, BigInteger to) {
        DefaultBlockParameter fromBlock = DefaultBlockParameter.valueOf(from);
        DefaultBlockParameter toBlock = null;

        if (to != null)
            toBlock = DefaultBlockParameter.valueOf(to);

        return getWorkOrderRegistryContract().workOrderSubmittedEventFlowable(fromBlock, toBlock)
                .map(workOrderNotice -> {
                    return WorkOrderSubmittedEvent.builder()
                            .workOrderId(bytesToString(workOrderNotice.workOrderID))
                            .workerId(bytesToString(workOrderNotice.workerID))
                            .requesterId(bytesToString(workOrderNotice.requesterID))
                            .build();
                });
    }

    public Optional<ChainWorkOrder> workOrderGet(String workOrderId) {
        byte[] workOrderIdBytes = stringToBytes32(workOrderId);

        Tuple7<BigInteger, byte[], byte[], String, String, BigInteger, String> workOrder;
        RemoteCall<Tuple7<BigInteger, byte[], byte[], String, String, BigInteger, String>> getCall =
                getWorkOrderRegistryContract().workOrderGet(workOrderIdBytes);

        try {
            workOrder = getCall.send();
        } catch (Exception e) {
            log.error("Failed to retrieve [workerOrderId:{}, exception:{}]",
                    workOrderId, e.getMessage());
            return Optional.empty();
        }

        // TODO the result is never null so we look at the status (that should never be 0)
        if(workOrder.getValue1().equals(BigInteger.ZERO))
            return Optional.empty();

        return Optional.of(ChainWorkOrder.fromTuple(workOrderId, workOrder));
    }
}
