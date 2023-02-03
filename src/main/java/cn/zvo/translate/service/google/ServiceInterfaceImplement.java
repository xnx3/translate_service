package cn.zvo.translate.service.google;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.xnx3.StringUtil;
import cn.zvo.http.Http;
import cn.zvo.http.Response;
import cn.zvo.translate.api.vo.TranslateResultVO;
import cn.zvo.translate.core.LanguageEnum;
import cn.zvo.translate.core.service.Language;
import cn.zvo.translate.core.service.interfaces.ServiceInterface;
import net.sf.json.JSONArray;

/**
 * 谷歌翻译接口的对接
 * @author 管雷鸣
 *
 */
public class ServiceInterfaceImplement implements ServiceInterface{
	static Http http;
	static {
		http = new Http();
	}
	
	public static void main(String[] args) {
		ServiceInterfaceImplement service = new ServiceInterfaceImplement();
		service.setLanguage();
		
		JSONArray array = new JSONArray();
		array.add("你好");
		array.add("世界");
		
		TranslateResultVO vo = service.api("chinese_simplified", "english", array);
		System.out.println(vo);
	}
	
	@Override
	public TranslateResultVO api(String from, String to, JSONArray array) {
		TranslateResultVO vo = new TranslateResultVO();
		from = Language.currentToService(from).getInfo();
		to = Language.currentToService(to).getInfo();
		
		String domain = "translate.googleapis.com";
		domain = "api.translate.zvo.cn";	//本地调试用
		String url = "https://"+domain+"/translate_a/t?anno=3&client=te&format=html&v=1.0&key&logld=vTE_20200210_00&sl="+from+"&tl="+to+"&sp=nmt&tc=1&sr=1&tk=&mode=1";
		System.out.println(url);
//		JSONArray array = JSONArray.fromObject(text);
		StringBuffer payload = new StringBuffer();
		for (int i = 0; i < array.size(); i++) {
			if(i > 0) {
				payload.append("&");
			}
			if(from.equalsIgnoreCase(LanguageEnum.CHINESE_SIMPLIFIED.id) || from.equalsIgnoreCase(LanguageEnum.CHINESE_TRADITIONAL.id)) {
				//简体中文、繁体中文时要用url编码
				payload.append("q="+StringUtil.stringToUrl(array.getString(i)));
			}else {
				payload.append("q="+array.getString(i));
			}
		}
		System.out.println(payload);
		Response res = null;
		try {
			res = trans(url, payload.toString(), null, null, null);
		} catch (IOException e) {
			e.printStackTrace();
			res = new Response();
			res.code = 0;
			res.content = e.getMessage();
//			return res;
		}
		
		//Log.debug(res.getContent());
		
		
		//组合vo
		vo.setResult(res.code > 0? TranslateResultVO.SUCCESS:TranslateResultVO.FAILURE);
		vo.setInfo(res.getContent());
		vo.setFrom(from);
		vo.setTo(to);
		vo.setStringText(res.getContent());
		
		//对结果中不合适的地方进行替换
		vo = responseReplace(vo);
		
		return vo;
	}
	
	public static Response trans(String url, String payload, String userAgent, String acceptLanguage, String contentLength) throws IOException {
		if(userAgent == null || userAgent.length() == 0) {
			userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36";
		}
		
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
		
    	Response res = http.post(url, payload, headers);
		return res;
	}
	

	/**
	 * 对翻译的结果进行优化替换，将不合适的替换掉
	 * @param vo
	 * @return
	 */
	public static TranslateResultVO responseReplace(TranslateResultVO vo) {
		if(vo.getResult() - TranslateResultVO.SUCCESS == 0 && vo.getText() != null) {
			
    		boolean update = false; //是否改动了，如果true改动了，需要后面向res中更新
    		JSONArray array = vo.getText();
    		
    		//遍历 i标签的问题
    		for (int i = 0; i < array.size(); i++) {
				if(array.getString(i).indexOf("<i>") > -1) {
					//存在i标签，过滤
					array.set(i, resultFindBTagText(array.getString(i)));
					update = true;
				}
			}
    		
    		//遍历 &#39; 的问题
    		for (int i = 0; i < array.size(); i++) {
				if(array.getString(i).indexOf("&#39;") > -1) {
					//存在，过滤，将其替换
					String text = array.getString(i);
					text = text.replaceAll("&#39;", "'");
					array.set(i, text);
					
					update = true;
				}
			}
    		
    		//遍历 &amp; 的问题
    		for (int i = 0; i < array.size(); i++) {
				if(array.getString(i).indexOf("&amp;") > -1) {
					//存在，过滤，将其替换
					String text = array.getString(i);
					text = text.replaceAll("&amp;", "&");
					array.set(i, text);
					
					update = true;
				}
			}
    		
    		if(update) {
    			//重新赋予
    			vo.setText(array);
    		}
    		
    	}
		
		return vo;
	}
	

	/**
	 * <p>从翻译结果中取b标签的内容</p>
	 * 谷歌翻译后，如果句子有点长，会被拆分为这样 
	 * <textarea><i>没有？</i> <b>No?</b><i>点此立即注册一个域名</i><b>Click here to register a domain name now</b></textarea>
	 * 此时需要将i全删除掉，将b标签隐藏掉，只返回b标签内的翻译好的结果
	 * @param text 包含i、b标签的翻译结果
	 * @return 只取b标签中的结果返回
	 */
	public static String resultFindBTagText(String text) {
		if(text == null || text.length() == 0) {
			return "";
		}
		
		while(text.indexOf("<i>") > -1 && text.indexOf("</i>") > -1) {
			text = StringUtil.subStringReplace(text, "<i>", "</i>", "");
		}
		
		text = text.replaceAll("<b>", "");
		text = text.replaceAll("</b>", "");
		return text;
	}

	@Override
	public void setLanguage() {
		Language.append("english", "en", "English");
		Language.append("chinese_simplified", "zh-CN", "简体中文");
		Language.append("chinese_traditional", "zh-TW", "繁體中文");
		Language.append("korean", "ko", "한어");
	}
	
}
