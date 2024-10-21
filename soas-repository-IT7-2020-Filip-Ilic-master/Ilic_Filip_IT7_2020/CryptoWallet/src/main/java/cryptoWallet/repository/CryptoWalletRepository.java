package cryptoWallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import cryptoWallet.model.CryptoWalletModel;
import jakarta.transaction.Transactional;

public interface CryptoWalletRepository extends JpaRepository<CryptoWalletModel, Integer> {
	
	CryptoWalletModel findByEmail(String email);
	@Modifying
	@Transactional
	@Query("update CryptoWalletModel b set b.eth = :eth, b.bnb = :bnb, b.btc = :btc where b.email = :email")
	void updateCryptoWallet(@Param("email") String email, @Param("eth") double eth, @Param("bnb") double bnb,
	                       @Param("btc") double btc);
}
