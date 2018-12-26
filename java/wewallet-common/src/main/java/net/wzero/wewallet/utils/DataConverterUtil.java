package net.wzero.wewallet.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wzero.wewallet.WalletException;

public class DataConverterUtil {
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger(DataConverterUtil.class);
	
	/**
	 * 将josn数组字符串转为Integer类的集合
	 * @param integerJson
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Integer> integerJsonToIntegerList(String integerJson, String key){
		HashMap<String, List<Integer>> IntegerMap = new HashMap<String, List<Integer>>();
		logger.info("integerJson：" + integerJson);
		IntegerMap = JsonUtils.deserialize(integerJson, IntegerMap.getClass());
		if(IntegerMap == null || IntegerMap.get(key) == null) throw new WalletException("params_error",key+"格式错误，不是json数组格式");
		return IntegerMap.get(key);
	}
	
	/**
	 * 将josn数组字符串转为List的Integer类的集合
	 * @param integerJson
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<List<Integer>> integerJsonToListIntegerList(String integerJson, String key){
		HashMap<String, List<List<Integer>>> IntegerMap = new HashMap<String, List<List<Integer>>>();
		logger.info("integerJson：" + integerJson);
		IntegerMap = JsonUtils.deserialize(integerJson, IntegerMap.getClass());
		if(IntegerMap == null || IntegerMap.get(key) == null) throw new WalletException("params_error",key+"格式错误，不是json二维数组格式");
		return IntegerMap.get(key);
	}
	
	/**
	 * 将json字符串转为map，json为null时返回空map
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(String data){
		Map<String, Object> objectMap = new HashMap<String, Object>();
		if(data != null && !"".equals(data) && !"null".equals(data)) {
			objectMap = JsonUtils.deserialize(data, objectMap.getClass());
			if(objectMap == null) throw new WalletException("params_error","data格式不正确");
		}
		return objectMap;
	}
	
	/**
	 * 将json字符串转为map，并与原map合并；data为空时返回原map
	 * @param data
	 * @param oldMap
	 * @return
	 */
	public static Map<String, Object> updateMap(String data, Map<String, Object> oldMap){
		if(data != null && !"".equals(data)) {
			Map<String, Object> map = jsonToMap(data);
			oldMap.putAll(map);
		}
		return oldMap;
	}
	
	public static Map<String, Object> objectToMap(Object object){
		String json = JsonUtils.serialize(object);
		return jsonToMap(json);
	}
	
	public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz){
		String json = JsonUtils.serialize(map);
		return JsonUtils.deserialize(json, clazz);
	}
	
	public static String toSBC(String input){ 
		char[] c=input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i]==12288) {
                c[i]= (char)32;
                continue;
            }
            if (c[i]>65280 && c[i]<65375)
                c[i]=(char)(c[i]-65248);
        }
        return new String(c);
	}
	
}
