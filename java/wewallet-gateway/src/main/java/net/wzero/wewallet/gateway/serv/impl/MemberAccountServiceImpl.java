package net.wzero.wewallet.gateway.serv.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.MemberAccount;
import net.wzero.wewallet.gateway.repo.MemberAccountRepository;
import net.wzero.wewallet.gateway.serv.MemberAccountService;

@Service("memberAccountService")
public class MemberAccountServiceImpl implements MemberAccountService {
	
	@Autowired
	private MemberAccountRepository memberAccountRepository;

	@Override
	public MemberAccount createMemberAccount(Member member, String loginName, String loginPwd) {
		MemberAccount ma = new MemberAccount();
		ma.setMember(member);
		ma.setLoginName(loginName);
		ma.setLoginPwd(DigestUtils.md5Hex(loginPwd).toUpperCase());
		return this.memberAccountRepository.save(ma);
	}
	
	@Override
	public MemberAccount findByLogin(String username, String password) {
		return this.memberAccountRepository.findByLogin(username, DigestUtils.md5Hex(password).toUpperCase());
	}
	
	@Override
	public MemberAccount findByLoginName(String loginName) {
		return this.memberAccountRepository.findByLoginName(loginName);
	}
	
	@Override
	public MemberAccount updateMemberAccount(Member member, String loginName, String loginPwd) {
		if(loginName != null) {
			MemberAccount tmpMa = this.findByLoginName(loginName);
			if(tmpMa != null) throw new WalletException("login_name_exist","登录名存在或不能与当前登录名相同");
			MemberAccount ma = this.memberAccountRepository.findByMemberId(member.getId());
			ma.setLoginName(loginName);
			
			if(loginPwd != null)
				ma.setLoginPwd(DigestUtils.md5Hex(loginPwd).toUpperCase());
			return this.memberAccountRepository.save(ma);
		}
		return null;
	}

}
