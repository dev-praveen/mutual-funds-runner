package com.funds.batch.mapper;

import com.funds.batch.model.MutualFund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class MutualFundRowMapper implements RowMapper<MutualFund> {

  @Override
  public MutualFund mapRow(ResultSet rs, int rowNum) throws SQLException {

    log.debug("current processing row number " + rowNum);
    return MutualFund.builder()
        .fundId(BigInteger.valueOf(rs.getInt("id")))
        .fundSymbol(rs.getString("fund_symbol"))
        .quoteType(rs.getString("quote_type"))
        .region(rs.getString("region"))
        .fundShortName(rs.getString("fund_short_name"))
        .fundLongName(rs.getString("fund_long_name"))
        .currency(rs.getString("currency"))
        .initialInvestment(
            new BigInteger(
                (rs.getString("initial_investment") == null)
                    ? BigInteger.ZERO.toString()
                    : (rs.getString("initial_investment"))))
        .subsequentInvestment(
            new BigInteger(
                (rs.getString("subsequent_investment") == null)
                    ? BigInteger.ZERO.toString()
                    : (rs.getString("subsequent_investment"))))
        .fundCategory(rs.getString("fund_category"))
        .fundFamily(rs.getString("fund_family"))
        .exchangeCode(rs.getString("exchange_code"))
        .exchangeName(rs.getString("exchange_name"))
        .exchangeTimeZone(rs.getString("exchange_timezone"))
        .managementName(rs.getString("management_name"))
        .managementBio(rs.getString("management_bio"))
        .managementStartDate(rs.getDate("management_start_date").toLocalDate())
        .totalNetAssets(
            new BigInteger(
                (rs.getString("total_net_assets") == null)
                    ? BigInteger.ZERO.toString()
                    : (rs.getString("total_net_assets"))))
        .yearToDateReturn(
            new BigDecimal(
                (rs.getString("year_to_date_return") == null)
                    ? BigInteger.ZERO.toString()
                    : (rs.getString("year_to_date_return"))))
        .build();
  }
}
