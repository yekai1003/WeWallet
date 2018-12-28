package net.wzero.wewallet.gateway.serv.impl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.gateway.domain.ApiData;
import net.wzero.wewallet.gateway.repo.ApiDataRepository;
import net.wzero.wewallet.gateway.serv.ApiService;

@Slf4j
@Service("apiService")
public class ApiServiceImpl implements ApiService,InitializingBean {
	
	private static Map<String,ApiData> apiTree;

//	@Autowired
//	private RestTemplate restTemplate;
	@Autowired
	private ApiDataRepository apiDataRepository;

	@Override
	public void afterPropertiesSet() throws Exception {

		List<ApiData> apis = this.apiDataRepository.findAll();  //this.restTemplate.getForObject("http://shrey-core/api/getAll", ApiData[].class);
		if(apis != null && apis.size()>0) {
			apiTree = new TreeMap<String,ApiData>();
			for(ApiData ad : apis) {
				log.info("---api init:"+ad.getUri());
				apiTree.put(ad.getUri(), ad);
			}
		}
	}
	@Override
	public void initApi() {
		List<ApiData> apis = this.apiDataRepository.findAll();  //this.restTemplate.getForObject("http://shrey-core/api/getAll", ApiData[].class);
		if(apis != null && apis.size()>0) {
			apiTree = new TreeMap<String,ApiData>();
			for(ApiData ad : apis) {
				log.info("---api init:"+ad.getUri());
				apiTree.put(ad.getUri(), ad);
			}
		}
	}

	@Override
	public ApiData getApi(String apiuri) {
		if(apiTree == null)
			this.initApi();
		if(!apiTree.containsKey(apiuri))
			return null;
		return apiTree.get(apiuri);
	}


}
