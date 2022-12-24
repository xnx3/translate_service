package cn.zvo.translate.api.vo;

import com.xnx3.BaseVO;
import net.sf.json.JSONArray;

/**
 * 翻译结果
 * @author 管雷鸣
 *
 */
public class TranslateResultVO extends BaseVO{
	private String from;
	private String to;
	private JSONArray text;
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public JSONArray getText() {
		return text;
	}
	public void setText(JSONArray text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return "TranslateResultVO [from=" + from + ", to=" + to + ", text=" + text + "]";
	}
}
