package net.wzero.wewallet.core.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.wzero.wewallet.domain.EntityBase;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Builder
public class CardType extends EntityBase {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private String name;
	private String code;
	private String rootPath;

}
