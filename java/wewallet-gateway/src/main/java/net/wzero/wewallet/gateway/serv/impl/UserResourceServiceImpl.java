package net.wzero.wewallet.gateway.serv.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.UserGroup;
import net.wzero.wewallet.gateway.domain.UserResource;
import net.wzero.wewallet.gateway.repo.UserResourceRepository;
import net.wzero.wewallet.gateway.serv.UserResourceService;

@Service("userResourceService")
public class UserResourceServiceImpl implements UserResourceService {

	@Autowired
	private UserResourceRepository userResourceRepository;

	@Override
	public UserResource createUserResource(UserGroup userGroup, Member member) {
		return updateUserResource(userGroup, member);
	}
	
	@Override
	public UserResource updateUserResource(UserGroup userGroup, Member member) {
		UserResource userResource = member.getUserResource();
		if(userResource == null) {
			userResource = new UserResource();
			userResource.setMember(member);
		}
		userResource.setApis(userGroup.getApis());
		userResource.setClients(userGroup.getClients());
		return userResourceRepository.save(userResource);
	}

}
