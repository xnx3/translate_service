package cn.zvo.translate.service.baidu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.xnx3.DateUtil;
import com.xnx3.MD5Util;
import com.xnx3.StringUtil;

import cn.zvo.http.Http;
import cn.zvo.http.Response;
import cn.zvo.translate.api.vo.TranslateResultVO;
import cn.zvo.translate.core.service.Language;
import cn.zvo.translate.core.service.interfaces.ServiceInterface;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 对接百度翻译提供的翻译服务
 * @author 管雷鸣
 *
 */
public class ServiceInterfaceImplement implements ServiceInterface{
	static Http http; //http请求工具类，使用参考 https://github.com/xnx3/http.java
	static {
		http = new Http();
	}
	// 请求地址
	private static final String TRANS_API_URL = "http://api.fanyi.baidu.com/api/trans/vip/translate";
	//百度翻译的appid
	private String appId;
	//百度翻译的密钥
	private String secretKey;
	
	public ServiceInterfaceImplement(Map<String, String> config) {
		this.appId = config.get("appId");
		this.secretKey = config.get("secretKey");
	}
	
	public static void main(String[] args) {
		Map<String, String> config = new HashMap<String, String>();
		config.put("appId", "202301200000000000");
		config.put("secretKey", "0ZuvL6G51Wxxxxxxxxxx");
		
		ServiceInterfaceImplement service = new ServiceInterfaceImplement(config);
		service.setLanguage();
		
		JSONArray array = new JSONArray();
		array.add("你好");
		array.add("世界");
		
		TranslateResultVO vo = service.api("zh", "en", array);
		System.out.println(vo);
	}
	
	@Override
	public TranslateResultVO api(String from, String to, JSONArray array) {
		TranslateResultVO vo = new TranslateResultVO();
		from = "auto";
		
		//要翻译的原字符串
		String sourceText = array.toString(); 
		
		int time = DateUtil.timeForUnix10();
		// 生成签名sign, appid+q+salt+密钥的MD5值
		String signString = this.appId + sourceText + time + this.secretKey;
		String sign = MD5Util.MD5(signString);
		// 请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("q", StringUtil.stringToUrl(sourceText));
		params.put("from", from);
		params.put("to", to);
		params.put("appid", this.appId);
		params.put("salt", "" + time);
		params.put("sign", sign);
		
		//header
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		
		try {
			Response response = http.post(TRANS_API_URL, params, headers);
			if(response.getCode() == 200) {
				JSONObject result = JSONObject.fromObject(response.getContent());
				if(result.get("trans_result") == null) {
					//接口响应出现了错误码
					vo.setBaseVO(TranslateResultVO.FAILURE, response.getContent());
				}else {
					//成功
					JSONArray resultArray = result.getJSONArray("trans_result").getJSONObject(0).getJSONArray("dst");
					vo.setText(resultArray);
					vo.setBaseVO(TranslateResultVO.SUCCESS, "SUCCESS");
				}
			}else {
				//http没有正常响应
				vo.setBaseVO(TranslateResultVO.FAILURE, "http response code : " + response.getCode()+", content: "+ response.getContent());
			}
		} catch (IOException e) {
			e.printStackTrace();
			vo.setBaseVO(TranslateResultVO.FAILURE, e.getMessage());
		}

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
