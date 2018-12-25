package net.wzero.wewallet.core.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wzero.wewallet.domain.EntityBase;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name="cards")
@Builder
public class Card extends EntityBase {
	@Id
	@GeneratedValue
	private Integer id;
	// 账户的Id,这里是不同模块，所以不能强关联
	private Integer memberId;
	@JoinColumn(name="card_type_id")
	@ManyToOne
	private CardType cardType;
	//bip39 的路径
	private String path;
	//账户地址,方便其他业务，反正地址不可能推算出私钥
	private String addr;
	private BigDecimal amount;
	private String keystore;
	
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
	private Date created;
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", updatable = false)
	private Date updated;
	
}
