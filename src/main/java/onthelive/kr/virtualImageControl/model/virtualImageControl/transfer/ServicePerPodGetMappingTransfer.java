package onthelive.kr.virtualImageControl.model.virtualImageControl.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ServicePerPodGetMappingTransfer {
    private String serviceName;
    private Map<String, String> selector;
    private int portNumber;
    private String deploymentName;

    private String accessUrl;
}
