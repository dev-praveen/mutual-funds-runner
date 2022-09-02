package com.funds.batch.config;

import com.funds.batch.model.MutualFund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class MutualFundItemProcessor implements ItemProcessor<MutualFund, MutualFund> {

  @Override
  public MutualFund process(MutualFund mutualFund) throws InterruptedException {
    return MutualFund.builder()
        .fundSymbol(mutualFund.getFundSymbol())
        .quoteType(mutualFund.getQuoteType())
        .region(mutualFund.getRegion())
        .fundLongName(mutualFund.getFundLongName())
        .currency(mutualFund.getCurrency())
        .initialInvestment(mutualFund.getInitialInvestment())
        .subsequentInvestment(mutualFund.getSubsequentInvestment())
        .fundCategory(mutualFund.getFundCategory())
        .fundFamily(mutualFund.getFundFamily())
        .exchangeCode(mutualFund.getExchangeCode())
        .exchangeName(mutualFund.getExchangeName())
        .exchangeTimeZone(mutualFund.getExchangeTimeZone())
        .managementName(mutualFund.getManagementName())
        .managementBio(mutualFund.getManagementBio())
        .managementStartDate(mutualFund.getManagementStartDate())
        .totalNetAssets(mutualFund.getTotalNetAssets())
        .yearToDateReturn(mutualFund.getYearToDateReturn())
        .build();
  }
}
