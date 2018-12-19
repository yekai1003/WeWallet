package net.wzero.wewallet.domain.converter;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import net.wzero.wewallet.domain.KeyVal;

public class KeyValListConverter extends JpaJsonConverter<List<KeyVal<String, Object>>> {

	@Override
	public List<KeyVal<String, Object>> convertToEntityAttribute(String dbData) {
		try {
			if(dbData==null) return null;
			return objectMapper.readValue(dbData,new TypeReference<List<KeyVal<String, Object>>>(){});
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
