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
public class TopMutualFund implements Serializable {

  private BigInteger fundId;
  private String fundSymbol;
  private String fundShortName;
  private String fundLongName;
  private BigInteger initialInvestment;
  private BigInteger subsequentInvestment;
  private String fundCategory;
  private String fundFamily;
  private String managementName;
  private String managementBio;
  private LocalDate managementStartDate;
  private BigInteger totalNetAssets;
  private BigDecimal yearToDateReturn;
}
