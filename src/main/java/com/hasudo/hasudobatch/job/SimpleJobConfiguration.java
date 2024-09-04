package com.hasudo.hasudobatch.job;

import com.hasudo.hasudobatch.mapper.postgresql.PostgresqlMapper;
import com.hasudo.hasudobatch.mapper.tiberoegis.TiberoegisMapper;
import com.hasudo.hasudobatch.model.Omms;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@EnableScheduling
@EnableBatchProcessing
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    @Lazy
    private Job exampleJob;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    @Qualifier("postgresqlSqlSessionTemplate")
    private SqlSessionTemplate postgresqlSqlSessionTemplate;

    @Qualifier("postgresqlSqlSessionTemplate")
    private PostgresqlMapper postgresqlMapper;

    @Autowired
    @Qualifier("tiberoegisSqlSessionTemplate")
    private SqlSessionTemplate tiberoegisSqlSessionTemplate;

    @Qualifier("tiberoegisSqlSessionTemplate")
    private TiberoegisMapper tiberoegisMapper;

    @Bean
    public Job exampleJob(Step exampleStep) {
        return jobBuilderFactory.get("exampleJob")
                .incrementer(new RunIdIncrementer())
                .start(exampleStep)
                .on("FAILED")
                .stopAndRestart(exampleStep)
                .on("*")
                .end()
                .end()
                .build();
    }

    @Bean
    public Step exampleStep() {
        System.out.println("==============step==============");
        return stepBuilderFactory.get("exampleStep")
                .<Omms, Omms>chunk(1000) // Set chunk size to 1 (processed item by item)
                .reader(ommsItemReader())
                .processor(ommsItemProcessor())
                .writer(ommsItemWriter())
                .transactionManager(transactionManager)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        System.out.println("============== Step before execution ==============");
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        System.out.println("============== Step after execution ==============");
                        return ExitStatus.COMPLETED;
                    }
                })
                .build();
//                .tasklet(printTimeTasklet())
//                .transactionManager(transactionManager)
//                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Omms> ommsItemReader() {
        return new ItemReader<Omms>() {
            private List<Omms> ommsList;
            private int currentIndex = 0;

            @Override
            public Omms read() throws Exception {
                if (ommsList == null) {
                    tiberoegisMapper = tiberoegisSqlSessionTemplate.getMapper(TiberoegisMapper.class);
                    ommsList = tiberoegisMapper.selectOmms();
                    currentIndex = 0;
                    System.out.println("==========omms select==========");
                    System.out.println(ommsList.size());
                }

                if (currentIndex >= ommsList.size()) {
                    return null;  // No more items, end of data
                }

                return ommsList.get(currentIndex++);
            }
        };
    }

    @Bean
    public ItemProcessor<Omms, Omms> ommsItemProcessor() {
        return item -> {
            // Process items if needed
            return item;
        };
    }

    @Bean
    public ItemWriter<Omms> ommsItemWriter() {
        return items -> {
            log.info("Inserted {} items", items.size());
            System.out.println("==========omms insert==========");
            postgresqlMapper = postgresqlSqlSessionTemplate.getMapper(PostgresqlMapper.class);
            postgresqlMapper.insertOmmsList(new ArrayList<>(items));
        };
    }

    @Bean
    public Tasklet printTimeTasklet() {
        return (contribution, chunkContext) -> {
            postgresqlMapper = postgresqlSqlSessionTemplate.getMapper(PostgresqlMapper.class);
            tiberoegisMapper = tiberoegisSqlSessionTemplate.getMapper(TiberoegisMapper.class);
            List<Omms> ommsList = tiberoegisMapper.selectOmms();
            System.out.println(ommsList.size());
            return RepeatStatus.FINISHED;
        };
    }

    @Scheduled(fixedDelay = 60000)
    public void performJob() {
        try {
            System.out.println("Executing job at: " + LocalDateTime.now());
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("run_date", new Date())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(exampleJob, jobParameters);
            System.out.println("Job Status: " + jobExecution.getStatus());

            if (!jobExecution.getAllFailureExceptions().isEmpty()) {
                System.out.println("Job encountered exceptions:");
                jobExecution.getAllFailureExceptions().forEach(e -> System.out.println("Exception: " + e.getMessage()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
