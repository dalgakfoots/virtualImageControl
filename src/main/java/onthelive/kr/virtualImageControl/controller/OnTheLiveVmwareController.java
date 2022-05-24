package onthelive.kr.virtualImageControl.controller;

import lombok.RequiredArgsConstructor;
import onthelive.kr.virtualImageControl.model.VmInfo;
import onthelive.kr.virtualImageControl.service.OnTheLiveVmwareService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apis/v1/vmware")
public class OnTheLiveVmwareController {

    private final OnTheLiveVmwareService vmwareService;

    @GetMapping("/vm/{vmName}")
    public ResponseEntity getLinkedCloneVmTicket(@PathVariable String vmName) {
        VmInfo info = vmwareService.getLinkedCloneVm(vmName);
        return new ResponseEntity(info , HttpStatus.OK);
    }

    @PostMapping("/vm")
    public ResponseEntity<VmInfo> createLinkedCloneVm(){
        VmInfo linkedCloneVm = vmwareService.createLinkedCloneVm();
        return new ResponseEntity<>(linkedCloneVm , HttpStatus.OK);
    }

    @DeleteMapping("/vm")
    public ResponseEntity deleteLinkedCloneVm(@RequestParam(value = "vmName") String vmName){
        vmwareService.deleteLinkedCloneVm(vmName);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
