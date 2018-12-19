package net.wzero.wewallet.gateway.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import net.wzero.wewallet.domain.EntityBase;
import net.wzero.wewallet.domain.converter.KeyValueStringConverter;

@Entity
public class ApiData extends EntityBase  implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5788917840779487356L;
	@Id
	@GeneratedValue
	private Integer id;
	private String uri;
	private String description;
	@Column
	@Convert(converter = KeyValueStringConverter.class)
	private Map<String,String> requirements;
	@Column(columnDefinition="TINYINT DEFAULT 0")
	private Boolean needAuthorization;
	@Column(columnDefinition="TINYINT DEFAULT 0")
	private Boolean isPublic;
	@Column(columnDefinition="TINYINT DEFAULT 0",nullable=false)
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
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, String> getRequirements() {
		return requirements;
	}
	public void setRequirements(Map<String, String> requirements) {
		this.requirements = requirements;
	}
	public Boolean getNeedAuthorization() {
		return needAuthorization;
	}
	public void setNeedAuthorization(Boolean needAuthorization) {
		this.needAuthorization = needAuthorization;
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
