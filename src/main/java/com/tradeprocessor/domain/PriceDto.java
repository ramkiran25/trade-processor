package com.tradeprocessor.domain;

import java.math.BigDecimal;

public record PriceDto(BigDecimal amount, String currency) {

}
