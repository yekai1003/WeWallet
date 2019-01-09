package net.wzero.wewallet.gateway.serv;

import net.wzero.wewallet.gateway.domain.UserGroup;

public interface UserGroupService {

	UserGroup getNormalUserGroup();
	
	UserGroup findByGroupId(Integer groupId);
}
