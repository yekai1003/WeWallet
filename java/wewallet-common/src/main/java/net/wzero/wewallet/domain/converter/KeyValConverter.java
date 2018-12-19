package net.wzero.wewallet.domain.converter;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;

import net.wzero.wewallet.domain.KeyVal;

public class KeyValConverter extends JpaJsonConverter<KeyVal<String, Object>> {

	@Override
	public KeyVal<String, Object> convertToEntityAttribute(String dbData) {
		try {
			if(dbData==null) return null;
			return objectMapper.readValue(dbData,new TypeReference<KeyVal<String, Object>>(){});
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
