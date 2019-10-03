package org.eea.tcf.listener.feign;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonpCharacterEscapes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderRequestData {
    @JsonProperty("workOrderId") private String workOrderId;
    @JsonProperty("workerId") private String workerId;
    @JsonProperty("workloadId") private String workloadId;
    @JsonProperty("requesterId") private String requesterId;
    @JsonProperty("inData") private String inData;

    @JsonProperty("resultUri") private String resultUri;
    @JsonProperty("notifyUri") private String notifyUri;
    @JsonProperty("workerEncryptionKey") private String workerEncryptionKey;
    @JsonProperty("dataEncryptionAlgorithm") private String dataEncryptionAlgorithm;
    @JsonProperty("encryptedSessionKey") private String sessionKey;
    @JsonProperty("sessionKeyIv") private String sessionKeyIv;
    @JsonProperty("requesterNonce") private String requesterNonce;
    @JsonProperty("encryptedRequestHash") private String requestHash;
    @JsonProperty("requesterSignature") private String requesterSignature;
}
