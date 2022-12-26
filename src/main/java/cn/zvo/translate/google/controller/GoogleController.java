package cn.zvo.translate.google.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.xnx3.Log;
import cn.zvo.http.Http;
import cn.zvo.http.Response;
import cn.zvo.translate.core.util.GoogleTranslateUtil;

/**
 * 
 * 对外开放的翻译接口
 * @author 管雷鸣
 *
 */
@Controller
@RequestMapping("/translate_a/")
public class GoogleController{
	public static Http http;
	static {
		http = new Http();
	}

	/**
	 * google翻译接口的路径
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping("/t")
	public String index(HttpServletRequest request, HttpServletResponse response) throws IOException{
//		response.addHeader("Access-Control-Allow-Origin", "*"); //允许跨域
		
//		System.out.println("----------");
		String payload = getRequestData(request);
		if(payload == null || payload.length() == 0) {
			return "翻译的内容为空";
		}
		String referer = request.getHeader("referer"); 
		Log.debug("referer:"+referer+"\tlength:"+payload.length());
		//System.out.println(payload);
		//System.out.println(JSONObject.fromObject(request.getHeaderNames()));
//		Enumeration headerNames = request.getHeaderNames();
//		while (headerNames.hasMoreElements()) {
//			String headerName = (String) headerNames.nextElement();
//			System.out.println(headerName + " : " + request.getHeader(headerName));
//		}
//	
		
		String url = "https://translate.googleapis.com/translate_a/t?"+request.getQueryString();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("User-Agent", request.getHeader("user-agent"));
		headers.put("Accept-Language", request.getHeader("accept-language"));
		headers.put("Connection", "keep-alive");
		headers.put("Content-Length", request.getHeader("content-length"));
		headers.put("Accept", "*/*");
//		headers.put("Accept-Encoding", request.getHeader("accept-encoding"));
//		headers.put("Accept-Language", request.getHeader("accept-language"));
		
//		System.out.println(JSONObject.fromObject(headers));
		
		Response res = GoogleTranslateUtil.trans(url, payload, request.getHeader("user-agent"), request.getHeader("accept-language"), request.getHeader("content-length"));
		//Log.debug(res.getContent());
        
		return res.getContent();
	}
	
	public String getRequestData(HttpServletRequest httpServletRequest){
		  HttpServletRequestWrapper httpServletRequestWrapper = new HttpServletRequestWrapper(httpServletRequest);
		  StringBuilder sb = new StringBuilder();
		  BufferedReader reader = null;
		  InputStreamReader inputStreamReader=null;
		  ServletInputStream servletInputStream =null;
		  try {
		   servletInputStream = httpServletRequestWrapper.getInputStream();
		   inputStreamReader=new InputStreamReader (servletInputStream, Charset.forName("UTF-8"));
		   reader = new BufferedReader(inputStreamReader);
		   String line = "";
		   while ((line = reader.readLine()) != null) {
		    sb.append(line);
		   }
		  } catch (IOException e) {
		   return "";
		  }finally {
		   try {
		    if(servletInputStream!=null){
		     servletInputStream.close();
		    }
		    if(inputStreamReader!=null){
		     inputStreamReader.close();
		    }
		    if(reader!=null){
		     reader.close();
		    }
		   } catch (IOException e) {    
		   }
		  }
		  return sb.toString ();
		 }
}
