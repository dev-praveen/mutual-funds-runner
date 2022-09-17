package com.funds.batch.config;

import com.funds.batch.mapper.MutualFundRowMapper;
import com.funds.batch.model.MutualFund;
import com.funds.batch.model.TopMutualFund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
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

  @Value("${top10FundsQuery}")
  private String top10FundsQuery;

  @Value("${topMfInsertQuery}")
  private String topMfInsertQuery;

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
        .resource(new ClassPathResource("MutualFundsOut.csv"))
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
  public JdbcCursorItemReader<MutualFund> mfItemReader(DataSource dataSource) {

    return new JdbcCursorItemReaderBuilder<MutualFund>()
        .dataSource(dataSource)
        .name("mfTableReader")
        .sql(top10FundsQuery)
        .rowMapper(new MutualFundRowMapper())
        .build();
  }

  @Bean
  public JdbcBatchItemWriter<TopMutualFund> topFundwriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<TopMutualFund>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .sql(topMfInsertQuery)
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Job mutualfundjob(JobCompletionNotificationListener listener, Step step1, Step step2) {
    return jobBuilderFactory
        .get("mutualfundjob")
        .listener(listener)
        .flow(step1)
        .next(step2)
        .end()
        .build();
  }

  @Bean
  public Step step1(JdbcBatchItemWriter<MutualFund> writer) {
    return stepBuilderFactory
        .get("step1")
        .<MutualFund, MutualFund>chunk(10)
        .reader(reader())
        .writer(writer)
        .build();
  }

  @Bean
  public Step step2(JdbcBatchItemWriter<TopMutualFund> topFundwriter, DataSource dataSource) {
    return stepBuilderFactory
        .get("step2")
        .<MutualFund, TopMutualFund>chunk(10)
        .reader(mfItemReader(dataSource))
        .writer(topFundwriter)
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
