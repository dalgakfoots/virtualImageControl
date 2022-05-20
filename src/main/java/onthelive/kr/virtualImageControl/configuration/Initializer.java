package onthelive.kr.virtualImageControl.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class Initializer {

    @Value("${KUBECONFIG}")
    private String kubeconfig;

    @Value("${KUBERNETES_MASTER}")
    private String master;

    @Value("${KUBERNETES_TRUST_CERTIFICATES}")
    private String trustCertificates;

    @PostConstruct
    private void init() {
        System.setProperty("kubernetes.master", master);

        System.setProperty("kubeconfig", kubeconfig);

        System.setProperty("kubernetes.trust.certificates", trustCertificates);
    }
}
