package cn.zvo.translate.core.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.xnx3.BaseVO;

import cn.zvo.http.Http;
import cn.zvo.http.Response;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GoogleTranslateUtil {
	public static Http http;
	static {
		http = new Http();
	}
	
	public static Response trans(String url, String payload, String userAgent, String acceptLanguage, String contentLength) throws IOException {
		if(userAgent == null || userAgent.length() == 0) {
			userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36";
		}
		
//		String url = "https://api.translate.zvo.cn/translate_a/t?anno=3&client=te&format=html&v=1.0&key&logld=vTE_20200210_00&sl=zh-CN&tl=en&sp=nmt&tc=1&sr=1&tk=472527.103212&mode=1";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("User-Agent", userAgent);
		if(acceptLanguage != null && acceptLanguage.length() > 0) {
			headers.put("Accept-Language", acceptLanguage);
		}else {
			headers.put("Accept-Language", "zh-CN,zh;q=0.9");
		}
		headers.put("Connection", "keep-alive");
		if(contentLength != null && contentLength.length() > 0) {
			headers.put("Content-Length", contentLength);
		}else {
			headers.put("Content-Length", payload.length()+"");
		}
		headers.put("Accept", "*/*");
		
//		System.out.println("url: "+url);
//		System.out.println(JSONObject.fromObject(headers));
		
//		System.out.println("payload:"+payload.toString());
//		String payload = "q=%0A%0A%E5%9B%BD%E9%99%85%E5%8C%96%EF%BC%8C%E7%BD%91%E9%A1%B5%E8%87%AA%E5%8A%A8%E7%BF%BB%E8%AF%91%EF%BC%8C%E5%90%8C%E8%B0%B7%E6%AD%8C%E6%B5%8F%E8%A7%88%E5%99%A8%E8%87%AA%E5%8A%A8%E7%BF%BB%E8%AF%91%E7%9A%84%E6%95%88%E6%9E%9C%EF%BC%8C%E9%80%82%E7%94%A8%E4%BA%8E%E7%BD%91%E7%AB%99%E3%80%82%0A%0A&q=%E9%A6%96%E9%A1%B5&q=%E5%85%AC%E5%8F%B8%E7%AE%80%E4%BB%8B&q=%E8%81%94%E7%B3%BB%E6%88%91%E4%BB%AC&q=%3Ca%20i%3D0%3E%0A%09%E5%BD%93%E5%89%8D%E8%AF%AD%E8%A8%80%EF%BC%9A%3C%2Fa%3E%3Ca%20i%3D1%3Ezh-CN%0A%09%3C%2Fa%3E&q=%E5%88%87%E6%8D%A2%E7%A4%BA%E4%BE%8B%E4%B8%80%EF%BC%9A%E8%BF%99%E4%B8%AA%E4%B8%80%E8%88%AC%E6%98%AF%E7%BD%91%E7%AB%99%E4%B8%AD%E5%8F%B3%E4%B8%8A%E8%A7%92%E4%BC%9A%E6%9C%89%E5%88%87%E6%8D%A2%E8%AF%AD%E8%A8%80%E7%9A%84%E6%95%88%E6%9E%9C%EF%BC%8C%E8%AE%A9%E7%BD%91%E7%AB%99%E6%94%AF%E6%8C%81%E5%A4%9A%E8%AF%AD%E8%A8%80%E5%8A%9F%E8%83%BD%E3%80%82%E6%95%88%E6%9E%9C%EF%BC%9A&q=%7C%0A%09&q=%E5%88%87%E6%8D%A2%E7%A4%BA%E4%BE%8B%E4%BA%8C%EF%BC%9A%E5%87%BA%E7%8E%B0select%E4%B8%8B%E6%8B%89%E9%80%89%E6%8B%A9%E7%9A%84%E6%96%B9%E5%BC%8F%E9%80%89%E6%8B%A9%E5%BD%93%E5%89%8D%E5%88%87%E6%8D%A2%E7%9A%84%E8%AF%AD%E8%A8%80%EF%BC%9A&q=%3Ca%20i%3D0%3E%0A%09%E5%BC%80%E6%BA%90%E5%9C%B0%E5%9D%80%E5%8F%8A%E4%BD%BF%E7%94%A8%E6%96%B9%E5%BC%8F%E5%8F%82%E8%80%83%EF%BC%9A%20%3C%2Fa%3E%3Ca%20i%3D1%3Ehttps%3A%2F%2Fgitee.com%2Fmail_osc%2Ftranslate%3C%2Fa%3E&q=%E8%AF%AD%E8%A8%80%E7%BF%BB%E8%AF%91%E5%BE%AE%E4%BB%B6";
    	Response res = http.post(url, payload, headers);
		
		return res;
	}
	
	public static void main(String[] args) throws IOException {
		String url = "https://api.translate.zvo.cn/translate_a/t?anno=3&client=te&format=html&v=1.0&key&logld=vTE_20200210_00&sl=zh-CN&tl=en&sp=nmt&tc=1&sr=1&tk=&mode=1";
		String payload = "q=%E4%BD%A0%E5%A5%BD&q=%E5%93%88%E5%96%BD";
		System.out.println(trans(url, payload, null, null, null).getContent());
		
	}
}
