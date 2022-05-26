package onthelive.kr.virtualImageControl.model.dashboard.k8s;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeInfoResources {
    private String resource;
    private Long requests;
    private Long Limits;
}
