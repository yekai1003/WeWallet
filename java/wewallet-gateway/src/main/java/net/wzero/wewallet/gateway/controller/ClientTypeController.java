package net.wzero.wewallet.gateway.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.gateway.domain.ClientType;
import net.wzero.wewallet.gateway.repo.ClientTypeRepository;
import net.wzero.wewallet.res.OkResponse;
import net.wzero.wewallet.res.WalletResponse;

@RestController
@RequestMapping("/clientType")
public class ClientTypeController extends BaseController {

	@Autowired
	private ClientTypeRepository clientTypeRepository;
	
	@RequestMapping(value="/create")
	public ClientType create(
			@RequestParam(name="code") String code,
			@RequestParam(name="name") String name
			) {
		ClientType clientType = new ClientType();
		clientType.setCode(code);
		clientType.setName(name);
		clientType = clientTypeRepository.save(clientType);
		return clientType;
	}
	
	@RequestMapping(value="/delete")
	WalletResponse delete(@RequestParam(name="id") Integer id) {
		ClientType clientType = this.clientTypeRepository.findOne(id);
		if(clientType == null) throw new WalletException("params_error","客户端类型ID不存在");
		try {
			this.clientTypeRepository.delete(id);
			return new OkResponse();
		} catch (Exception e) {
			throw new WalletException("params_error","客户端类型ID被引用，不能删除");
		}
	}
	
	@RequestMapping(value="/update")
	ClientType update(
			@RequestParam(name="id") Integer id,
			@RequestParam(name="name") String name,
			@RequestParam(name="code") String code) {
		ClientType clientType = this.clientTypeRepository.findOne(id);
		if(clientType == null) throw new WalletException("params_error","客户端类型ID不存在");
		clientType.setName(name);
		clientType.setCode(code);
		clientType = clientTypeRepository.save(clientType);
		return clientType;
	}
	
	@RequestMapping(value="/get")
	ClientType get(@RequestParam(name="id") Integer id) {
		ClientType clientType = this.clientTypeRepository.findOne(id);
		if(clientType == null) throw new WalletException("params_error","客户端类型ID不存在");
		return clientType;
	}
	
	@RequestMapping(value="/list")
	List<ClientType> list(){
		return this.clientTypeRepository.findAll();
	}
}
