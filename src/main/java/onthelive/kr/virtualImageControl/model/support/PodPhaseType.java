package onthelive.kr.virtualImageControl.model.support;

public enum PodPhaseType {
    Pending, // 파드가 쿠버네티스 클러스터에서 승인되었지만, 실행 준비가 되지 않음.
    Running, // 파드가 노드에 바인딩 되었고, 모든 컨테이너가 생성되었다. 적어도 하나의 컨테니너가 실행중이다.
    Succeeded, // 파드에 있는 모든 컨테이너들이 성공적으로 종료되었고, 재시작되지 않을 것이다.
    Failed, // 파드에 이는 모든 컨테이너가 종료되었고, 적어도 하나 이상의 컨테이너가 실패로 종료되었다.
    Unknown, // 어떤 이유에 의해서 파드의 상태를 얻을 수 없다. 일반적으로 파드와 노드와의 통신 오류로 인해 발생한다.
}
