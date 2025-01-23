package com.example.task.batch;

import com.example.task.dto.ExcelRow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPipelineWriter implements ItemWriter<ExcelRow> {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    private final String KEY = "KW2_SHAREHOLDER";

    @Override
    public void write(@NonNull Chunk<? extends ExcelRow> chunk) throws Exception {
        redisTemplate.executePipelined((RedisCallback<?>) connection -> {
            log.info("isBusy: {}, isEnd: {}, isEmpty: {}, chunkSize: {}", chunk.isBusy(), chunk.isEnd(), chunk.isEmpty(), chunk.size());

            for (ExcelRow item : chunk) {
                final String key = KEY + item.actualShareholderNo();

                try {
                    connection.hashCommands()
                              .hSet(key.getBytes(StandardCharsets.UTF_8),
                                    "data".getBytes(StandardCharsets.UTF_8),
                                    objectMapper.writeValueAsString(item).getBytes(StandardCharsets.UTF_8));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            connection.keyCommands()
                      .expire(KEY.getBytes(StandardCharsets.UTF_8), 3600); // Hash 만료시간: 1시간

            return null; // executePipelined는 반환값이 필요하지 않으므로 null 반환
        });
    }
}
