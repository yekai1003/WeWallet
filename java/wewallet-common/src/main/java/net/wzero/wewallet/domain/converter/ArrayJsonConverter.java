package net.wzero.wewallet.domain.converter;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;

public class ArrayJsonConverter extends JpaJsonConverter<Object[]> {

	@Override
	public Object[] convertToEntityAttribute(String dbData) {
		try {
			if(dbData==null) return null;
			return objectMapper.readValue(dbData,new TypeReference<Object[]>(){});
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
