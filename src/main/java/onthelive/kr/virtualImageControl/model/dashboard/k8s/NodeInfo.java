package onthelive.kr.virtualImageControl.model.dashboard.k8s;

import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import onthelive.kr.virtualImageControl.model.support.NodeConditionType;
import onthelive.kr.virtualImageControl.model.support.PodPhaseType;

import static onthelive.kr.virtualImageControl.model.support.NodeConditionType.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeInfo {

    private String name;

    /*Conditions*/
    private NodeConditionCustom ready; // 노드가 상태 양호하며 파드를 수용할 준비가 되어 있는경우 True, 노드 상태 불량시 False, 응답이 없을 경우 UnKnown
    private NodeConditionCustom networkUnavailable; // 네트워크가 올바르게 구성되지 못했으면 True , 그밖에 False
    private NodeConditionCustom MemoryPressure; // 노드 메모리 상에 압박이 있는 경우 True, 그밖에 False
    private NodeConditionCustom diskPressure; // 디스크 사이즈 상에 압박이 있는 경우 True, 그밖에 False
    private NodeConditionCustom PidPressure; // 프로세스 상에 압박이 있는 (노드 상에 많은 프로세스들이 존재하는) 경우 True, 그밖에 False

    /*Pods*/
    private List<NodeInfoPod> nonTerminatedPods;

    public void setConditions(List<NodeCondition> conditions) {
        conditions.forEach(
                condition -> {
                    NodeConditionCustom conditionCustom = new NodeConditionCustom(
                            condition.getStatus(),
                            condition.getReason(),
                            condition.getMessage()
                    );
                    switch (NodeConditionType.valueOf(condition.getType())) {
                        case Ready:
                            setReady(conditionCustom);
                            break;
                        case PIDPressure:
                            setPidPressure(conditionCustom);
                            break;
                        case DiskPressure:
                            setDiskPressure(conditionCustom);
                            break;
                        case MemoryPressure:
                            setMemoryPressure(conditionCustom);
                            break;
                        case NetworkUnavailable:
                            setNetworkUnavailable(conditionCustom);
                            break;
                        default:
                            break;
                    }
                }
        );
    }

    public void setNonTerminatedPodsInit(List<Pod> pods){
        List<NodeInfoPod> nodeInfoPods = new ArrayList<>();

        pods.forEach(
                pod -> {
                    NodeInfoPod nodeInfoPod = new NodeInfoPod();

                    nodeInfoPod.setName(pod.getMetadata().getName());
                    PodPhaseType podPhaseType = PodPhaseType.valueOf(pod.getStatus().getPhase());

                    System.out.println("podPhaseType = " + podPhaseType);

                    nodeInfoPod.setPhase(PodPhaseType.valueOf(pod.getStatus().getPhase()));
                    nodeInfoPod.setConditions(pod.getStatus().getConditions());
                    nodeInfoPod.setLabels(pod.getMetadata().getLabels());
                    nodeInfoPod.setPodIp(pod.getStatus().getPodIP());

                    nodeInfoPods.add(nodeInfoPod);
                }
        );

        setNonTerminatedPods(nodeInfoPods);
    }

}
