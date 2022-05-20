package onthelive.kr.virtualImageControl.controller;

import lombok.RequiredArgsConstructor;
import onthelive.kr.virtualImageControl.model.ServicePerPod;
import onthelive.kr.virtualImageControl.service.OnTheLiveK8sService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apis/v1")
public class OnTheLiveK8sController {

    @Value("${kubernetes.master.url}")
    private String baseUrl;

    private final OnTheLiveK8sService onTheLiveK8sService;

    @GetMapping("/k8s/services")
    public List<ServicePerPod> getList() {
        List<ServicePerPod> servicePerPodList = onTheLiveK8sService.getServicePerPodList();
        servicePerPodList.forEach(
                e -> e.setAccessUrl(baseUrl)
        );

        return servicePerPodList;
    }

    @PostMapping("/k8s/services/create")
    public ServicePerPod createService() {
        ServicePerPod servicePerPod = onTheLiveK8sService.createServicePerPod();
        servicePerPod.setAccessUrl(baseUrl);
        return servicePerPod;
    }

    @DeleteMapping("/k8s/services/delete")
    public ResponseEntity deleteService(@RequestParam(value = "serviceName") String serviceName,
                                    @RequestParam(value = "deploymentName") String deploymentName) {

        onTheLiveK8sService.deleteServicePerPod(serviceName, deploymentName);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}