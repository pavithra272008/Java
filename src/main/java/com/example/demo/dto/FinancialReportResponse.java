package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.Map;

public record FinancialReportResponse(
    String reportTitle,
    BigDecimal totalAssets,
    BigDecimal totalLiabilities,
    BigDecimal netWorth,
    BigDecimal totalIncome,
    BigDecimal totalExpenses,
    BigDecimal netOperatingSurplus,
    Map<String, BigDecimal> details
) {}
