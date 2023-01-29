package cn.zvo.translate.core.service;

import org.springframework.stereotype.Component;

import cn.zvo.translate.core.service.interfaces.ServiceInterface;
import cn.zvo.translate.service.google.ServiceInterfaceImplement;

/**
 * 翻译服务核心
 * @author 管雷鸣
 */
@Component(value="translateService")
public class Service {
	
	public static ServiceInterface serviceInterface;
	static {
		serviceInterface = new ServiceInterfaceImplement();
		serviceInterface.setLanguage();
	}
	
}
