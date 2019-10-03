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
package org.eea.tcf.listener;


import org.eea.tcf.listener.chain.services.ListenerService;
import org.eea.tcf.listener.chain.services.WorkOrderRegistryService;
import org.eea.tcf.listener.config.ConfigurationModel;
import lombok.extern.slf4j.Slf4j;
import org.eea.tcf.listener.config.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Optional;


@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableRetry
@EnableAsync
@Slf4j
public class Application implements CommandLineRunner {
    @Autowired
    private ConfigurationService config;

    @Autowired
    private WorkOrderRegistryService workOrderRegistryService;

    @Autowired
    private ListenerService listenerService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        ConfigurationModel model = ConfigurationModel.builder()
                .workOrderRegistryAddress(config.getWorkOrderRegistryAddress())
                .blockChainNodeAddress(config.getBlockchainNodeAddress())
                .build();

        if (config.getHttpProxyHost() != null &&
                config.getHttpProxyPort() != null) {
            log.info("Running with proxy [proxyHost:{}, proxyPort:{}]",
                    config.getHttpProxyHost(),
                    config.getHttpProxyPort());
        }

        // Start listening for events
        listenerService.startListening();

        log.info("Listener running");
    }
}
