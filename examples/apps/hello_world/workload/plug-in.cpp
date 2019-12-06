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

#include <string>
#include "plug-in.h"
#include "logic.h"

// 此宏为特定应用程序注册工作负载处理器，将字符串与工作负载相关联，即工作订单请求中的字符串
REGISTER_WORKLOAD_PROCESSOR(workload_id, Workload)

void Workload::ProcessWorkOrder(
        std::string workload_id,
        const ByteArray& requester_id,//&表示按址传送
        const ByteArray& worker_id,
        const ByteArray& work_order_id,
        const std::vector<tcf::WorkOrderData>& in_work_order_data,
        std::vector<tcf::WorkOrderData>& out_work_order_data) {

    // Replace the dummy implementation below with invocation of
    // actual logic defined in logic.h and implemented in logic.cpp.
    /*std::string result_str("Error: under construction");
    ByteArray ba(result_str.begin(), result_str.end());
    AddOutput(0, out_work_order_data, ba);*/
	for (auto wo_data : in_work_order_data) {
		// Process the input data
		std::string result_str =
			ProcessHelloWorld(ByteArrayToString(wo_data.decrypted_data));

		ByteArray ba(result_str.begin(), result_str.end());
		AddOutput(wo_data.index, out_work_order_data, ba);
	}
}
