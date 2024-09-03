package com.hasudo.hasudobatch.mapper.postgresql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface PostgresqlMapper {
    void testInsert(@Param("id") String id,
                    @Param("message") String message,
                    @Param("record_time") LocalDateTime record_time);
}
