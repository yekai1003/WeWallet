package net.wzero.wewallet.gateway.serv.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wzero.wewallet.gateway.domain.UserGroup;
import net.wzero.wewallet.gateway.repo.UserGroupRepository;
import net.wzero.wewallet.gateway.serv.UserGroupService;
import net.wzero.wewallet.utils.AppConstants;

@Service("userGroupService")
public class UserGroupServiceImpl implements UserGroupService {
	
	@Autowired
	private UserGroupRepository userGroupRepository;

	@Override
	public UserGroup getNormalUserGroup() {
		return this.userGroupRepository.findOne(AppConstants.NORMAL_USER_GROUP_ID);
	}
	
	@Override
	public UserGroup findByGroupId(Integer groupId) {
		return this.userGroupRepository.findOne(groupId);
	}

}
