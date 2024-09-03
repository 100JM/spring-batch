package com.hasudo.hasudobatch.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.hasudo.hasudobatch.mapper.tiberoegis", sqlSessionFactoryRef = "tiberoegisSqlSessionFactory")
public class TiberoEGISDataSourceConfig {

    @Bean(name = "tiberoegisDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.tiberoegis")
    public DataSource tiberoEgisDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "tiberoegisSqlSessionFactory")
    public SqlSessionFactory tiberoEgisSqlSessionFactory(@Qualifier("tiberoegisDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/tiberoegis/**/*.xml"));
        return sessionFactory.getObject();
    }

    @Bean(name = "tiberoegisSqlSessionTemplate")
    public SqlSessionTemplate tiberoEgisSqlSessionTemplate(@Qualifier("tiberoegisSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
