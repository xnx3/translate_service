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
import com.xnx3.MD5Util;
import com.xnx3.StringUtil;
import cn.zvo.http.Response;
import cn.zvo.log.framework.springboot.LogUtil;
import cn.zvo.translate.api.vo.LanguageListVO;
import cn.zvo.translate.api.vo.TranslateResultVO;
import cn.zvo.translate.api.vo.bean.LanguageBean;
import cn.zvo.translate.core.LanguageEnum;
import cn.zvo.translate.core.util.CacheUtil;
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
		
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("test", "123");
        LogUtil.add(params);
        
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
		vo.setFrom(from);
		vo.setTo(to);
		
		if(text == null || text.length() == 0) {
			vo.setBaseVO(BaseVO.FAILURE, "请传入 text 值");
			return vo;
		}
		if(to.length() < 1) {
			vo.setBaseVO(BaseVO.FAILURE, "请传入要翻译的语种");
			return vo;
		}
		
		//日志
		String referer = request.getHeader("referer"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("referer", referer);
		LogUtil.add(params);
		
		//先从缓存中取
		String hash = MD5Util.MD5(text);
		Response res = CacheUtil.get(hash, to);
		if(res == null) {
			//缓存中没有，那么从api中取
			res = byApi(from, to, text);
			//取出来后加入缓存
			CacheUtil.set(hash, to, res);
		}
		
		//判断结果，输出
		if(res.getCode() == 200) {
			vo.setText(JSONArray.fromObject(res.getContent()));
		}else {
			vo.setBaseVO(BaseVO.FAILURE, "error code : "+res.getCode());
		}
		
		return vo;
	}
	
	//从接口中取翻译数据数据
	private Response byApi(String from, String to, String text) {
		String sl = GoogleTranslateUtil.languageConvert(from);
		String tl = GoogleTranslateUtil.languageConvert(to);
		String domain = "translate.googleapis.com";
//		domain = "api.translate.zvo.cn";	//本地调试用
		String url = "https://"+domain+"/translate_a/t?anno=3&client=te&format=html&v=1.0&key&logld=vTE_20200210_00&sl="+sl+"&tl="+tl+"&sp=nmt&tc=1&sr=1&tk=&mode=1";
		
		
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
		Response res = null;
		try {
			res = GoogleTranslateUtil.trans(url, payload.toString(), null, null, null);
		} catch (IOException e) {
			e.printStackTrace();
			res = new Response();
			res.code = 0;
			res.content = e.getMessage();
			return res;
		}
		//Log.debug(res.getContent());
		
		//对结果中不合适的地方进行替换
		res = GoogleTranslateUtil.responseReplace(res);
		return res;
	}
	
	public static void main(String[] args) throws IOException {
		String text = "12346";
		String hash = com.xnx3.MD5Util.MD5(text);
				
    	System.out.println(hash);
        
	}
}
