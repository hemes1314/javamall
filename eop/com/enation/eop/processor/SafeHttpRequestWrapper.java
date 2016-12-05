package com.enation.eop.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 安全的httprequest包装器
 * 
 * @author kingapex
 * 
 */
public class SafeHttpRequestWrapper extends HttpServletRequestWrapper {

	public SafeHttpRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	/**
	 * 字符过滤用正则列表.
	 */
	private static List<Object[]> filterRegs;
	private static List<Object[]> getFilterRegs() {
		if (filterRegs == null) {
			synchronized (SafeHttpRequestWrapper.class) {
				if (filterRegs == null) {
					filterRegs = new ArrayList<Object[]>();
					filterRegs.add(new Object[] { "<(no)?script[^>]*>.*?</(no)?script>", Pattern.CASE_INSENSITIVE });
					filterRegs.add(new Object[] { "</script>", Pattern.CASE_INSENSITIVE });
					filterRegs.add(new Object[] { "<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
//					filterRegs.add(new Object[] { "src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
//					filterRegs.add(new Object[] { "src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
					filterRegs.add(new Object[] { "eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
					filterRegs.add(new Object[] { "e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
					filterRegs.add(new Object[] { "(javascript:|vbscript:|view-source:)*", Pattern.CASE_INSENSITIVE });
					filterRegs.add(new Object[] { "<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
					filterRegs.add(new Object[] { "(window\\.location|window\\.|\\.location|document\\.cookie|document\\.|alert\\(.*?\\)|window\\.open\\()*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
					filterRegs.add(new Object[] { "<+\\s*\\w*\\s*(oncontrolselect|oncopy|oncut|ondataavailable|ondatasetchanged|ondatasetcomplete|ondblclick|ondeactivate|ondrag|ondragend|ondragenter|ondragleave|ondragover|ondragstart|ondrop|onerror=|onerroupdate|onfilterchange|onfinish|onfocus|onfocusin|onfocusout|onhelp|onkeydown|onkeypress|onkeyup|onlayoutcomplete|onload|onlosecapture|onmousedown|onmouseenter|onmouseleave|onmousemove|onmousout|onmouseover|onmouseup|onmousewheel|onmove|onmoveend|onmovestart|onabort|onactivate|onafterprint|onafterupdate|onbefore|onbeforeactivate|onbeforecopy|onbeforecut|onbeforedeactivate|onbeforeeditocus|onbeforepaste|onbeforeprint|onbeforeunload|onbeforeupdate|onblur|onbounce|oncellchange|onchange|onclick|oncontextmenu|onpaste|onpropertychange|onreadystatechange|onreset|onresize|onresizend|onresizestart|onrowenter|onrowexit|onrowsdelete|onrowsinserted|onscroll|onselect|onselectionchange|onselectstart|onstart|onstop|onsubmit|onunload)+\\s*=+", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
					filterRegs.add(new Object[] { "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)", Pattern.CASE_INSENSITIVE });
				}
			}
		}
		return filterRegs;
	}
	private static List<Pattern> patterns;
	private static List<Pattern> getPatterns() {
		if (patterns == null) {
			synchronized (SafeHttpRequestWrapper.class) {
				if (patterns == null) {
					patterns = new ArrayList<Pattern>(getFilterRegs().size());
					for (Object[] arr : getFilterRegs()) {
						if (arr.length == 2) {
							patterns.add(Pattern.compile((String) arr[0], (Integer) arr[1]));
						}
					}
				}
			}
		}
		return patterns;
	}
	
	/**
	 * 对字符进行安全过滤
	 * 
	 * @param value
	 * @return
	 */
	private String safeFilter(String value) {
		  if (value != null) {
	            // Avoid null characters
	            value = value.replaceAll("", "");

	            for (Pattern p : getPatterns()) {
	            	value = p.matcher(value).replaceAll("");
	            }
	            value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	            
//	            // Avoid anything between script tags
//	            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
//	            value = scriptPattern.matcher(value).replaceAll("");
//
//	            // Avoid anything in a src='...' type of e­xpression
////	            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
////	            value = scriptPattern.matcher(value).replaceAll("");
////
////	            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
////	            value = scriptPattern.matcher(value).replaceAll("");
//
//	            // Remove any lonesome </script> tag
//	            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
//	            value = scriptPattern.matcher(value).replaceAll("");
//
//	            // Remove any lonesome <script ...> tag
//	            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
//	            value = scriptPattern.matcher(value).replaceAll("");
//
//	            // Avoid eval(...) e­xpressions
//	            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
//	            value = scriptPattern.matcher(value).replaceAll("");
//
//	            // Avoid e­xpression(...) e­xpressions
//	            scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
//	            value = scriptPattern.matcher(value).replaceAll("");
//
//	            // Avoid javascript:... e­xpressions
//	            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
//	            value = scriptPattern.matcher(value).replaceAll("");
//
//	            // Avoid vbscript:... e­xpressions
//	            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
//	            value = scriptPattern.matcher(value).replaceAll("");
//
//	            // Avoid onload= e­xpressions
//	            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
//	            value = scriptPattern.matcher(value).replaceAll("");
//	            
//	            String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"  
//	                    + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";  
//	            scriptPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);  
//	            value = scriptPattern.matcher(value).replaceAll("");
//	            value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	        }
	        return value;
	}

	/**
	 * 对字串数组进行安全过滤
	 * 
	 * @param values
	 */
	private void safeFilter(String[] values) {
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				values[i] = this.safeFilter(values[i]);
			}
		}
	}

	public String getParameter(String name) {
		String value = super.getParameter(name);
		value = this.safeFilter(value);
		return value;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameterMap() {
		Map map = super.getParameterMap();
		Iterator keiter = map.keySet().iterator();
		while (keiter.hasNext()) {
			String name = keiter.next().toString();
			Object value = map.get(name);
			if (value instanceof String) {
				value = this.safeFilter(((String) value));
			}
			if (value instanceof String[]) {
				String[] values = (String[]) value;
				this.safeFilter(values);
			}
		}
		return map;
	}

	public String[] getParameterValues(String arg0) {
		String[] values = super.getParameterValues(arg0);
		this.safeFilter(values);
		return values;
	}

	

}
