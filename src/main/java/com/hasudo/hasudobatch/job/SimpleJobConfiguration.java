package com.hasudo.hasudobatch.job;

import com.hasudo.hasudobatch.mapper.postgresql.PostgresqlMapper;
import com.hasudo.hasudobatch.mapper.tiberoegis.TiberoegisMapper;
import com.hasudo.hasudobatch.model.Omms;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {

    @Autowired
    @Lazy
    private JobLauncher jobLauncher;

    @Autowired
    @Lazy
    private Job exampleJob;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PlatformTransactionManager transactionManager;

//    private final InsertMapper insertMapper;
    @Autowired
    @Qualifier("postgresqlSqlSessionTemplate")
    private SqlSessionTemplate postgresqlSqlSessionTemplate;

    private PostgresqlMapper postgresqlMapper;

    @Autowired
    @Qualifier("tiberoegisSqlSessionTemplate")
    private SqlSessionTemplate tiberoegisSqlSessionTemplate;

    private TiberoegisMapper tiberoegisMapper;

    @Bean
    public Job exampleJob(Step exampleStep) {
        return jobBuilderFactory.get("exampleJob")
                .start(exampleStep)
                .build();
    }

    @Bean
    public Step exampleStep() {
        return stepBuilderFactory.get("exampleStep")
                .tasklet(printTimeTasklet())
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public ItemReader<String> exampleItemReader() {
        return new ItemReader<String>() {
            @Override
            public String read() throws Exception {
                return "data";
            }
        };
    }

    @Bean
    public ItemProcessor<String, String> exampleItemProcessor() {
        return item -> {
            return item;
        };
    }

    @Bean
    public Tasklet printTimeTasklet() {
        return (contribution, chunkContext) -> {
//            System.out.println("Current time: " + LocalDateTime.now());
//            insertMapper.testInsert("id", "test_message", LocalDateTime.now());
            postgresqlMapper = postgresqlSqlSessionTemplate.getMapper(PostgresqlMapper.class);
            postgresqlMapper.testInsert("id", "test_message", LocalDateTime.now());

            tiberoegisMapper = tiberoegisSqlSessionTemplate.getMapper(TiberoegisMapper.class);
            List<Omms> ommsList = tiberoegisMapper.selectOmms();
            System.out.println(ommsList);
            return RepeatStatus.FINISHED;
        };
    }

    @Scheduled(fixedRate = 60000) // 10초마다 실행
    public void performJob() {
        try {
            System.out.println("Executing job at: " + LocalDateTime.now());
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 유니크한 파라미터 추가
                    .toJobParameters();
            jobLauncher.run(exampleJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
