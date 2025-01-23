package com.example.task.parameter;

import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@ToString
public class CreateJobParameter {
    private final int chunkSize;
    private final LocalDateTime targetDateTime;

    /**
     * @param chunkSize 청크 사이즈
     * @param date 날짜 ("yyyy-MM-dd")
     * @param time 시간 ("HH:mm:ssSSS")
     */
    public CreateJobParameter(int chunkSize, String date, String time) {
        this.chunkSize = chunkSize;
        if (!StringUtils.hasText(date)) {
            this.targetDateTime = LocalDateTime.now();
            return;
        }

        LocalDate targetDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime targetTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ssSSS"));

        this.targetDateTime = LocalDateTime.of(targetDate, targetTime);
    }
}
