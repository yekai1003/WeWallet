package net.wzero.wewallet.domain;

public class KeyVal<K, V>{

    private K key;
    private V value;
    public KeyVal() {}
    
    public KeyVal(K key, V value)
    {
        this.key = key;
        this.value = value;
    }
	public K getKey() {
		// TODO Auto-generated method stub
		return this.key;
	}

	public V getValue() {
		// TODO Auto-generated method stub
		return this.value;
	}

	public V setValue(V value) {
		// TODO Auto-generated method stub
		return this.value=value;
	}
	public void setKey(K key) {
		this.key = key;
	}

}
