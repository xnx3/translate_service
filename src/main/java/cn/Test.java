package cn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xnx3.StringUtil;

public class Test {
	public static void main(String[] args) {
		
		String s = "<i>没有？</i> <b>No?</b><i>点此立即注册一个域名</i><b>Click here to register a domain name now</b>";
		
		while(s.indexOf("<i>") > -1 && s.indexOf("</i>") > -1) {
			s = StringUtil.subStringReplace(s, "<i>", "</i>", "");
		}
		
		s = s.replaceAll("<b>", "");
		s = s.replaceAll("</b>", "");
		
		
		System.out.println(s);
	}
}
