package com.funds.batch.config;

import com.funds.batch.model.MutualFund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Configuration
@EnableBatchProcessing
@PropertySource("classpath:sql/fund_queries.xml")
public class MutualFundsRunnerBatchConfiguration extends DefaultBatchConfigurer {

  @Value("${mfInsertQuery}")
  private String mfInsertQuery;

  @Value("${mf.input-file-location}")
  private String fileLocation;

  @Autowired private JobBuilderFactory jobBuilderFactory;
  @Autowired private StepBuilderFactory stepBuilderFactory;

  @Override
  public void setDataSource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  @Bean
  public ConversionService localDateConversionService() {
    final var defaultConversionService = new DefaultConversionService();
    DefaultConversionService.addDefaultConverters(defaultConversionService);
    defaultConversionService.addConverter(
        new Converter<String, LocalDate>() {
          @Override
          public LocalDate convert(String stringDate) {
            LocalDate localDate = LocalDate.now();
            try {
              localDate = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException exception) {
              log.error(
                  "Couldn't convert stringDate " + stringDate + " to localDate ",
                  exception.getMessage());
            }
            return localDate;
          }
        });

    return defaultConversionService;
  }

  @Bean
  public FlatFileItemReader<MutualFund> reader() {
    return new FlatFileItemReaderBuilder<MutualFund>()
        .name("csvFileReader")
        .resource(new FileSystemResource(fileLocation))
        .linesToSkip(1)
        .delimited()
        .names(getHeaderNames())
        .fieldSetMapper(
            new BeanWrapperFieldSetMapper<>() {
              {
                setTargetType(MutualFund.class);
                setConversionService(localDateConversionService());
              }
            })
        .build();
  }

  @Bean
  public JdbcBatchItemWriter<MutualFund> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<MutualFund>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .sql(mfInsertQuery)
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
    return jobBuilderFactory.get("mutualfundjob").listener(listener).flow(step1).end().build();
  }

  @Bean
  public Step step1(JdbcBatchItemWriter<MutualFund> writer) {
    return stepBuilderFactory
        .get("step1")
        .<MutualFund, MutualFund>chunk(10)
        .reader(reader())
        .writer(writer)
        .taskExecutor(new SimpleAsyncTaskExecutor())
        .build();
  }

  private String[] getHeaderNames() {
    return new String[] {
      "fund_symbol",
      "quote_type",
      "region",
      "fund_short_name",
      "fund_long_name",
      "currency",
      "initial_investment",
      "subsequent_investment",
      "fund_category",
      "fund_family",
      "exchange_code",
      "exchange_name",
      "exchange_timezone",
      "management_name",
      "management_bio",
      "management_start_date",
      "total_net_assets",
      "year_to_date_return"
    };
  }
}
