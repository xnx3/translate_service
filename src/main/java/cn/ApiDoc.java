package cn;

import com.xnx3.doc.JavaDoc;

/**
 * 自动扫描指定的包下所有controller的json接口，根据其标准的JAVADOC注释，生成接口文档。  
 * 使用参考 https://gitee.com/leimingyun/dashboard/wikis/leimingyun/wm/preview?sort_id=4518712&doc_id=1101390
 * @author 管雷鸣
 */
public class ApiDoc {
	public static void main(String[] args) {
		JavaDoc doc = new com.xnx3.doc.JavaDoc("cn.zvo.translate.api.controller");
//		doc.templatePath = "/Users/apple/Downloads/javadoc/";		//本地模板所在磁盘的路径
		doc.name = "translate.zvo.cn API文档";				//文档的名字
		doc.domain = "https://api.translate.zvo.cn";				//文档中默认的接口请求域名
		doc.version = "1.0";						//当前做的软件系统的版本号

		doc.generateHtmlDoc();	//生成文档
	}
}