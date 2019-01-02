package net.wzero.wewallet.gateway.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.gateway.domain.UserGroup;
import net.wzero.wewallet.gateway.repo.ApiDataRepository;
import net.wzero.wewallet.gateway.repo.ClientRepository;
import net.wzero.wewallet.gateway.repo.UserGroupRepository;
import net.wzero.wewallet.res.ErrorResponse;
import net.wzero.wewallet.res.OkResponse;
import net.wzero.wewallet.res.WalletResponse;

@Slf4j
@RestController
@RequestMapping("/usergroup")
public class UserGroupController extends BaseController {
	
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private ApiDataRepository apiDataRepository;
	@Autowired
	private ClientRepository clientRepository;
	
	@RequestMapping("/getAll")
	List<UserGroup> getAll(){
		return this.userGroupRepository.findAll();
	}
	
	@RequestMapping("/get")
	UserGroup get(@RequestParam(name="id")Integer id){
		return this.userGroupRepository.getOne(id);
	}

	@RequestMapping("/create")
	UserGroup create(@ModelAttribute UserGroup userGroup) {
		return this.userGroupRepository.save(userGroup);
	}
	
	@RequestMapping("/update")
	UserGroup update(@ModelAttribute UserGroup userGroup) {
		UserGroup old = this.userGroupRepository.findOne(userGroup.getId());
		if(old == null) throw new WalletException("group_not_exist","不存在");
		if(userGroup.getGroupName()!=null)
			old.setGroupName(userGroup.getGroupName());
		if(userGroup.getDescription()!=null)
			old.setDescription(userGroup.getDescription());
		return this.userGroupRepository.save(old);
	}
	
	@RequestMapping("/delete")
	WalletResponse delete(@RequestParam(name="groupId") int groupId) {
		this.userGroupRepository.delete(groupId);
		return new OkResponse();
	}
	
	@RequestMapping("/setUserApi")
	public WalletResponse setUserApi(
			@RequestParam(name="groupId") Integer groupId,
			@RequestParam(name="apiId") Integer apiId,
			@RequestParam(name="type") Integer type
			) {
		synchronized(groupId) {
			UserGroup userGroup = this.userGroupRepository.findOne(groupId);
			if(userGroup == null) throw new WalletException("user_not_exist","用户组不存在");
			if(userGroup.getApis() == null) userGroup.setApis(new ArrayList<Integer>());
			
			Integer id = null;
			for (Integer integer : userGroup.getApis()) {
				if (integer.equals(apiId)) {
					id = integer;
					break;
				}
			}
			
			if(type == 0) { // 减少
				if(id == null) return new ErrorResponse("power_not_exist","用户无此权限");
				userGroup.getApis().remove(id);
				this.userGroupRepository.save(userGroup);
				return new OkResponse();
			}else if(type == 1) { // 增加
				if(id != null) return new ErrorResponse("power_exist","用户已拥有此权限");
				if (apiDataRepository.findOne(apiId) == null) throw new WalletException("api_not_exist","api不存在");
				userGroup.getApis().add(apiId);
				this.userGroupRepository.save(userGroup);
				log.info("---groupid:"+groupId+",apiId:"+apiId+",type:"+type);
				return new OkResponse();
			}else
				return new ErrorResponse("op_type_error","操作类型参数错误");
		}
	}
	
	@RequestMapping("/setUserClient")
	public WalletResponse setUserClient(
			@RequestParam(name="groupId") Integer groupId,
			@RequestParam(name="clientId") Integer clientId,
			@RequestParam(name="type") Integer type) {
		synchronized(groupId) {
			UserGroup userGroup = this.userGroupRepository.findOne(groupId);
			if(userGroup == null) throw new WalletException("user_not_exist","用户组不存在");
			if(userGroup.getClients() == null) userGroup.setClients(new ArrayList<Integer>());
			
			Integer id = null;
			for (Integer integer : userGroup.getClients()) {
				if (integer.equals(clientId)) {
					id = integer;
					break;
				}
			}
			
			if(type == 0) { // 减少
				if(id == null) return new ErrorResponse("power_not_exist","用户无此权限");
				userGroup.getClients().remove(id);
				this.userGroupRepository.save(userGroup);
				log.info("---groupid:"+groupId+",clientId:"+clientId+",type:"+type);
				return new OkResponse();
			}else if(type == 1) { // 增加
				if(id != null) return new ErrorResponse("power_exist","用户已拥有此权限");
				if (clientRepository.findOne(clientId) == null) throw new WalletException("client_not_exist","client不存在");
				userGroup.getClients().add(clientId);
				this.userGroupRepository.save(userGroup);
				log.info("---groupid:"+groupId+",clientId:"+clientId+",type:"+type);
				return new OkResponse();
			}else
				return new ErrorResponse("op_type_error","操作类型参数错误");
		}
	}
	
}
