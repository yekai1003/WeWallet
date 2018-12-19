package net.wzero.wewallet.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value=Include.NON_NULL)
public class DomainBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6532633079505315744L;

}
