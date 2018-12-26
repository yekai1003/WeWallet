package net.wzero.wewallet.serv;

public interface ThreadLocalService {
	void set(Object newValue);
	Object get();
	void remove();
}
