package net.wzero.wewallet.core.serv;

import net.wzero.wewallet.core.domain.Account;
import net.wzero.wewallet.core.domain.EthereumAccount;
import net.wzero.wewallet.core.domain.Token;
import net.wzero.wewallet.core.domain.Transaction;

public interface EthService {

	Transaction sendTransaction(Transaction transaction,String pwd);
	
	Transaction sendTokenTransaction(Transaction transaction,String pwd);
	
	Transaction getTransactionReceipt(Transaction transaction);
	
	Account refreshEthBalance(EthereumAccount account,String env);
	
	Token refreshTokenBalance(Token token);
}
