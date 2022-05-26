package onthelive.kr.virtualImageControl.model.dashboard.k8s;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeConditionCustom {

    private String status;
    private String reason;
    private String message;
}
