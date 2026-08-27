package com.tradeprocessor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tradeprocessor.entity.TradeEntity;

public interface TradeRepository extends JpaRepository<TradeEntity, String>{

}
