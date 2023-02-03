package cn.zvo.translate.core.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import com.xnx3.Lang;
import com.xnx3.ScanClassUtil;

import cn.zvo.log.framework.springboot.Log;
import cn.zvo.translate.core.service.interfaces.ServiceInterface;
import cn.zvo.translate.service.google.ServiceInterfaceImplement;

/**
 * 翻译服务核心
 * @author 管雷鸣
 */
//@Component(value="translateService")
@EnableConfigurationProperties(ApplicationConfig.class)
@Configuration
public class Service implements CommandLineRunner{
	@Resource
	private ApplicationConfig translateServiceApplicationConfig;
	
	public static ServiceInterface serviceInterface;
	static {
		serviceInterface = new ServiceInterfaceImplement();
		serviceInterface.setLanguage();
	}

	public void run(String... args) throws Exception {
		com.xnx3.Log.debug("load translate config by application.properties / yml : "+this.translateServiceApplicationConfig);
		if(translateServiceApplicationConfig == null) {
			System.err.println("请配置 translate.service.xxx.xxx 设置");
			return;
		}
    	loadConfig(this.translateServiceApplicationConfig); //加载application配置
	}
	
	/**
     * 加载配置 {@link ApplicationConfig} （aplication.properties/yml）文件的配置数据，通过其属性来决定使用何种配置。
     * <br>这个其实就相当于用java代码来动态决定配置
     * @param config
     */
    public void loadConfig(ApplicationConfig config) {
    	if(config == null) {
    		return;
    	}

		if(config.getService() != null) {
			for (Map.Entry<String, Map<String, String>> entry : config.getService().entrySet()) {
				//拼接，取该插件在哪个包
				String datasourcePackage = "cn.zvo.translate.service."+entry.getKey();
				List<Class<?>> classList = ScanClassUtil.getClasses(datasourcePackage);
				if(classList.size() == 0) {
					System.err.println("====================");
					System.err.println(" 【【【 ERROR 】】】    ");
					System.err.println(" translate service 未发现 "+datasourcePackage +" 这个包存在，请确认pom.xml是否加入了这个 service 支持模块");
					System.err.println("====================");
					continue;
				}else {
					for (int i = 0; i < classList.size(); i++) {
						com.xnx3.Log.debug("class list item : "+classList.get(i).getName());
					}
				}
				
				//搜索继承ServiceInterface接口的
				List<Class<?>> logClassList = ScanClassUtil.searchByInterfaceName(classList, "cn.zvo.translate.core.service.interfaces.ServiceInterface");
				for (int i = 0; i < logClassList.size(); i++) {
					Class logClass = logClassList.get(i);
					com.xnx3.Log.debug("log datasource : "+logClass.getName());
					try {
						Object newInstance = logClass.getDeclaredConstructor(Map.class).newInstance(entry.getValue());
						ServiceInterface service = (ServiceInterface) newInstance;
						service.setLanguage(); //初始化设置语种
						
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException| InvocationTargetException  | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				}
			}
		}
    }
	
	
}
