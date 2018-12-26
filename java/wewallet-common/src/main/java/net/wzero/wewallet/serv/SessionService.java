package net.wzero.wewallet.serv;

import net.wzero.wewallet.domain.SessionData;

public interface SessionService {
	SessionData save(SessionData sd);
	SessionData find(String token);
	SessionData delete(SessionData sd);
	void clear();
}
