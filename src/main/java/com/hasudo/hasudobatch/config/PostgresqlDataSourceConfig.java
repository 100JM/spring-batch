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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.hasudo.hasudobatch.mapper.postgresql", sqlSessionFactoryRef = "postgresqlSqlSessionFactory")
public class PostgresqlDataSourceConfig {

    @Bean(name = "postgresqlDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.postgresql")
    public DataSource postgresqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "postgresqlSqlSessionFactory")
    @Primary
    public SqlSessionFactory postgresqlSqlSessionFactory(@Qualifier("postgresqlDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/postgresql/**/*.xml"));
        return sessionFactory.getObject();
    }

//    @Bean(name = "postgresqlTransactionManager")
//    @Primary
//    public DataSourceTransactionManager postgresqlTransactionManager(@Qualifier("postgresqlDataSource") DataSource dataSource) {
//        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
//        transactionManager.setGlobalRollbackOnParticipationFailure(false);
//        return transactionManager;
//    }

    @Bean(name = "postgresqlSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate postgresqlSqlSessionTemplate(@Qualifier("postgresqlSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
