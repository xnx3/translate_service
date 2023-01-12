package cn.zvo.translate.core.util;

import cn.zvo.http.Response;

/**
 * 缓存层
 * @author 管雷鸣
 */
public class CacheUtil {
	
	/**
	 * 向缓存中设置或更新
	 * @param hash 要翻译的内容文本的hash
	 * @param language 要翻译的目标语种
	 * @param response 翻译后的接口响应结果
	 */
	public static void set(String hash, String language, Response response) {
		com.xnx3.CacheUtil.set(hash+"_"+language, response);
	}
	
	/**
	 * 通过对要翻译的内容文本的hash，取缓存中是否有存放这个结果。
	 * @param hash 要翻译的内容文本的hash
	 * @param language 要翻译的目标语种
	 * @return 如果有，返回 {@link Response} 直接将其返回响应， 如果没有，则返回null
	 */
	public static Response get(String hash, String language) {
		Object obj = com.xnx3.CacheUtil.get(hash+"_"+language);
		if(obj != null) {
			Response res = (Response) obj;
			return res;
		}else {
			return null;
		}
	}
	
}
