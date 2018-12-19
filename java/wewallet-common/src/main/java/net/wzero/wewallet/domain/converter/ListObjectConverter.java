package net.wzero.wewallet.domain.converter;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
/**
 * List<Object> 的json 结构 
 * 但是支持重叠很神奇
 * @author yjjie
 *
 */
public class ListObjectConverter extends JpaJsonConverter<List<Object>> {

	@Override
	public List<Object> convertToEntityAttribute(String dbData) {
		try {
			if(dbData==null) return null;
			return objectMapper.readValue(dbData,new TypeReference<List<Object>>(){});
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
