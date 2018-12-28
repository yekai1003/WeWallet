package net.wzero.wewallet.gateway.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.UserResource;
import net.wzero.wewallet.gateway.repo.MemberRepository;
import net.wzero.wewallet.gateway.repo.UserResourceRepository;
import net.wzero.wewallet.res.ErrorResponse;
import net.wzero.wewallet.res.OkResponse;
import net.wzero.wewallet.res.WalletResponse;
import net.wzero.wewallet.utils.JsonUtils;

@Slf4j
@RestController
@RequestMapping("/power")
public class PowerController extends BaseController {
		
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private UserResourceRepository userResourceRepository;
	
	/**
	 * 1.同步锁对象不能是值类型，所以参数是int值类型时需要转换为String类型
	 * 2.int值类型转换成String类型后会生成两个对象：堆内存一个、栈内存一个，所以会导致每个对象都不一样
	 * 3.为使同步锁生效，参数类型需要为Integer类型或者将int基本类型转为Integer包装类型
	 */
	@RequestMapping("/setUserApi")
	public WalletResponse setUserApi(
			@RequestParam(name="userId") Integer userId,
			@RequestParam(name="apiId") Integer apiId,
			@RequestParam(name="type") Integer type) {
		synchronized(userId) {
			log.info(apiId+"："+"用户id为："+userId);
			Member member = this.memberRepository.findOne(userId);
			if(member == null) throw new WalletException("user_not_exist","用户不存在");
			if(member.getUserResource() == null) {
				UserResource ur = new UserResource();
				ur.setId(userId);
				ur.setApis(new ArrayList<Integer>());
				ur.setClients(new ArrayList<Integer>());
				ur = this.userResourceRepository.save(ur);
				member.setUserResource(ur);
			}
			
			log.info(apiId+"："+"APIid为："+apiId);
			Integer id = null;
			for (Integer i:member.getUserResource().getApis()) {
				if(i.equals(apiId)) {
					id = i;
					break;
				}
			}
			log.info(apiId+"："+"当前要操作的api为："+id);
			if(type == 0) { // 减少
				if(id == null) return new ErrorResponse("power_not_exist","用户无此权限");
				member.getUserResource().getApis().remove(id);
				UserResource userResource = this.userResourceRepository.save(member.getUserResource());
				log.info(apiId+"："+"删除api"+id+"成功");
				log.info(apiId+"："+"用户资源为："+JsonUtils.serialize(userResource));
				return new OkResponse();
			}else if(type == 1) { // 增加
				if(id != null) return new ErrorResponse("power_exist","用户已拥有此权限");
				member.getUserResource().getApis().add(apiId);
				UserResource userResource = this.userResourceRepository.save(member.getUserResource());
				log.info(apiId+"："+"添加api"+apiId+"成功");
				log.info(apiId+"："+"用户资源为："+JsonUtils.serialize(userResource));
				return new OkResponse();
			}else
				return new ErrorResponse("op_type_error","操作类型参数错误");
		}
	}
	
	@RequestMapping("/setUserClient")
	public WalletResponse setUserClient(
			@RequestParam(name="userId") Integer userId,
			@RequestParam(name="cId") Integer clientId,
			@RequestParam(name="type") Integer type) {
		synchronized(userId) {
			Member user = this.memberRepository.findOne(userId);
			if(user == null)
				throw new WalletException("user_not_exist","用户不存在");
			if(user.getUserResource() == null) {
				UserResource ur = new UserResource();
				ur.setId(userId);
				ur.setApis(new ArrayList<Integer>());
				ur.setClients(new ArrayList<Integer>());
				ur = this.userResourceRepository.save(ur);
				user.setUserResource(ur);
			}
	
			Integer id = null;
			for(Integer i:user.getUserResource().getClients()) {
				if(i.equals(clientId)) {
					id = i;break;
				}
			}
			if(type == 0) {//减少
				if(id==null) 
					return new ErrorResponse("power_not_exist","用户无此权限");
				user.getUserResource().getClients().remove(id);
				this.userResourceRepository.save(user.getUserResource());
				return new OkResponse();
			}else if(type==1) {//增加
				if(id!=null)
					return new ErrorResponse("power_exist","用户已拥有此权限");
				user.getUserResource().getClients().add(clientId);
				this.userResourceRepository.save(user.getUserResource());
				return new OkResponse();
			}else
				return new ErrorResponse("op_type_error","操作类型参数错误");
		}
	}
}
