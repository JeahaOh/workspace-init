<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import ="egovframework.com.cmm.LoginVO" %>
<%
	LoginVO loginVO = (LoginVO)session.getAttribute("loginVO"); 
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>팝업존관리 목록</title>
<link rel="stylesheet" href="./css/neo.css" />
<link rel="stylesheet" href="./css/subheader.css" />
<script type="text/javascript">
	function fn_changeSite( val ) {
		window.location = "./selectPopupZoneList.do?siteId=" + val;
	}
</script>
</head>
<body>

<h1 class="subHeader">팝업존관리 &gt; 
<select name="siteId" onchange="fn_changeSite(this.value)">
<c:if test="${fn:length(siteSeList) != 0}">
	<c:forEach var="siteSeList" items="${siteSeList}" varStatus="status">
		<optgroup label="${siteSeList.codeNm}">
		<c:forEach var="siteInfoList" items="${siteInfoList}" varStatus="status">
			<c:if test="${siteSeList.code eq siteInfoList.siteSe}">
			<option value="${siteInfoList.siteId}" <c:if test="${popupZone.siteId eq siteInfoList.siteId}">selected="selected"</c:if>>${siteInfoList.siteNm}</option>
			</c:if>
		</c:forEach>			
		</optgroup>
	</c:forEach>
</c:if>
<c:if test="${fn:length(siteSeList) == 0}">
	<c:forEach var="siteInfoList" items="${siteInfoList}" varStatus="status">
		<c:if test="${siteSeList.code eq siteInfoList.siteSe}">
		<option value="${siteInfoList.siteId}" <c:if test="${popupZone.siteId eq siteInfoList.siteId}">selected="selected"</c:if>>${siteInfoList.siteNm}</option>
		</c:if>
	</c:forEach>
</c:if>
</select>
&gt; 목록</h1>

<script type="text/javascript">
	function fn_deletePopupZone( url ) {
		if( confirm("삭제하시겠습니까?") ) {
			window.location = url;
		}
	}
</script>

<div class="clearfix topMargin">
	<div class="floatLeft">
		등록된 팝업존 수 : ${fn:length(popupZoneList)} 건
	</div>
	<div class="floatRight">
	</div>
</div>

<table class="table_t1 topMargin" width="100%"> 
	<colgroup>
		<col width="40"/>
		<col width="200"/>
		<col/>
		<col width="100"/>
		<col width="120"/>
		<col width="80"/>
		<col width="60"/>
		<col width="60"/>
	</colgroup>
	<tr>
		<th>번호</th>
		<th>사이트명</th>
		<th>팝업존이름</th>
		<th>팝업존항목수</th>
		<th>이미지크기</th>
		<th>등록일</th>
		<th>수정</th>
		<th>삭제</th>
	</tr>
	<c:set var="sNumber" value="${fn:length(popupZoneList)}" />
	
		<%

			if( "ADMIN".equals(loginVO.getUserSe()) ) {
		%>
	
	<c:forEach var="result" items="${popupZoneList}" varStatus="status">
	<tr>
		<td align="right">${sNumber}</td> 
		<td align="center">${siteInfoMap[result.siteId]}</td>
		<td><a href="./selectPopupZoneItemList.do?siteId=${popupZone.siteId}&amp;popupZoneNo=${result.popupZoneNo}">${result.popupZoneNm}</a></td>
		<td align="right">${result.popupZoneIemCo}</td>
		<td align="center">${result.imageWidthSize}px * ${result.imageHeightSize}px</td>
		<td align="center">${result.frstRegisterPnttmYMD}</td>
		<td align="center"><a href="./updatePopupZoneView.do?siteId=${popupZone.siteId}&amp;popupZoneNo=${result.popupZoneNo}" class="button green"><span>수정</span></a></td>
		<td align="center"><a href="./deletePopupZone.do?siteId=${popupZone.siteId}&amp;popupZoneNo=${result.popupZoneNo}" onclick="fn_deletePopupZone(this.href); return false;" class="button red"><span>삭제</span></a></td>
	</tr>
		<c:set var="sNumber" value="${sNumber-1}" />
	</c:forEach>
		 <%
			} else	{
		%>

			<c:forEach var="result" items="${popupZoneList}" varStatus="status">
			<c:if test="${result.popupZoneNo eq 92}">
	<tr>
		<td align="right">${sNumber}</td> 
		<td align="center">${siteInfoMap[result.siteId]}</td>
		<td><a href="./selectPopupZoneItemList.do?siteId=${popupZone.siteId}&amp;popupZoneNo=${result.popupZoneNo}">${result.popupZoneNm}</a></td>
		<td align="right">${result.popupZoneIemCo}</td>
		<td align="center">${result.imageWidthSize}px * ${result.imageHeightSize}px</td>
		<td align="center">${result.frstRegisterPnttmYMD}</td>
		<td align="center"><a href="./updatePopupZoneView.do?siteId=${popupZone.siteId}&amp;popupZoneNo=${result.popupZoneNo}" class="button green"><span>수정</span></a></td>
		<td align="center"><a href="./deletePopupZone.do?siteId=${popupZone.siteId}&amp;popupZoneNo=${result.popupZoneNo}" onclick="fn_deletePopupZone(this.href); return false;" class="button red"><span>삭제</span></a></td>
	</tr>
		</c:if>
		<c:set var="sNumber" value="${sNumber-1}" />
	</c:forEach>

		<%
			}
		%>

	<c:if test="${fn:length(popupZoneList) == 0}">
	<tr>
		<td colspan="8" align="center">등록된 팝업존이 없습니다.</td>
	</tr>
	</c:if>
</table>

<div class="clearfix topMargin">
	<div class="floatLeft">
	</div>
	<div class="floatRight">
		<a href="./addPopupZoneView.do?siteId=${popupZone.siteId}" class="button blue"><span>등록</span></a>
	</div>
</div>

</body>
</html>