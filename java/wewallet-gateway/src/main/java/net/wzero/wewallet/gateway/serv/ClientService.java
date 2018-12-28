package net.wzero.wewallet.gateway.serv;

import net.wzero.wewallet.gateway.domain.Client;

public interface ClientService {

	void initClient();
	Client getClient(Integer clientId);
}
