package net.wzero.wewallet.serv.impl;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.domain.SessionData;
import net.wzero.wewallet.serv.SessionService;
import net.wzero.wewallet.utils.TimeUtils;

@Slf4j
public class SessionServiceImpl implements SessionService {

	@Override
	@CachePut(value="session",key="#sd.token")
	public SessionData save(SessionData sd) {
		// 创建token
		if(sd.getToken()== null)
			sd.setToken(UUID.randomUUID().toString());
		sd.setExpired(TimeUtils.after(3600));
		log.info(sd.getToken()+"->保存Session");
		return sd;
	}

	@Override
	@CacheEvict(value = "session",key="#sd.token",condition="#sd.token != null")
	public SessionData delete(SessionData sd) {
		//如果有数据库操作，在这里做
		log.info(sd.getToken()+"->删除Session");
		return sd;
	}

	@Override
	@CacheEvict(value = "session",allEntries=true)
	public void clear() {
		log.info("清空所有 session");
	}

	@Override
	@Cacheable(value = "session", key = "#token")
	public SessionData find(String token) {
		log.info("查找指定 session");
		return null;
	}

}
