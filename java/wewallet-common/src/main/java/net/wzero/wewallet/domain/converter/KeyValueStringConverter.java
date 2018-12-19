package net.wzero.wewallet.domain.converter;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

public class KeyValueStringConverter extends JpaJsonConverter<Map<String, Object>> {

	@Override
	public Map<String, Object> convertToEntityAttribute(String dbData) {
		try {
			if(dbData==null) return null;
			return objectMapper.readValue(dbData,new TypeReference<Map<String, Object>>(){});
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
