package net.wzero.wewallet.domain.converter;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

public class StringListJsonConverter extends JpaJsonConverter<List<String>> {

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		try {
			if(dbData==null) return null;
			return objectMapper.readValue(dbData,new TypeReference<List<String>>(){});
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
