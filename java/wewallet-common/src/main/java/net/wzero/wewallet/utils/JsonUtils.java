package net.wzero.wewallet.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonUtils {
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger(JsonUtils.class);  
	  
    private static ObjectMapper objectMapper = new ObjectMapper();  
  
    /** 
     * 将对象序列化为JSON字符串 
     *  
     * @param object 
     * @return JSON字符串 
     */  
    public static String serialize(Object object) {  
        Writer write = new StringWriter();  
        try {  
            objectMapper.writeValue(write, object);  
        } catch (JsonGenerationException e) {  
            logger.error("JsonGenerationException when serialize object to json", e);  
        } catch (JsonMappingException e) {  
            logger.error("JsonMappingException when serialize object to json", e);  
        } catch (IOException e) {  
            logger.error("IOException when serialize object to json", e);  
        }  
        return write.toString();  
    }  
  
    /** 
     * 将JSON字符串反序列化为对象 
     *  
     * @param object 
     * @return JSON字符串 
     */  
    @SuppressWarnings("unchecked")
	public static <T> T deserialize(String json, Class<T> clazz) {  
        Object object = null;  
        try {  
            object = objectMapper.readValue(json, TypeFactory.rawClass(clazz));  
        } catch (JsonParseException e) {  
            logger.error("JsonParseException when serialize object to json", e);  
        } catch (JsonMappingException e) {  
            logger.error("JsonMappingException when serialize object to json", e);  
        } catch (IOException e) {  
            logger.error("IOException when serialize object to json", e);  
        }  
        return (T) object;  
    }  
  
    /** 
     * 将JSON字符串反序列化为对象 
     *  
     * @param object 
     * @return JSON字符串 
     */  
    @SuppressWarnings("unchecked")
	public static <T> T deserialize(String json, TypeReference<T> typeRef) {  
        try {  
            return (T) objectMapper.readValue(json, typeRef);  
        } catch (JsonParseException e) {  
            logger.error("JsonParseException when deserialize json", e);  
        } catch (JsonMappingException e) {  
            logger.error("JsonMappingException when deserialize json", e);  
        } catch (IOException e) {  
            logger.error("IOException when deserialize json", e);  
        }  
        return null;  
    }  
    
    /**
     * 将JSON字符串反序列化为复杂Map对象 
     * @param object
     * @return Map
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> json2Map(String json) {
    	LinkedMap map = new LinkedMap();
		JSONObject js = JSONObject.fromObject(json);
		populate(js, map);
		return map;
	}
    
    @SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private static Map populate(JSONObject jsonObject, Map map) {
		for (Iterator iterator = jsonObject.entrySet().iterator(); iterator.hasNext();) {
			String entryStr = String.valueOf(iterator.next());
			String key = entryStr.substring(0, entryStr.indexOf("="));
			String value = entryStr.substring(entryStr.indexOf("=") + 1, entryStr.length());
			if (jsonObject.get(key).getClass().equals(JSONObject.class)) {
				HashMap _map = new HashMap();
				map.put(key, _map);
				populate(jsonObject.getJSONObject(key), ((Map) (_map)));
			} else if (jsonObject.get(key).getClass().equals(JSONArray.class)) {
				ArrayList list = new ArrayList();
				map.put(key, list);
				populateArray(jsonObject.getJSONArray(key), list);
			} else {
				map.put(key, jsonObject.get(key));
			}
		}
		return map;
	}
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private static void populateArray(JSONArray jsonArray, List list) {
		for (int i = 0; i < jsonArray.size(); i++) {
			if (jsonArray.get(i).getClass().equals(JSONArray.class)) {
				ArrayList _list = new ArrayList();
				list.add(_list);
				populateArray(jsonArray.getJSONArray(i), _list);
			} else if (jsonArray.get(i).getClass().equals(JSONObject.class)) {
				HashMap _map = new HashMap();
				list.add(_map);
				populate(jsonArray.getJSONObject(i), _map);
			} else {
				list.add(jsonArray.get(i));
			}
		}
	}
}
