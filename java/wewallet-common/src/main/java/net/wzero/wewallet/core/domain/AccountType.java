package net.wzero.wewallet.core.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.wzero.wewallet.domain.EntityBase;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountType extends EntityBase {
	
	@Id
	private Integer id;
	
	private String name;
	private String code;
	private String rootPath;

}
