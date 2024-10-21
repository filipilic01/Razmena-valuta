package cryptoWallet.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "crypto_wallet")
public class CryptoWalletModel implements Serializable{

	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "eth")
	private double eth;
	
	@Column(name = "bnb")
	private double bnb;
	
	@Column(name = "btc")
	private double btc;
	
	
	
	@Column(unique = true, nullable = false)
	private String email;
	
	public CryptoWalletModel() {
		
	}

	public CryptoWalletModel(int id, double eth, double bnb, double btc, String email) {
		super();
		this.id = id;
		this.eth = eth;
		this.bnb = bnb;
		this.btc = btc;
		this.email = email;
	}

	public CryptoWalletModel( double eth, double bnb, double btc, String email) {
		super();
		
		this.eth = eth;
		this.bnb = bnb;
		this.btc = btc;
		this.email = email;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
