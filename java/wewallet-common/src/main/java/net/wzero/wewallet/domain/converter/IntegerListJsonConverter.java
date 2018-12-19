package net.wzero.wewallet.domain.converter;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

public class IntegerListJsonConverter extends JpaJsonConverter<List<Integer>> {

	@Override
	public List<Integer> convertToEntityAttribute(String dbData) {
		try {
			if(dbData==null) return null;
			return objectMapper.readValue(dbData,new TypeReference<List<Integer>>(){});
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
