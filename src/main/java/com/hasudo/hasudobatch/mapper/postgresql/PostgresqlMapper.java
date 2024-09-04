package com.hasudo.hasudobatch.mapper.postgresql;

import com.hasudo.hasudobatch.model.Omms;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostgresqlMapper {
    void insertOmmsList(@Param("ommsList") List<Omms> ommsList);
}
