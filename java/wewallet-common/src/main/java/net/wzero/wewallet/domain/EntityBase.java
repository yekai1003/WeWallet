package net.wzero.wewallet.domain;

import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value=Include.NON_NULL)
@JsonIgnoreProperties(value= {"handler","hibernateLazyInitializer"})
@MappedSuperclass
public class EntityBase {
	public EntityBase() {}
}
