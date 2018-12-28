package net.wzero.wewallet.gateway.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import net.wzero.wewallet.domain.EntityBase;
import net.wzero.wewallet.domain.converter.IntegerListJsonConverter;

@Entity
@Table(name="user_group")
public class UserGroup extends EntityBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1132998564214536824L;
	@Id
	@GeneratedValue
	private Integer id;
	@Column
	private String groupName;
	@Column
	private String description;
	@Column(columnDefinition="TEXT")
	@Convert(converter = IntegerListJsonConverter.class)
	private List<Integer> apis;
	@Column
	@Convert(converter = IntegerListJsonConverter.class)
	private List<Integer> clients;
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
	private Date created;
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", updatable = false)
	private Date updated;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Integer> getApis() {
		return apis;
	}
	public void setApis(List<Integer> apis) {
		this.apis = apis;
	}
	public List<Integer> getClients() {
		return clients;
	}
	public void setClients(List<Integer> clients) {
		this.clients = clients;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
}
