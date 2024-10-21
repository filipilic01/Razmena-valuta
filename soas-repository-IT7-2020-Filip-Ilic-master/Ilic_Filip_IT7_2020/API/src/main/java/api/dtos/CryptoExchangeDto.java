package api.dtos;

public class CryptoExchangeDto {


	private String from;
	private String to;
	private double exchangeValue;
	private String instancePort;

	public CryptoExchangeDto() {
	}

	public CryptoExchangeDto(String from, String to, double exchangeValue) {
		this.from = from;
		this.to = to;
		this.exchangeValue = exchangeValue;
	}

	public String getInstancePort() {
		return instancePort;
	}

	public void setInstancePort(String instancePort) {
		this.instancePort = instancePort;
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
