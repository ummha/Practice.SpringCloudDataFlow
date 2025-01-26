package com.example.task.batch;

import com.example.task.dto.KsdExcelRow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPipelineWriter implements ItemWriter<KsdExcelRow> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    private final int TTL = 3600 * 24; // Hash 만료시간: 하루
    private final String KEY_PREFIX = "KW2_SHAREHOLDER";

    private long testCount = 0;

    @Override
    @Transactional
    public void write(@NonNull Chunk<? extends KsdExcelRow> chunk) throws Exception {
        try {
//            jdbcTemplate.execute("ALTER SEQUENCE ADMIN.TEST_SEQ INCREMENT BY " + chunk.size());
//            Long sequence = jdbcTemplate.queryForObject("SELECT ADMIN.TEST_SEQ.NEXTVAL FROM DUAL", Long.class);
            log.info("isBusy: {}, isEnd: {}, isEmpty: {}, chunkSize: {}", chunk.isBusy(), chunk.isEnd(), chunk.isEmpty(), chunk.size());

            redisTemplate.executePipelined((RedisCallback<?>) connection -> {

                for (KsdExcelRow item : chunk) {
                    final String KEY = KEY_PREFIX + item.actualShareholderNo();
                    byte[] keyBytes = KEY.getBytes(StandardCharsets.UTF_8);
                    try {
                        connection.hashCommands()
                                  .hSet(keyBytes,
                                        "data".getBytes(StandardCharsets.UTF_8),
                                        objectMapper.writeValueAsString(item).getBytes(StandardCharsets.UTF_8));
                        connection.keyCommands()
                                  .expire(keyBytes, TTL);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
                return null; // executePipelined는 반환값이 필요하지 않으므로 null 반환
            });

            testCount += chunk.size();
            log.info("testCount: {}", testCount);
        } finally {
//            jdbcTemplate.execute("ALTER SEQUENCE ADMIN.TEST_SEQ INCREMENT BY 1");
        }
    }
}
