package net.wzero.wewallet.gateway.serv.impl;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import net.wzero.wewallet.domain.SessionData;
import net.wzero.wewallet.gateway.serv.MemberSessionService;
import net.wzero.wewallet.serv.SessionService;

@Service
public class MemberSessionServiceImpl implements MemberSessionService {
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger(MemberSessionServiceImpl.class);
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private SessionService sessionService;

	@Override
	public void saveMemberSession(Integer memberId, String token) {
		this.redisTemplate.opsForValue().set(memberId.toString(), token, 3600l, TimeUnit.SECONDS);
		logger.info("保存{}号member的token: {}", memberId, token);
	}

	@Override
	public void deleteMemberSession(Integer memberId) {
		String token = getMemberSession(memberId);
		if(token != null) {
			SessionData sessionData = this.sessionService.find(token);
			if(sessionData != null) {
				this.sessionService.delete(sessionData);
				logger.info("删除{}号member的token: {}", memberId, token);
				this.redisTemplate.delete(memberId.toString());
			}
		}
	}
	
	@Override
	public void updateMemberSession(Integer memberId, String token) {
		deleteMemberSession(memberId);
		saveMemberSession(memberId, token);
	}
	
	@Override
	public String getMemberSession(Integer memberId) {
		return this.redisTemplate.opsForValue().get(memberId.toString());
	}
	
}
