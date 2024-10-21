package bankAccount.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank_account")
public class BankAccountModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "rsd")
	private double rsd;
	
	@Column(name = "gbp")
	private double gbp;
	
	@Column(name = "eur")
	private double eur;
	
	@Column(name = "usd")
	private double usd;
	
	@Column(name = "chf")
	private double chf;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	public BankAccountModel() {
		
	}
	
	public BankAccountModel(int id, double rsd, double gbp, double eur,
			double usd, double chf, String email) {
		super();
		this.id = id;
		this.rsd = rsd;
		this.gbp = gbp;
		this.eur = eur;
		this.usd = usd;
		this.chf = chf;
		this.email = email;
	}

	public BankAccountModel( double rsd, double gbp, double eur,
			double usd, double chf, String email) {
		super();
		this.rsd = rsd;
		this.gbp = gbp;
		this.eur = eur;
		this.usd = usd;
		this.chf = chf;
		this.email = email;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
