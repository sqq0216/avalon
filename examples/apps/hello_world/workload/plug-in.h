/* Copyright 2019 Intel Corporation
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
*/

#pragma once

#include <string>
#include "workload_processor.h"

// replace $NameSpace$ below with a namespace, e.g. HelloWorld 通过命名空间区分不同workerload
namespace HelloWorld {

// replace $WorkloadId$ below with a workload id, e.g. "hello-world"
const char* workload_id = hello-world;

class Workload : public WorkloadProcessor {
public:
    // 为worker克隆一个workerloadprocessor类
    
    IMPL_WORKLOAD_PROCESSOR_CLONE(Workload)
    //该函数需要重写
    void ProcessWorkOrder(
        std::string workload_id,
        const ByteArray& requester_id,
        const ByteArray& worker_id,
        const ByteArray& work_order_id,
        const std::vector<tcf::WorkOrderData>& in_work_order_data,
        std::vector<tcf::WorkOrderData>& out_work_order_data);


    void AddOutput(int index,
                   std::vector<tcf::WorkOrderData>& out_work_order_data,
                   ByteArray& data){
        int out_wo_data_size = out_work_order_data.size();
        // If the out_work_order_data has entry to hold the data
        if (index < out_wo_data_size) {
            tcf::WorkOrderData& out_wo_data = out_work_order_data.at(index);
            out_wo_data.decrypted_data = data;
        } else {
            // Create a new entry
            out_work_order_data.emplace_back(index, data);
        }
    };
};

}  // namespace HelloWorld

// replace $NameSpace$ below with a workload namespace, e.g. HelloWorld
using namespace HelloWorld;


