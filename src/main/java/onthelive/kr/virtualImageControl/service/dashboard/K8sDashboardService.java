package onthelive.kr.virtualImageControl.service.dashboard;

import com.google.gson.Gson;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import onthelive.kr.virtualImageControl.model.dashboard.k8s.NodeInfo;
import onthelive.kr.virtualImageControl.model.support.NodeConditionType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class K8sDashboardService {

    // 노드의 목록과 현재 상태
    public List<NodeInfo> getNodeList() {

        try (KubernetesClient client = new DefaultKubernetesClient()) {

            List<NodeInfo> nodeInfoList = new ArrayList<>();

            List<Node> nodes = client.nodes().list().getItems();
            List<Pod> pods = client.pods().inNamespace("default").list().getItems();

            nodes.forEach(
                    node -> {
                        NodeInfo info = new NodeInfo();
                        info.setName(node.getMetadata().getName());
                        info.setConditions(node.getStatus().getConditions());

                        List<Pod> collect = pods.stream().filter(
                                pod -> pod.getSpec().getNodeName().equals(node.getMetadata().getName())
                        ).collect(Collectors.toList());

                        info.setNonTerminatedPodsInit(collect);

                        nodeInfoList.add(info);
                    }
            );

            return nodeInfoList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
