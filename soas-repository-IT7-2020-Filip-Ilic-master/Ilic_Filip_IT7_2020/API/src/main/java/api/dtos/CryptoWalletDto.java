package api.dtos;

public class CryptoWalletDto {
	
	private double eth;

	private double bnb;
	
	private double btc;
	
	
	
	private String email;
	
	public CryptoWalletDto() {
		
	}

	
	public CryptoWalletDto(double eth, double bnb, double btc, String email) {
		super();
		this.eth = eth;
		this.bnb = bnb;
		this.btc = btc;
		this.email = email;
	}


	public double getEth() {
		return eth;
	}

	public void setEth(double eth) {
		this.eth = eth;
	}

	public double getBnb() {
		return bnb;
	}

	public void setBnb(double bnb) {
		this.bnb = bnb;
	}

	public double getBtc() {
		return btc;
	}

	public void setBtc(double btc) {
		this.btc = btc;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
