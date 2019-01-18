package net.wzero.wewallet.domain.converter;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import net.wzero.wewallet.core.domain.Balance;
import net.wzero.wewallet.domain.converter.JpaJsonConverter;

public class KeyBalanceConverter extends JpaJsonConverter<Map<String,Balance>>{

	@Override
	public Map<String, Balance> convertToEntityAttribute(String dbData) {
		try {
			if(dbData==null) return null;
			return objectMapper.readValue(dbData,new TypeReference<Map<String, Balance>>(){});
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
}