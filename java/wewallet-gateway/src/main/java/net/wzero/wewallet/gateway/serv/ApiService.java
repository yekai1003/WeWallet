package net.wzero.wewallet.gateway.serv;

import net.wzero.wewallet.gateway.domain.ApiData;

public interface ApiService {
	void initApi();
	ApiData getApi(String apiuri);
}
