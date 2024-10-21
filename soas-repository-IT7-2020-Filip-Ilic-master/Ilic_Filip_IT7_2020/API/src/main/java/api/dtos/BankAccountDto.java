package api.dtos;




public class BankAccountDto {

	
	private double rsd;

	private double gbp;
	
	private double eur;
	
	private double usd;
	private double chf;
	
	private String email;
	
	public BankAccountDto() {
		
	}

	public BankAccountDto(double rsd, double gbp, double eur,
			double usd, double chf, String email) {
		super();
		this.rsd = rsd;
		this.gbp = gbp;
		this.eur = eur;
		this.usd = usd;
		this.chf = chf;
		this.email = email;
	}

	public double getRsd() {
		return rsd;
	}

	public void setRsd(double rsd) {
		this.rsd = rsd;
	}

	public double getGbp() {
		return gbp;
	}

	public void setGbp(double gbp) {
		this.gbp = gbp;
	}

	public double getEur() {
		return eur;
	}

	public void setEur(double eur) {
		this.eur = eur;
	}

	public double getUsd() {
		return usd;
	}

	public void setUsd(double usd) {
		this.usd = usd;
	}

	public double getChf() {
		return chf;
	}

	public void setChf(double chf) {
		this.chf = chf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
