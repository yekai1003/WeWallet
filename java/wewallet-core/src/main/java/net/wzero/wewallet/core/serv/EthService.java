package net.wzero.wewallet.core.serv;

import net.wzero.wewallet.core.domain.Card;
import net.wzero.wewallet.core.domain.EthereumCard;
import net.wzero.wewallet.core.domain.Token;
import net.wzero.wewallet.core.domain.Transaction;

public interface EthService {

	Transaction sendTransaction(Transaction transaction,String pwd);
	
	Transaction sendTokenTransaction(Transaction transaction,String pwd);
	
	Transaction getTransactionReceipt(Transaction transaction);
	
	Card refreshEthBalance(EthereumCard card,String env);
	
	Token refreshTokenBalance(Token token,String env);
}
