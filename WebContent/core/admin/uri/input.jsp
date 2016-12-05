<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>	
<div id="addcontent">
<div class="input">
<form >
<input type="hidden" name="themeUri.id" value="${ themeUri.id}" />
<table cellspacing="1" cellpadding="3" width="100%" class="form-table">
	<tr>
		<th><label class="text">URL：</label></th>
		<td>
		<input type="text" name="themeUri.uri" value="${ themeUri.uri}"  dataType="string" isrequired="true" />
		</td>
	</tr>
	<tr>
		<th><label class="text">模板文件：</label></th>
		<td><input type="text" name="themeUri.path" value="${themeUri.path }" dataType="string" isrequired="true"/> </td>
	</tr>
	<tr>
		<th><label class="text">http缓存： </label></th>
		<td>
		<input type="radio" name="themeUri.httpcache" value="1"  <c:if test="${uri.httpcache==1}" >checked="true"</c:if> >是&nbsp;&nbsp;
		<input type="radio" name="themeUri.httpcache" value="0"  <c:if test="${uri.httpcache==null || uri.httpcache==0}" >checked="true"</c:if> >否
		</td>
	</tr>	
	<tr>
		<th><label class="text">页面名称：</label></th>
		<td><input type="text" name="themeUri.pagename"  value="${ themeUri.pagename}"  dataType="string" isrequired="true"/> 
		</td>
	</tr>
	<tr>
		<th><label class="text">积分</label></th>
		<td><input type="text" name="themeUri.point" value="${themeUri.point }"  dataType="string" required="int" value="0"/> </td>
	</tr>
	<tr>
		<th><label class="text">关键字：</label></th>
		<td><input type="text" name="themeUri.keywords" value="${ themeUri.keywords}"   dataType="string" isrequired="true"/> 
		</td>
	</tr>
	<tr>
		<th><label class="text">描述：</label></th>
		<td> 
			<textarea name="themeUri.description"  style="width:250px;height:100px"  >${themeUri.description }</textarea>
		</td>
	</tr>	
</table>
</form>
</div>

</div>

<div class="footContent" >
<div style="width: 200px; height: 40px; margin: 0pt auto;"
	class="mainFoot">
<table style="margin: 0pt auto; width: auto;">
	<tbody>
		<tr>
			<td><b class="save">
			<button class="submitBtn" >保存</button>
			</b></td>
		</tr>
	</tbody>
</table>
</div>
</div> 
