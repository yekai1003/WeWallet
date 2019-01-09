
package net.wzero.wewallet.gateway.serv.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.MemberPhone;
import net.wzero.wewallet.gateway.repo.MemberPhoneRepository;
import net.wzero.wewallet.gateway.serv.MemberPhoneService;

@Service("memberPhoneService")
public class MemberPhoneServiceImpl implements MemberPhoneService {
	
	@Autowired
	private MemberPhoneRepository memberPhoneRepository;

	@Override
	public MemberPhone createMemberPhone(Member member, String phone) {
		MemberPhone memberPhone = new MemberPhone();
		memberPhone.setMember(member);
		memberPhone.setNumber(phone);
		return this.memberPhoneRepository.save(memberPhone);
	}

	@Override
	public MemberPhone findByNumber(String phone) {
		return this.memberPhoneRepository.findByNumber(phone);
	}
	
	@Override
	public MemberPhone findByMemberId(Integer memberId) {
		return this.memberPhoneRepository.findByMemberId(memberId);
	}
	
	@Override
	public MemberPhone updateMemberPhone(Member member, String phone) {
		MemberPhone memberPhone = this.findByMemberId(member.getId());
		if(memberPhone == null) {
			memberPhone = new MemberPhone();
			memberPhone.setMember(member);
		}
		memberPhone.setNumber(phone);
		return this.memberPhoneRepository.save(memberPhone);
	}

}
