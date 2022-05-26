package onthelive.kr.virtualImageControl.model.virtualImageControl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicePerPod {
    private String serviceName;
    private Map<String, String> selector;
    private int portNumber;
    private String deploymentName;

    private String accessUrl;

    private String secret;

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl + ":"+ portNumber;
    }
}
