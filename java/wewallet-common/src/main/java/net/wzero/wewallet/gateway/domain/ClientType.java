package net.wzero.wewallet.gateway.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import net.wzero.wewallet.domain.EntityBase;

@Entity
public class ClientType extends EntityBase {

	@Id
	@GeneratedValue
	private Integer id;
	private String code;
	private String name;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
