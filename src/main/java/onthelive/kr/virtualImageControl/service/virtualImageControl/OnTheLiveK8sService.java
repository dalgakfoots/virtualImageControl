package onthelive.kr.virtualImageControl.service.virtualImageControl;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onthelive.kr.virtualImageControl.common.RandomStringUtil;
import onthelive.kr.virtualImageControl.model.virtualImageControl.ServicePerPod;
import onthelive.kr.virtualImageControl.model.support.NumberSupporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnTheLiveK8sService {

    @Value("${service.name.prefix}")
    private String serviceNamePrefix;
    @Value("${selector.value.prefix}")
    private String selectorValuePrefix;
    @Value("${deployment.name.prefix}")
    private String deploymentNamePrefix;

    public List<ServicePerPod> getServicePerPodList() {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            log.info("getServicePerPodList....");
            List<ServicePerPod> servicePerPodList = new ArrayList<>();
            List<io.fabric8.kubernetes.api.model.Service> services = client.services().inNamespace("default").list().getItems();
            services.stream()
                    .filter(e -> e.getMetadata().getName().contains("my-service"))
                    .forEach(e -> {
                        ServicePerPod servicePerPod =
                                ServicePerPod.builder()
                                        .serviceName(e.getMetadata().getName())
                                        .selector(e.getSpec().getSelector())
                                        .portNumber(e.getSpec().getPorts().get(0).getNodePort())
                                        .build();

                        List<Deployment> deployments =
                                client.apps()
                                        .deployments()
                                        .inNamespace("default")
                                        .list()
                                        .getItems()
                                        .stream().filter(
                                                deployment -> deployment.getSpec().getSelector().getMatchLabels().equals(e.getSpec().getSelector())
                                        ).collect(Collectors.toList());

                        servicePerPod.setDeploymentName(deployments.get(0).getMetadata().getName());
                        servicePerPodList.add(servicePerPod);
                    });

            return servicePerPodList;
            //[ServicePerPod(serviceName=my-service, selector={app=shell}, portNumber=30007, deploymentName=coresecu-testdeployment),
            // ServicePerPod(serviceName=my-service1, selector={app=shell1}, portNumber=30008, deploymentName=coresecu-testdeployment1)]
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ServicePerPod createServicePerPod() {
        List<ServicePerPod> servicePerPodList = getServicePerPodList();

        NumberSupporter nextNumbers = new NumberSupporter(30000,0);

        try {
            nextNumbers = getNextNumbers(servicePerPodList); // 현재 파드가 아무것도 없을 경우 예외 발생
        } catch (Exception e) {
            e.printStackTrace();
        }

        int nextNumber = nextNumbers.getNextNumber();
        int nextPortNumber = nextNumbers.getNextPortNumber();

        io.fabric8.kubernetes.api.model.Service newService = getNewService(nextNumber, nextPortNumber);
        Deployment newDeployment = getNewDeployment(nextNumber);

        try (KubernetesClient client = new DefaultKubernetesClient()) {

            client.services().inNamespace("default").create(newService);
            client.apps().deployments().inNamespace("default").create(newDeployment);

            log.info("created Service - {} | NodePort : {}"
                    ,serviceNamePrefix+nextNumber
                    ,nextPortNumber);
            log.info("created Deployment - {}",deploymentNamePrefix+nextNumber);

            return ServicePerPod.builder()
                    .serviceName(serviceNamePrefix+nextNumber)
                    .selector(Collections.singletonMap("app",selectorValuePrefix+nextNumber))
                    .deploymentName(deploymentNamePrefix+nextNumber)
                    .portNumber(nextPortNumber)
                    .secret(RandomStringUtil.randomAlphaNumeric(32))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    public void deleteServicePerPod(String serviceName , String deploymentName) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            log.info("Delete service - {} : {}"
                    ,serviceName
                    ,client.services().inNamespace("default").withName(serviceName).delete());
            log.info("Delete deployment - {} : {}"
                    ,deploymentName
                    ,client.apps().deployments().inNamespace("default").withName(deploymentName).delete());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private NumberSupporter getNextNumbers(List<ServicePerPod> servicePerPodList) {
        int maxPortNumber = servicePerPodList
                .stream()
                .mapToInt(ServicePerPod::getPortNumber)
                .max()
                .getAsInt();

        ServicePerPod maxNumberServicePerPod =
                servicePerPodList
                        .stream()
                        .filter(e -> e.getPortNumber() == maxPortNumber)
                        .collect(Collectors.toList())
                        .get(0);

        String maxNumberServiceName = maxNumberServicePerPod.getServiceName();
        int maxNumber = Integer.parseInt(maxNumberServiceName.replaceAll("[^\\d]", ""));

        int nextPortNumber = maxPortNumber + 1;
        int nextNumber = maxNumber + 1;

        return new NumberSupporter(nextPortNumber, nextNumber);
    }

    private io.fabric8.kubernetes.api.model.Service getNewService(int nextNumber, int nextPortNumber) {
        io.fabric8.kubernetes.api.model.Service service = new ServiceBuilder()
                .withNewMetadata()
                .withName(serviceNamePrefix+ nextNumber)
                .endMetadata()
                .withNewSpec()
                .withSelector(Collections.singletonMap("app",selectorValuePrefix+ nextNumber))
                .addNewPort()
                .withProtocol("TCP")
                .withPort(80)
                .withTargetPort(new IntOrString(4200))
                .withNodePort(nextPortNumber)
                .endPort()
                .withType("NodePort")
                .endSpec().build();

        return service;
    }

    private Deployment getNewDeployment(int nextNumber){
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName(deploymentNamePrefix+nextNumber)
                .endMetadata()
                .withNewSpec()
                .withReplicas(1)
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app",selectorValuePrefix+nextNumber)
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName("coresecu-container")
                .withImage("hsw3074/shellinabox_custom:latest")
                .addNewPort()
                .withContainerPort(4200)
                .endPort()
                .endContainer()
                .endSpec()
                .endTemplate()
                .withNewSelector()
                .addToMatchLabels("app",selectorValuePrefix+nextNumber)
                .endSelector()
                .endSpec()
                .build();

        return deployment;
    }
}
