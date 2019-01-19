package net.wzero.wewallet.gateway.serv.impl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wzero.wewallet.gateway.domain.OutAccessApi;
import net.wzero.wewallet.gateway.repo.OutAccessApiRepository;
import net.wzero.wewallet.gateway.serv.OutAccessApiService;

@Service("outAccessApiService")
public class OutAccessApiServiceImpl implements OutAccessApiService,InitializingBean {

	private static Logger log = LoggerFactory.getLogger(OutAccessApiServiceImpl.class);
	private static Map<String, OutAccessApi> outAccessApiTree;
//
//	@Autowired
//	private RestTemplate restTemplate;
	@Autowired
	private OutAccessApiRepository outAccessApiRepository;
	
	@Override
	public void initOutAccessApi() {
		log.info("--------init start-------");
		List<OutAccessApi> outAccessApiArr =  this.outAccessApiRepository.findAll();//this.restTemplate.getForObject("http://shrey-core/outAccessApi/list?enabled=1", OutAccessApi[].class);
		log.info("--------init end-------");
		if(outAccessApiArr != null && outAccessApiArr.size() > 0) {
			outAccessApiTree = new TreeMap<String, OutAccessApi>();
			for(OutAccessApi outAccessApi : outAccessApiArr) {
				log.info("---outAccessApi init:"+outAccessApi.getUri());
				outAccessApiTree.put(outAccessApi.getUri(), outAccessApi);
			}
		}
	}

	@Override
	public OutAccessApi getOutAccessApi(String uri) {
		if(outAccessApiTree == null) return null;
		if(!outAccessApiTree.containsKey(uri)) return null;
		return outAccessApiTree.get(uri);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initOutAccessApi();
	}

}
