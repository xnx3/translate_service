package cn.zvo.translate.service.baidu;

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

public class ServiceInterfaceImplement implements ServiceInterface{
	static Http http; //http请求工具类，使用参考 https://github.com/xnx3/http.java
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
		
//		application/x-www-form-urlencoded
//		http.post(to, null, null)
		
		
		
		return vo;
	}

	@Override
	public void setLanguage() {
		
		/*
		 * 向语种列表中追加支持的语种，以下注意只需要改第二个参数为对接的翻译服务中，人家的api语种标识即可
		 */
		
		Language.append("english", "en", "English");
		Language.append("chinese_simplified", "zh", "简体中文");
		Language.append("chinese_traditional", "cht", "繁體中文");
		Language.append("korean", "kor", "한어");
	}
	
}
