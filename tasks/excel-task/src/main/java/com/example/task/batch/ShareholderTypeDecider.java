package com.example.task.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ShareholderTypeDecider implements JobExecutionDecider {

    @Override
    public @NonNull FlowExecutionStatus decide(@NonNull JobExecution jobExecution, StepExecution stepExecution) {
        // ExecutionContext에서 주주명부 유형 가져오기
        String shareholderType = (String) stepExecution.getExecutionContext()
                                                       .get("shareholderType");

        if (shareholderType == null) return FlowExecutionStatus.FAILED;

        return switch (shareholderType) {
            case "KSD" -> new FlowExecutionStatus("KSD");
            case "KEB" -> new FlowExecutionStatus("KEB");
            case "KB" -> new FlowExecutionStatus("KB");
            default -> FlowExecutionStatus.FAILED; // 조건이 맞지 않을 경우 실패 처리
        };
    }
}
