package bankAccount.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import bankAccount.model.BankAccountModel;
import jakarta.transaction.Transactional;




public interface BankAccountRepository extends JpaRepository<BankAccountModel, Integer>{

	BankAccountModel findByEmail(String email);
	
	@Modifying
	@Transactional
	@Query("update BankAccountModel b set b.rsd = :rsd, b.gbp = :gbp, b.eur = :eur, b.usd = :usd, b.chf = :chf where b.email = :email")
	void updateBankAccount(@Param("email") String email, @Param("rsd") double rsd, @Param("gbp") double gbp,
	                       @Param("eur") double eur, @Param("usd") double usd, @Param("chf") double chf);
}
