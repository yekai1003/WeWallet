package net.wzero.wewallet.gateway.domain;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.wzero.wewallet.domain.EntityBase;
import net.wzero.wewallet.domain.converter.KeyValueStringConverter;

@Entity
@Table(name="clients")
public class Client extends EntityBase {
	@Id
	@GeneratedValue
	private Integer id;
	@ManyToOne
	private ClientType clientType;
	@Column
	private String clientName;
	@Column
	@Convert(converter=KeyValueStringConverter.class)
	private Map<String,String> clientData;

	@Column(columnDefinition="TINYINT(1) DEFAULT 1")
	private Boolean isPublic;

	@Column(columnDefinition="TINYINT(1) DEFAULT 1")
	private Boolean isEnable;
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
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public Map<String, String> getClientData() {
		return clientData;
	}
	public void setClientData(Map<String, String> clientData) {
		this.clientData = clientData;
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
	public ClientType getClientType() {
		return clientType;
	}
	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}
	public Boolean getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}
	public Boolean getIsEnable() {
		return isEnable;
	}
	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}
}
