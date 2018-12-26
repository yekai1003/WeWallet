package net.wzero.wewallet.serv.impl;

import net.wzero.wewallet.domain.SysParam;
import net.wzero.wewallet.serv.ThreadLocalService;

public class SysParamService implements ThreadLocalService {

	private static ThreadLocal<SysParam> threadLocal = new ThreadLocal<>();

	@Override
	public void set(Object newValue) {
		threadLocal.set((SysParam)newValue);
	}

	@Override
	public Object get() {
		// TODO Auto-generated method stub
		return threadLocal.get();
	}

	@Override
	public void remove() {
		threadLocal.remove();
	}

}
