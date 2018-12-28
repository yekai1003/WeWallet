package net.wzero.wewallet.gateway.serv.impl;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import net.wzero.wewallet.gateway.domain.OutAccessApi;
import net.wzero.wewallet.gateway.serv.OutAccessApiService;

@Service("outAccessApiService")
public class OutAccessApiServiceImpl implements OutAccessApiService {

	private static Logger log = LoggerFactory.getLogger(OutAccessApiServiceImpl.class);
	private static Map<String, OutAccessApi> outAccessApiTree;

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public void initOutAccessApi() {
		log.info("--------init start-------");
		OutAccessApi[] outAccessApiArr = this.restTemplate.getForObject("http://shrey-core/outAccessApi/list?enabled=1", OutAccessApi[].class);
		log.info("--------init end-------");
		if(outAccessApiArr != null && outAccessApiArr.length > 0) {
			outAccessApiTree = new TreeMap<String, OutAccessApi>();
			for(OutAccessApi outAccessApi : outAccessApiArr) {
				log.info("---outAccessApi init:"+outAccessApi.getUri());
				outAccessApiTree.put(outAccessApi.getUri(), outAccessApi);
			}
		}
	}

	@Override
	public OutAccessApi getOutAccessApi(String uri) {
		if(outAccessApiTree == null) this.initOutAccessApi();
		if(!outAccessApiTree.containsKey(uri)) return null;
		return outAccessApiTree.get(uri);
	}

}
