package cn.zvo.translate.api.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.xnx3.BaseVO;
import com.xnx3.Log;
import com.xnx3.StringUtil;
import cn.zvo.http.Http;
import cn.zvo.http.Response;
import cn.zvo.translate.api.vo.LanguageListVO;
import cn.zvo.translate.api.vo.TranslateResultVO;
import cn.zvo.translate.api.vo.bean.LanguageBean;
import cn.zvo.translate.core.LanguageEnum;
import cn.zvo.translate.core.util.GoogleTranslateUtil;
import net.sf.json.JSONArray;

/**
 * 
 * 翻译接口
 * @author 管雷鸣
 *
 */
@Controller
@RequestMapping("/")
public class TranslateController{
	
	/**
	 * 当前支持的语言
	 * @return 支持的语言列表
	 * @author 管雷鸣
	 */
	@ResponseBody
	@RequestMapping(value="language.json", method = RequestMethod.POST)
	public LanguageListVO language(HttpServletRequest request) {
		LanguageListVO vo = new LanguageListVO();
		
		List<LanguageBean> list = new ArrayList<LanguageBean>();
		LanguageEnum[] languages = LanguageEnum.values();
        for (int i = 0; i < languages.length; i++) {
        	LanguageBean bean = new LanguageBean();
        	bean.setId(languages[i].id);
        	bean.setName(languages[i].name);
        	list.add(bean);
        }
        vo.setList(list);
		
		return vo;
	}
	
	/**
	 * 执行翻译操作
	 * @param from 将什么语言进行转换。<required> 传入如 chinese_simplified 具体可传入有：
	 * 			<ul>
	 * 				<li>chinese_simplified : 简体中文</li>
	 * 				<li>chinese_traditional : 繁體中文</li>
	 * 				<li>english : English</li>
	 * 				<li>更多参见：<a href="language.json.html" target="_black">language.json</a></li>
	 * 			</ul>
	 * @param to 转换为什么语言输出。<required> 传入如 english 具体可传入有：
	 * 			<ul>
	 * 				<li>chinese_simplified : 简体中文</li>
	 * 				<li>chinese_traditional : 繁體中文</li>
	 * 				<li>english : English</li>
	 * 				<li>更多参见：<a href="language.json.html" target="_black">language.json</a></li>
	 * 			</ul>
	 * @param text 转换的语言json数组，格式如 ["你好","探索星辰大海"] <required> <example=[&quot;你好&quot;,&quot;探索星辰大海&quot;]>
	 * @return 翻译结果
	 * @author 管雷鸣
	 */
	@ResponseBody
	@RequestMapping(value="translate.json", method = RequestMethod.POST)
	public TranslateResultVO translate(HttpServletRequest request,
			@RequestParam(value = "from", defaultValue = "chinese_simplified") String from,
			@RequestParam(value = "to", defaultValue = "english") String to,
			@RequestParam(value = "text", defaultValue = "") String text){
		TranslateResultVO vo = new TranslateResultVO();
		
		if(text == null || text.length() == 0) {
			vo.setBaseVO(BaseVO.FAILURE, "请传入 text 值");
			return vo;
		}
		
		vo.setFrom(from);
		vo.setTo(to);
		
		String sl = GoogleTranslateUtil.languageConvert(from);
		String tl = GoogleTranslateUtil.languageConvert(to);
		String url = "https://translate.googleapis.com/translate_a/t?anno=3&client=te&format=html&v=1.0&key&logld=vTE_20200210_00&sl="+sl+"&tl="+tl+"&sp=nmt&tc=1&sr=1&tk=&mode=1";
		
		JSONArray array = JSONArray.fromObject(text);
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
//		System.out.println("payload:"+payload.toString());
//		String payload = "q=%0A%0A%E5%9B%BD%E9%99%85%E5%8C%96%EF%BC%8C%E7%BD%91%E9%A1%B5%E8%87%AA%E5%8A%A8%E7%BF%BB%E8%AF%91%EF%BC%8C%E5%90%8C%E8%B0%B7%E6%AD%8C%E6%B5%8F%E8%A7%88%E5%99%A8%E8%87%AA%E5%8A%A8%E7%BF%BB%E8%AF%91%E7%9A%84%E6%95%88%E6%9E%9C%EF%BC%8C%E9%80%82%E7%94%A8%E4%BA%8E%E7%BD%91%E7%AB%99%E3%80%82%0A%0A&q=%E9%A6%96%E9%A1%B5&q=%E5%85%AC%E5%8F%B8%E7%AE%80%E4%BB%8B&q=%E8%81%94%E7%B3%BB%E6%88%91%E4%BB%AC&q=%3Ca%20i%3D0%3E%0A%09%E5%BD%93%E5%89%8D%E8%AF%AD%E8%A8%80%EF%BC%9A%3C%2Fa%3E%3Ca%20i%3D1%3Ezh-CN%0A%09%3C%2Fa%3E&q=%E5%88%87%E6%8D%A2%E7%A4%BA%E4%BE%8B%E4%B8%80%EF%BC%9A%E8%BF%99%E4%B8%AA%E4%B8%80%E8%88%AC%E6%98%AF%E7%BD%91%E7%AB%99%E4%B8%AD%E5%8F%B3%E4%B8%8A%E8%A7%92%E4%BC%9A%E6%9C%89%E5%88%87%E6%8D%A2%E8%AF%AD%E8%A8%80%E7%9A%84%E6%95%88%E6%9E%9C%EF%BC%8C%E8%AE%A9%E7%BD%91%E7%AB%99%E6%94%AF%E6%8C%81%E5%A4%9A%E8%AF%AD%E8%A8%80%E5%8A%9F%E8%83%BD%E3%80%82%E6%95%88%E6%9E%9C%EF%BC%9A&q=%7C%0A%09&q=%E5%88%87%E6%8D%A2%E7%A4%BA%E4%BE%8B%E4%BA%8C%EF%BC%9A%E5%87%BA%E7%8E%B0select%E4%B8%8B%E6%8B%89%E9%80%89%E6%8B%A9%E7%9A%84%E6%96%B9%E5%BC%8F%E9%80%89%E6%8B%A9%E5%BD%93%E5%89%8D%E5%88%87%E6%8D%A2%E7%9A%84%E8%AF%AD%E8%A8%80%EF%BC%9A&q=%3Ca%20i%3D0%3E%0A%09%E5%BC%80%E6%BA%90%E5%9C%B0%E5%9D%80%E5%8F%8A%E4%BD%BF%E7%94%A8%E6%96%B9%E5%BC%8F%E5%8F%82%E8%80%83%EF%BC%9A%20%3C%2Fa%3E%3Ca%20i%3D1%3Ehttps%3A%2F%2Fgitee.com%2Fmail_osc%2Ftranslate%3C%2Fa%3E&q=%E8%AF%AD%E8%A8%80%E7%BF%BB%E8%AF%91%E5%BE%AE%E4%BB%B6";
    	Response res;
		try {
			res = GoogleTranslateUtil.trans(url, payload.toString(), null, null, null);
		} catch (IOException e) {
			e.printStackTrace();
			vo.setBaseVO(BaseVO.FAILURE, e.getMessage());
			return vo;
		}
		Log.debug(res.getContent());
		
		//判断结果，输出
		if(res.getCode() == 200) {
			vo.setText(JSONArray.fromObject(res.getContent()));
		}else {
			vo.setBaseVO(BaseVO.FAILURE, res.getContent());
		}
		
		return vo;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		Http http = new Http();
		
		String url = "https://api.translate.zvo.cn/translate_a/t?anno=3&client=te&format=html&v=1.0&key&logld=vTE_20200210_00&sl=zh-CN&tl=en&sp=nmt&tc=1&sr=1&tk=113318.465989&mode=1";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36");
		headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,la;q=0.7");
		headers.put("sec-ch-ua", "\"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"92\"");
		
		String payload = "q=%0A%0A%E5%9B%BD%E9%99%85%E5%8C%96%EF%BC%8C%E7%BD%91%E9%A1%B5%E8%87%AA%E5%8A%A8%E7%BF%BB%E8%AF%91%EF%BC%8C%E5%90%8C%E8%B0%B7%E6%AD%8C%E6%B5%8F%E8%A7%88%E5%99%A8%E8%87%AA%E5%8A%A8%E7%BF%BB%E8%AF%91%E7%9A%84%E6%95%88%E6%9E%9C%EF%BC%8C%E9%80%82%E7%94%A8%E4%BA%8E%E7%BD%91%E7%AB%99%E3%80%82%0A%0A&q=%E9%A6%96%E9%A1%B5&q=%E5%85%AC%E5%8F%B8%E7%AE%80%E4%BB%8B&q=%E8%81%94%E7%B3%BB%E6%88%91%E4%BB%AC&q=%3Ca%20i%3D0%3E%0A%09%E5%BD%93%E5%89%8D%E8%AF%AD%E8%A8%80%EF%BC%9A%3C%2Fa%3E%3Ca%20i%3D1%3Ezh-CN%0A%09%3C%2Fa%3E&q=%E5%88%87%E6%8D%A2%E7%A4%BA%E4%BE%8B%E4%B8%80%EF%BC%9A%E8%BF%99%E4%B8%AA%E4%B8%80%E8%88%AC%E6%98%AF%E7%BD%91%E7%AB%99%E4%B8%AD%E5%8F%B3%E4%B8%8A%E8%A7%92%E4%BC%9A%E6%9C%89%E5%88%87%E6%8D%A2%E8%AF%AD%E8%A8%80%E7%9A%84%E6%95%88%E6%9E%9C%EF%BC%8C%E8%AE%A9%E7%BD%91%E7%AB%99%E6%94%AF%E6%8C%81%E5%A4%9A%E8%AF%AD%E8%A8%80%E5%8A%9F%E8%83%BD%E3%80%82%E6%95%88%E6%9E%9C%EF%BC%9A&q=%7C%0A%09&q=%E5%88%87%E6%8D%A2%E7%A4%BA%E4%BE%8B%E4%BA%8C%EF%BC%9A%E5%87%BA%E7%8E%B0select%E4%B8%8B%E6%8B%89%E9%80%89%E6%8B%A9%E7%9A%84%E6%96%B9%E5%BC%8F%E9%80%89%E6%8B%A9%E5%BD%93%E5%89%8D%E5%88%87%E6%8D%A2%E7%9A%84%E8%AF%AD%E8%A8%80%EF%BC%9A&q=%3Ca%20i%3D0%3E%0A%09%E5%BC%80%E6%BA%90%E5%9C%B0%E5%9D%80%E5%8F%8A%E4%BD%BF%E7%94%A8%E6%96%B9%E5%BC%8F%E5%8F%82%E8%80%83%EF%BC%9A%20%3C%2Fa%3E%3Ca%20i%3D1%3Ehttps%3A%2F%2Fgitee.com%2Fmail_osc%2Ftranslate%3C%2Fa%3E&q=%E8%AF%AD%E8%A8%80%E7%BF%BB%E8%AF%91%E5%BE%AE%E4%BB%B6";
    	Response res = http.post(url, payload, headers);
    	System.out.println(res.getCode());
		System.out.println(JSONArray.fromObject(res.getContent()));
        
	}
}
