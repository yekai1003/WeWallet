package net.wzero.wewallet.gateway.serv.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wzero.wewallet.gateway.domain.Client;
import net.wzero.wewallet.gateway.repo.ClientRepository;
import net.wzero.wewallet.gateway.serv.ClientService;

@Service("clientService")
public class ClientServiceImpl implements ClientService,InitializingBean {
	private static Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);
	
	private static List<Client> clientList;
//
//	@Autowired
//	private RestTemplate restTemplate;
	@Autowired
	private ClientRepository clientRepository;
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void initClient() {
		clientList = new ArrayList<Client>();
		
		List<Client> clients = this.clientRepository.findAll(); //this.restTemplate.getForObject("http://shrey-core/client/list", Client[].class);
		log.info("Client Count :\t"+clients.size());
		for(Client c : clients) {
			log.info("---client init:"+c.getClientName());
			clientList.add(c);
		}
	}

	@Override
	public Client getClient(Integer clientId) {
		if(clientList == null)
			this.initClient();
		for(Client c:clientList) {
			log.info("---client "+c.getId()+" | "+clientId);
			if(clientId.equals(c.getId())) return c;
		}
		return null;
	}


}
