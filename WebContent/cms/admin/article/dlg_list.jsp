<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<c:set var="fieldList" value="${fieldList}" />

<div class="input">
	<table class="form-table">
		<tr>
			<th class="catbox"></th>
			<td>
				<input type="button" name="filterBtn" class="filterBtn" value="　刷　新　" />
			</td>
		</tr>
	</table>		
</div>

<div class="grid">
	<input type="hidden" name="catid" value="${catid }" />
	<div class="gridbox">
		<grid:grid from="webpage" ajax="yes">
			<grid:header> 
				<grid:cell width="50px">选择</grid:cell>
					<c:forEach items="${fieldList}" var="field">
					<th>${field.china_name}</th> 
					</c:forEach>
					<th style="text-align:right">添加时间</th>
			</grid:header> 
			<grid:body item="article">
				<grid:cell><input type="checkbox" name="relatedid" value="${article.id }" />${article.id }</grid:cell>
				<html:field></html:field>
	  			<td align="right"><html:dateformat pattern="yyyy-MM-dd HH:mm:ss" time="${article.add_time*1000 }" /></td>
			</grid:body>
		</grid:grid>
	</div>
	<div style="clear:both;padding-top:5px;"></div>
	<div class="submitlist" align="center">
		<table>
			<tr>
				<td><input name="button" type="button" value="　确　定　" class="submitBtn" /></td>
			</tr>
		</table>
	</div>
</div>