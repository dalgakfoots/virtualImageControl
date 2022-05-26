package onthelive.kr.virtualImageControl.model.dashboard.k8s;

import io.fabric8.kubernetes.api.model.PodCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import static onthelive.kr.virtualImageControl.model.support.PodConditionType.*;
import onthelive.kr.virtualImageControl.model.support.PodConditionType;
import onthelive.kr.virtualImageControl.model.support.PodPhaseType;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeInfoPod {

    private String name;

    private PodPhaseType phase; // PodPhaseType 참조

    private PodConditionCustom initialized; // 모든 초기화 컨테이너가 완료되었는지
    private PodConditionCustom containersReady; // 파드의 모든 컨테이너가 준비되었는지
    private PodConditionCustom podScheduled; // 파드가 노드에 스케쥴 되었는지
    private PodConditionCustom ready; // 파드가 요청을 처리할 수 있으며, 서비스의 로드 벨런싱 풀에 추가되었는지

    private Map<String, String> labels;
    private String podIp;

    public void setConditions(List<PodCondition> conditions) {
        conditions.forEach(
                condition -> {
                    PodConditionCustom conditionCustom = new PodConditionCustom(
                            condition.getStatus(),
                            condition.getReason(),
                            condition.getMessage()
                    );

                    switch (PodConditionType.valueOf(condition.getType())) {
                        case PodScheduled:
                            setPodScheduled(conditionCustom);
                            break;
                        case ContainersReady:
                            setContainersReady(conditionCustom);
                            break;
                        case Initialized:
                            setInitialized(conditionCustom);
                            break;
                        case Ready:
                            setReady(conditionCustom);
                            break;
                        default:
                            break;
                    }
                }
        );
    }

}
