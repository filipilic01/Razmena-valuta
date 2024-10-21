package api.dtos;


public class CryptoConversionDto {
	private CryptoExchangeDto exchange;
	private double quantity;
	private ConversionResult conversionResult;

	public CryptoConversionDto() {

	}

	public CryptoConversionDto(CryptoExchangeDto exchange, double quantity, String currencyTo,
			double result) {
		this.exchange = exchange;
		this.quantity = quantity;
		CryptoConversionDto.ConversionResult resultHolder = this.new ConversionResult(currencyTo, result);
		this.conversionResult = resultHolder;
	}

	public CryptoExchangeDto getExchange() {
		return exchange;
	}

	public void setExchange(CryptoExchangeDto exchange) {
		this.exchange = exchange;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public ConversionResult getConversionResult() {
		return conversionResult;
	}

	public void setConversionResult(ConversionResult conversionResult) {
		this.conversionResult = conversionResult;
	}

	public class ConversionResult {
		private String currencyTo;
		private double result;

		public ConversionResult() {

		}

		public ConversionResult(String currencyTo, double result) {
			this.currencyTo = currencyTo;
			this.result = result;
		}

		public String getCurrencyTo() {
			return currencyTo;
		}

		public void setCurrencyTo(String currencyTo) {
			this.currencyTo = currencyTo;
		}

		public double getResult() {
			return result;
		}

		public void setResult(double result) {
			this.result = result;
		}
}
}
