package tradeService.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trade")
public class TradeModel implements Serializable{

private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "\"from\"", length = 3)
	private String from;
	
	@Column(name = "\"to\"", length = 3)
	private String to;
	
	private double exchangeValue;
	
	public TradeModel() {
		
	}

	public TradeModel(int id, String from, String to, double exchangeValue) {
		super();
		this.id = id;
		this.from = from;
		this.to = to;
		this.exchangeValue = exchangeValue;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public double getExchangeValue() {
		return exchangeValue;
	}

	public void setExchangeValue(double exchangeValue) {
		this.exchangeValue = exchangeValue;
	}
	
	
	
	
	
}
