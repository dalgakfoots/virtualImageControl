package onthelive.kr.virtualImageControl.service.virtualImageControl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onthelive.kr.virtualImageControl.model.virtualImageControl.VmInfo;
import onthelive.kr.virtualImageControl.model.support.FirstAndLastLineSupporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnTheLiveVmwareService {

    @Value("${powercli.createlinkedclone.ps1}")
    private String createLinkedCloneCode;
    @Value("${powercli.deletelnikedclone.ps1}")
    private String deleteLinkedCloneCode;
    @Value("${powercli.getlinkedcloneticket.ps1}")
    private String getLinkedCloneTicketCode;

    public VmInfo getLinkedCloneVm(String vmName) {
        // vmName 이 존재할 경우, 해당 VM의 새로운 티켓을 발급받는다.
        // 반드시 해당 VM이 켜져있어야 한다
        String command = getLinkedCloneTicketCode + " "+vmName;
        FirstAndLastLineSupporter firstAndLastLineInShellStream = getFirstAndLastLineInShellStream(command);

        return new VmInfo(firstAndLastLineInShellStream.getFirst(), firstAndLastLineInShellStream.getLast());
    }

    public VmInfo createLinkedCloneVm() {
        // ps1 코드를 실행시킨다.
        FirstAndLastLineSupporter result = getFirstAndLastLineInShellStream(createLinkedCloneCode);
        VmInfo info = new VmInfo(result.getFirst() , result.getLast());
        // 해당 url 을 전달한다. (30분 동안만 유효한 것으로 보임)
        return info;
    }

    public void deleteLinkedCloneVm(String vmName) {
        // vmName 을 ps1 코드에 전달하여 해당 vm 을 삭제한다.
        String command = deleteLinkedCloneCode+" "+vmName;
        try {
            Process powerShellProcess = Runtime.getRuntime().exec(command);
            BufferedReader stdout = new BufferedReader(new InputStreamReader(
                    powerShellProcess.getErrorStream()));
            String line;
            while((line = stdout.readLine()) != null) {
                log.info(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FirstAndLastLineSupporter getFirstAndLastLineInShellStream(String command){

        try {
            Process powerShellProcess = Runtime.getRuntime().exec(command);

            BufferedReader stdout = new BufferedReader(new InputStreamReader(
                    powerShellProcess.getInputStream()));
            String line = stdout.readLine();
            String next = line;

            String firstValue = "";
            String lastValue = "";

            // 스트림의 마지막 한줄을 변수에 저장하도록 처리
            for (boolean first = true, last = (line == null); !last; first = false, line = next) {
                last = ((next = stdout.readLine()) == null);
                log.info(line);
                if(first) {
                    firstValue = line;
                } else if (last) {
                    lastValue = line;
                }
            }

            stdout.close();
            return new FirstAndLastLineSupporter(firstValue, lastValue);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
