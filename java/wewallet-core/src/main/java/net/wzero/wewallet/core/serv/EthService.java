package net.wzero.wewallet.core.serv;

import net.wzero.wewallet.core.domain.Transaction;

public interface EthService {

	Transaction sendTransaction(Transaction transaction,String pwd);
}
