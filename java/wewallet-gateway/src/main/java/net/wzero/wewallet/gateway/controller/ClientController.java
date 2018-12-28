package net.wzero.wewallet.gateway.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.gateway.domain.Client;
import net.wzero.wewallet.gateway.domain.ClientType;
import net.wzero.wewallet.gateway.repo.ClientRepository;
import net.wzero.wewallet.gateway.repo.ClientTypeRepository;
import net.wzero.wewallet.res.OkResponse;
import net.wzero.wewallet.res.WalletResponse;

@RestController
@RequestMapping("/client")
public class ClientController extends BaseController {
	
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private ClientTypeRepository clientTypeRepository;
	
	@RequestMapping("/list")
	List<Client> getList(){
		return clientRepository.findAll();
	}
	
	
	@RequestMapping("/getByClientTypeId")
	List<Client> getByApp(@RequestParam(name="clientTypeId") int clientTypeId){
		return clientRepository.findByClientTypeId(clientTypeId);
	}
	
	@RequestMapping("/get")
	Client get(@RequestParam(name="id") int id) {
		return this.clientRepository.findOne(id);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/create")
	Client create(
			@RequestParam(name="clientTypeId", required=false) Integer clientTypeId,
			@RequestParam(name="clientName", required=false) String clientName,
			@RequestParam(name="isPublic", required=false) Boolean isPublic,
			@RequestParam(name="isEnable", required=false) Boolean isEnable,
			@RequestParam(name="clientData", required=false) String clientData
			) {
		Client client = new Client();
		if (clientTypeId != null) {
			ClientType clientType = clientTypeRepository.findOne(clientTypeId);
			if(clientType == null) throw new WalletException("clientType_id_is_empty","客户端类型ID不存在");
			client.setClientType(clientType);
		}
		if(clientName != null) client.setClientName(clientName);
		if(isPublic != null) client.setIsPublic(isPublic);
		if(isEnable != null) client.setIsEnable(isEnable);
		if(clientData != null) {
			Gson gson = new Gson();
	        Map<String, String> map = new HashMap<String, String>();
	        map = gson.fromJson(clientData, map.getClass());
	        client.setClientData(map);
	        
		}
		client = this.clientRepository.save(client);
		return client;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/update")
	Client update(
			@RequestParam(name="id") Integer id,
			@RequestParam(name="clientTypeId", required=false) Integer clientTypeId,
			@RequestParam(name="clientName", required=false) String clientName,
			@RequestParam(name="isPublic", required=false) Boolean isPublic,
			@RequestParam(name="isEnable", required=false) Boolean isEnable,
			@RequestParam(name="clientData", required=false) String clientData
			) {
		Client client = this.clientRepository.findOne(id);
		if (client == null) throw new WalletException("client_id_is_empty","客户端ID不存在");
		if (clientTypeId != null) {
			ClientType clientType = clientTypeRepository.findOne(clientTypeId);
			if(clientType == null) throw new WalletException("clientType_id_is_empty","客户端类型ID不存在");
			client.setClientType(clientType);
		}
		if(clientName != null) client.setClientName(clientName);
		if(isPublic != null) client.setIsPublic(isPublic);
		if(isEnable != null) client.setIsEnable(isEnable);
		if(clientData != null) {
			Gson gson = new Gson();
	        Map<String, String> map = new HashMap<String, String>();
	        map = gson.fromJson(clientData, map.getClass());
	        client.setClientData(map);
	        
		}
		return this.clientRepository.save(client);
	}
	
	@RequestMapping("/delete")
	WalletResponse delete(@RequestParam(name="id") int id) {
		Client client = this.clientRepository.findOne(id);
		if(client == null) throw new WalletException("client_id_error","客户端ID错误");
		client.setIsPublic(false);
		clientRepository.save(client);
		return new OkResponse();
	}
}
