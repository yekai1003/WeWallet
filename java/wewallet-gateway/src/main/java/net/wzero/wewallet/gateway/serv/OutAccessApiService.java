package net.wzero.wewallet.gateway.serv;

import net.wzero.wewallet.gateway.domain.OutAccessApi;

public interface OutAccessApiService {
	
	void initOutAccessApi();
	
	OutAccessApi getOutAccessApi(String uri);
	
}
