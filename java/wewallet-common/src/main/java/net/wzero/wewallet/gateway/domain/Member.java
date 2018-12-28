package net.wzero.wewallet.gateway.domain;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.wzero.wewallet.domain.EntityBase;
import net.wzero.wewallet.domain.converter.KeyValueStringConverter;

@Entity
@Table(name="members")
public class Member extends EntityBase {
	@Id
	@GeneratedValue
	private Integer id;
	
	private String nickname;

	/**
	 * 账户联系电话
	 */
	@Column
	private String phone;

	@Column
	private String email;
	
	@Column
	private Boolean enable;

	@Column
	private String lastLoginIp;
	
	@Column
	private Date lastLoginTime;

	@Column
	private String mark;

	@OneToOne(mappedBy="member")
	private UserResource userResource;

	@JoinColumn(name="group_id")
	@ManyToOne(fetch=FetchType.LAZY)
	private UserGroup group;
	
	@Column
	@Convert(converter = KeyValueStringConverter.class)
	private Map<String, String> mData;

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
	public UserGroup getGroup() {
		return group;
	}
	public void setGroup(UserGroup group) {
		this.group = group;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Boolean getEnable() {
		return enable;
	}
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public Map<String, String> getmData() {
		return mData;
	}
	public void setmData(Map<String, String> mData) {
		this.mData = mData;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public UserResource getUserResource() {
		return userResource;
	}
	public void setUserResource(UserResource userResource) {
		this.userResource = userResource;
	}
	
}
