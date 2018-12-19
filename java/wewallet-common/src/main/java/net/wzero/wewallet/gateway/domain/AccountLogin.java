package net.wzero.wewallet.gateway.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import net.wzero.wewallet.domain.EntityBase;

@Entity
public class AccountLogin extends EntityBase {

	@Id
	@GeneratedValue
	private Integer id;
	@Column(columnDefinition="VARCHAR(32)")
	private String loginName;
	@Column(columnDefinition="VARCHAR(32)")
	private String loginPwd;
	
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
	private Date created;
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", updatable = false)
	private Date updated;
	
}
