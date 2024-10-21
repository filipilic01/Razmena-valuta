package tradeService.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import tradeService.model.TradeModel;

public interface TradeRepository extends JpaRepository<TradeModel, Integer>{

	TradeModel findByFromAndTo(String from, String to);
}
