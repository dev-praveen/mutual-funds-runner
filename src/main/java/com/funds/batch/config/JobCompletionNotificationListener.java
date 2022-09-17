package com.funds.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

  @Autowired private JdbcTemplate jdbcTemplate;

  @Override
  public void beforeJob(JobExecution jobExecution) {

    jdbcTemplate.execute("DELETE FROM MUTUAL_FUND");
    log.debug("deleted all rows in MUTUAL_FUND table");
    jdbcTemplate.execute("DELETE FROM TOP_MUTUAL_FUND");
    log.debug("deleted all rows in TOP_MUTUAL_FUND table");
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("!!! JOB FINISHED! Time to verify the results");
    }
  }
}
