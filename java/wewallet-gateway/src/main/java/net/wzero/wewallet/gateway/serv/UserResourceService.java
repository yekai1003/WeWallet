package net.wzero.wewallet.gateway.serv;

import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.UserGroup;
import net.wzero.wewallet.gateway.domain.UserResource;

public interface UserResourceService {

	UserResource createUserResource(UserGroup userGroup, Member member);
	
	UserResource updateUserResource(UserGroup userGroup, Member member);
	
}
