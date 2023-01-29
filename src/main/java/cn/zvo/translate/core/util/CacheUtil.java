package cn.zvo.translate.core.util;

import cn.zvo.fileupload.FileUpload;
import cn.zvo.fileupload.framework.springboot.FileUploadUtil;
import cn.zvo.fileupload.storage.local.LocalStorage;
import cn.zvo.http.Response;
import net.sf.json.JSONObject;

/**
 * 缓存层
 * @author 管雷鸣
 */
public class CacheUtil {
//	static FileUpload file = new FileUpload();
	static {
		/*
		 * 设置使用本地存储的方式。并将上传的文件存储到 D盘的fileupload文件夹下
		 * 如果不使用 fileUpload.setStorage(...) 设置存储方式，那默认使用的便是本地存储，文件存储到当前项目的根路径下
		 */
//		LocalStorage localStorage = new LocalStorage();
//		localStorage.setLocalFilePath("D:/fileupload/");
//		file.setStorage(localStorage);
	}
	
	/**
	 * 向缓存中设置或更新
	 * @param hash 要翻译的内容文本的hash
	 * @param language 要翻译的目标语种
	 * @param response 翻译后的接口响应结果
	 */
	public static void set(String hash, String language, Response response) {
//		com.xnx3.CacheUtil.set(hash+"_"+language, response);
		response.headerFields = null; //避免序列化失败
		FileUploadUtil.uploadString(hash+"_"+language+".txt", JSONObject.fromObject(response).toString());
	}
	
	/**
	 * 通过对要翻译的内容文本的hash，取缓存中是否有存放这个结果。
	 * @param hash 要翻译的内容文本的hash
	 * @param language 要翻译的目标语种
	 * @return 如果有，返回 {@link Response} 直接将其返回响应， 如果没有，则返回null
	 */
	public static Response get(String hash, String language) {
		//Object obj = com.xnx3.CacheUtil.get(hash+"_"+language);
		String text = FileUploadUtil.getText(hash+"_"+language+".txt");
		if(text != null && text.length() > 0) {
			JSONObject json = JSONObject.fromObject(text);
			Response res = new Response();
			res.setCode(json.getInt("code"));
			if(json.get("content") != null) {
				res.setContent(json.getString("content"));
			}
			return res;
		}else {
			return null;
		}
	}
	
}
