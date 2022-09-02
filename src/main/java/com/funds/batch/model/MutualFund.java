package com.funds.batch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MutualFund implements Serializable {

  private String fundSymbol;
  private String quoteType;
  private String region;
  private String fundShortName;
  private String fundLongName;
  private String currency;
  private BigInteger initialInvestment;
  private BigInteger subsequentInvestment;
  private String fundCategory;
  private String fundFamily;
  private String exchangeCode;
  private String exchangeName;
  private String exchangeTimeZone;
  private String managementName;
  private String managementBio;
  private LocalDate managementStartDate;
  private BigInteger totalNetAssets;
  private BigDecimal yearToDateReturn;
}
