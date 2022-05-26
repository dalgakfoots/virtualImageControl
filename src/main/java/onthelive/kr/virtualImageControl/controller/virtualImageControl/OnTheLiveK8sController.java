package onthelive.kr.virtualImageControl.controller.virtualImageControl;

import lombok.RequiredArgsConstructor;
import onthelive.kr.virtualImageControl.model.virtualImageControl.ServicePerPod;
import onthelive.kr.virtualImageControl.service.virtualImageControl.OnTheLiveK8sService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apis/v1/k8s")
public class OnTheLiveK8sController {

    @Value("${kubernetes.master.url}")
    private String baseUrl;

    private final OnTheLiveK8sService onTheLiveK8sService;

    @GetMapping("/services")
    public List<ServicePerPod> getList() {
        List<ServicePerPod> servicePerPodList = onTheLiveK8sService.getServicePerPodList();
        servicePerPodList.forEach(
                e -> e.setAccessUrl(baseUrl)
        );

        return servicePerPodList;
    }

    @GetMapping("/services/{serviceName}")
    public ServicePerPod getService(@PathVariable String serviceName) {
        List<ServicePerPod> servicePerPodList = onTheLiveK8sService.getServicePerPodList();
        ServicePerPod servicePerPod = servicePerPodList
                .stream()
                .filter(e -> e.getServiceName().equals(serviceName))
                .findFirst()
                .get();

        servicePerPod.setAccessUrl(baseUrl);

        return servicePerPod;
    }

    @PostMapping("/services/create")
    public ServicePerPod createService() {
        ServicePerPod servicePerPod = onTheLiveK8sService.createServicePerPod();
        servicePerPod.setAccessUrl(baseUrl);
        return servicePerPod;
    }

    @DeleteMapping("/services/delete")
    public ResponseEntity deleteService(@RequestParam(value = "serviceName") String serviceName,
                                        @RequestParam(value = "deploymentName") String deploymentName) {

        onTheLiveK8sService.deleteServicePerPod(serviceName, deploymentName);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
