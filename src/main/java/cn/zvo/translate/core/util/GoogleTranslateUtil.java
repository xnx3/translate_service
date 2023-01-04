package cn.zvo.translate.core.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.xnx3.StringUtil;

import cn.zvo.http.Http;
import cn.zvo.http.Response;
import cn.zvo.translate.core.LanguageEnum;
import net.sf.json.JSONArray;

public class GoogleTranslateUtil {
	public static Http http;
	static {
		http = new Http();
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
	
	
	/**
	 * 将雷鸣云翻译的语言标识转换为google的语言标识
	 * @param currentLanguage 传入雷鸣翻译的语言标识，传入如 chinese_simplified
	 * @return 返回google使用的语言标识，返回如 zh-CN 另外如果未匹配到，则返回空字符串
	 */
	public static String languageConvert(String currentLanguage) {
		LanguageEnum[] languages = LanguageEnum.values();
        
		if(currentLanguage.equalsIgnoreCase(LanguageEnum.CHINESE_SIMPLIFIED.id)) {
			return "zh-CN";
		}else if(currentLanguage.equalsIgnoreCase(LanguageEnum.CHINESE_TRADITIONAL.id)) {
			return "zh-TW";
		}else if(currentLanguage.equalsIgnoreCase(LanguageEnum.ENGLISH.id)) {
			return "en";
		}else if(currentLanguage.equalsIgnoreCase(LanguageEnum.KOREAN.id)) {
			return "ko";
		}else if(currentLanguage.equalsIgnoreCase(LanguageEnum.JAPANESE.id)) {
			return "ja";	
//		}else if(currentLanguage.equalsIgnoreCase(LanguageEnum.RUSSIAN.id)) {
//			return "ru";
		}else {
			return "";
		}
		
		/*
		 * 
		 * 待完善语种，对应关系参考：
		 * _callbacks____0l8x5pzzk({"sl":{"auto":"检测语言","sq":"阿尔巴尼亚语","ar":"阿拉伯语","am":"阿姆哈拉语","as":"阿萨姆语","az":"阿塞拜疆语","ee":"埃维语","ay":"艾马拉语","ga":"爱尔兰语","et":"爱沙尼亚语","or":"奥利亚语","om":"奥罗莫语","eu":"巴斯克语","be":"白俄罗斯语","bm":"班巴拉语","bg":"保加利亚语","is":"冰岛语","pl":"波兰语","bs":"波斯尼亚语","fa":"波斯语","bho":"博杰普尔语","af":"布尔语(南非荷兰语)","tt":"鞑靼语","da":"丹麦语","de":"德语","dv":"迪维希语","ti":"蒂格尼亚语","doi":"多格来语","ru":"俄语","fr":"法语","sa":"梵语","tl":"菲律宾语","fi":"芬兰语","fy":"弗里西语","km":"高棉语","ka":"格鲁吉亚语","gom":"贡根语","gu":"古吉拉特语","gn":"瓜拉尼语","kk":"哈萨克语","ht":"海地克里奥尔语","ko":"韩语","ha":"豪萨语","nl":"荷兰语","ky":"吉尔吉斯语","gl":"加利西亚语","ca":"加泰罗尼亚语","cs":"捷克语","kn":"卡纳达语","co":"科西嘉语","kri":"克里奥尔语","hr":"克罗地亚语","qu":"克丘亚语","ku":"库尔德语（库尔曼吉语）","ckb":"库尔德语（索拉尼）","la":"拉丁语","lv":"拉脱维亚语","lo":"老挝语","lt":"立陶宛语","ln":"林格拉语","lg":"卢干达语","lb":"卢森堡语","rw":"卢旺达语","ro":"罗马尼亚语","mg":"马尔加什语","mt":"马耳他语","mr":"马拉地语","ml":"马拉雅拉姆语","ms":"马来语","mk":"马其顿语","mai":"迈蒂利语","mi":"毛利语","mni-Mtei":"梅泰语（曼尼普尔语）","mn":"蒙古语","bn":"孟加拉语","lus":"米佐语","my":"缅甸语","hmn":"苗语","xh":"南非科萨语","zu":"南非祖鲁语","ne":"尼泊尔语","no":"挪威语","pa":"旁遮普语","pt":"葡萄牙语","ps":"普什图语","ny":"齐切瓦语","ak":"契维语","ja":"日语","sv":"瑞典语","sm":"萨摩亚语","sr":"塞尔维亚语","nso":"塞佩蒂语","st":"塞索托语","si":"僧伽罗语","eo":"世界语","sk":"斯洛伐克语","sl":"斯洛文尼亚语","sw":"斯瓦希里语","gd":"苏格兰盖尔语","ceb":"宿务语","so":"索马里语","tg":"塔吉克语","te":"泰卢固语","ta":"泰米尔语","th":"泰语","tr":"土耳其语","tk":"土库曼语","cy":"威尔士语","ug":"维吾尔语","ur":"乌尔都语","uk":"乌克兰语","uz":"乌兹别克语","es":"西班牙语","iw":"希伯来语","el":"希腊语","haw":"夏威夷语","sd":"信德语","hu":"匈牙利语","sn":"修纳语","hy":"亚美尼亚语","ig":"伊博语","ilo":"伊洛卡诺语","it":"意大利语","yi":"意第绪语","hi":"印地语","su":"印尼巽他语","id":"印尼语","jw":"印尼爪哇语","en":"英语","yo":"约鲁巴语","vi":"越南语","zh-CN":"中文","ts":"宗加语"},"tl":{"sq":"阿尔巴尼亚语","ar":"阿拉伯语","am":"阿姆哈拉语","as":"阿萨姆语","az":"阿塞拜疆语","ee":"埃维语","ay":"艾马拉语","ga":"爱尔兰语","et":"爱沙尼亚语","or":"奥利亚语","om":"奥罗莫语","eu":"巴斯克语","be":"白俄罗斯语","bm":"班巴拉语","bg":"保加利亚语","is":"冰岛语","pl":"波兰语","bs":"波斯尼亚语","fa":"波斯语","bho":"博杰普尔语","af":"布尔语(南非荷兰语)","tt":"鞑靼语","da":"丹麦语","de":"德语","dv":"迪维希语","ti":"蒂格尼亚语","doi":"多格来语","ru":"俄语","fr":"法语","sa":"梵语","tl":"菲律宾语","fi":"芬兰语","fy":"弗里西语","km":"高棉语","ka":"格鲁吉亚语","gom":"贡根语","gu":"古吉拉特语","gn":"瓜拉尼语","kk":"哈萨克语","ht":"海地克里奥尔语","ko":"韩语","ha":"豪萨语","nl":"荷兰语","ky":"吉尔吉斯语","gl":"加利西亚语","ca":"加泰罗尼亚语","cs":"捷克语","kn":"卡纳达语","co":"科西嘉语","kri":"克里奥尔语","hr":"克罗地亚语","qu":"克丘亚语","ku":"库尔德语（库尔曼吉语）","ckb":"库尔德语（索拉尼）","la":"拉丁语","lv":"拉脱维亚语","lo":"老挝语","lt":"立陶宛语","ln":"林格拉语","lg":"卢干达语","lb":"卢森堡语","rw":"卢旺达语","ro":"罗马尼亚语","mg":"马尔加什语","mt":"马耳他语","mr":"马拉地语","ml":"马拉雅拉姆语","ms":"马来语","mk":"马其顿语","mai":"迈蒂利语","mi":"毛利语","mni-Mtei":"梅泰语（曼尼普尔语）","mn":"蒙古语","bn":"孟加拉语","lus":"米佐语","my":"缅甸语","hmn":"苗语","xh":"南非科萨语","zu":"南非祖鲁语","ne":"尼泊尔语","no":"挪威语","pa":"旁遮普语","pt":"葡萄牙语","ps":"普什图语","ny":"齐切瓦语","ak":"契维语","ja":"日语","sv":"瑞典语","sm":"萨摩亚语","sr":"塞尔维亚语","nso":"塞佩蒂语","st":"塞索托语","si":"僧伽罗语","eo":"世界语","sk":"斯洛伐克语","sl":"斯洛文尼亚语","sw":"斯瓦希里语","gd":"苏格兰盖尔语","ceb":"宿务语","so":"索马里语","tg":"塔吉克语","te":"泰卢固语","ta":"泰米尔语","th":"泰语","tr":"土耳其语","tk":"土库曼语","cy":"威尔士语","ug":"维吾尔语","ur":"乌尔都语","uk":"乌克兰语","uz":"乌兹别克语","es":"西班牙语","iw":"希伯来语","el":"希腊语","haw":"夏威夷语","sd":"信德语","hu":"匈牙利语","sn":"修纳语","hy":"亚美尼亚语","ig":"伊博语","ilo":"伊洛卡诺语","it":"意大利语","yi":"意第绪语","hi":"印地语","su":"印尼巽他语","id":"印尼语","jw":"印尼爪哇语","en":"英语","yo":"约鲁巴语","vi":"越南语","zh-TW":"中文（繁体）","zh-CN":"中文（简体）","ts":"宗加语"},"al":{}})
		 * 
		 */
	}
	
	/**
	 * 对翻译的结果进行优化替换，将不合适的替换掉
	 * @param res
	 * @return
	 */
	public static Response responseReplace(Response res) {
		if(res.getCode() == 200) {
    		boolean update = false; //是否改动了，如果true改动了，需要后面向res中更新
    		JSONArray array = JSONArray.fromObject(res.getContent());
    		
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
    			//重新序列化赋予response
    			res.content = array.toString();
    		}
    		
    	}
		
		return res;
	}
	
	public static void main(String[] args) throws IOException {
		String url = "https://api.translate.zvo.cn/translate_a/t?anno=3&client=te&format=html&v=1.0&key&logld=vTE_20200210_00&sl=zh-CN&tl=en&sp=nmt&tc=1&sr=1&tk=&mode=1";
		String payload = "q=%E4%BD%A0%E5%A5%BD&q=%E5%93%88%E5%96%BD";
		System.out.println(trans(url, payload, null, null, null).getContent());
		
	}
}
