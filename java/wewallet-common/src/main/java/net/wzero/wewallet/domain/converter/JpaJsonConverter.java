package net.wzero.wewallet.domain.converter;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JpaJsonConverter<T> implements AttributeConverter<T, String> {

	
	protected final static ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(T meta) {
		try {
			if(meta == null) return null;
			return objectMapper.writeValueAsString(meta);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
			return null;
			// or throw an error
		}
	}

}
