package com.tradeprocessor.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.tradeprocessor.domain.TradeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "trades")
public class TradeEntity {
  @Id
  @EqualsAndHashCode.Include
  @Column(nullable = false, updatable = false)
  private String tradeId;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal quantity;
  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal priceAmount;
  private String priceCurrency;
  @Enumerated(EnumType.STRING)
  private String instrumentType;
  private LocalDateTime tradeTime;

  @Enumerated(EnumType.STRING)
  private TradeStatus tradeStatus;
  @Version
  private Long version;

  protected TradeEntity() {} // required no-arg constructor for JPA

  public TradeEntity(String tradeId, BigDecimal quantity, BigDecimal priceAmount,
      String priceCurrency, String instrumentType, LocalDateTime tradeTime,
      TradeStatus tradeStatus) {
    this.tradeId = tradeId;
    this.quantity = quantity;
    this.priceAmount = priceAmount;
    this.priceCurrency = priceCurrency;
    this.instrumentType = instrumentType;
    this.tradeTime = tradeTime;
    this.tradeStatus = tradeStatus;
  }
}
