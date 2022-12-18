package cn.zvo.translate.api.vo.bean;

/**
 * 具体语言
 * @author 管雷鸣
 *
 */
public class LanguageBean {
	private String id; 	//语言标识
	private String name;	//语言的文字描述
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "LanguageBean [id=" + id + ", name=" + name + "]";
	}
	
	
}
