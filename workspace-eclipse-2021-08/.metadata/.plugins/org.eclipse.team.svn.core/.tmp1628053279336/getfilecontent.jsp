<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@
	page
	import = "java.net.InetAddress" 
	import = "java.lang.management.ManagementFactory"
	import = "java.lang.management.RuntimeMXBean"
	import = "java.util.Arrays"
	import = "java.util.HashMap"
	import = "java.util.List"
	import = "java.util.Map"
	import = "egovframework.com.cmm.LoginVO"
	import = "org.apache.commons.lang.builder.ToStringBuilder"
	import = "org.apache.commons.lang.builder.ToStringStyle"
	import = "kr.co.hanshinit.NeoCMS.uat.uia.service.LoginUtil"
  import = "java.util.Properties"
%>


<% 
	LoginVO loginVO = (LoginVO) session.getAttribute("loginVO");
	LoginVO staffVO = (LoginVO) session.getAttribute("staffLoginVO");

	if( loginVO == null ) loginVO = new LoginVO();
	if( staffVO == null ) staffVO = new LoginVO();
	
%>

<!DOCTYPE html>
<html lang="ko">

<head>
  <meta charset="utf-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
  <meta name="viewport"
    content="width=device-width, height=device-height, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0, user-scalable=yes" />
  <meta name="robots" content="noindex, nofollow">
  <title>TEST REJECT</title>
</head>

<body>

  <%
  	//	주무관 211.114.22.148, 한신 175.212.21.90
    String whiteList = "175.212.21.90, 0:0:0:0:0:0:0:1, 105.18.28.28, 211.114.22.148, 175.212.21.90, 14.33.193.17, 175.115.244.254";
    String requester = request.getRemoteAddr();
    out.println("<br/>YOUR IP : " + requester);
    //out.println("<br/>ALLOWED IP CONTAINS YOUR IP : " + whiteList.contains(requester) );

	if( !whiteList.contains(requester) ) {
      out.println("<br/>NOT ALLOWED ACCESS.");
      %>
        <script> //location.href = '/site/landing/landing.html'; </script>
      <%
    } else {
      out.println("<br/>WELCOME.");
    }

	out.println("<br/>" + loginVO.getUserSe());
	out.println("<br/>" + ToStringBuilder.reflectionToString(loginVO, ToStringStyle.MULTI_LINE_STYLE));
	out.println("<br/>" + ToStringBuilder.reflectionToString(staffVO, ToStringStyle.MULTI_LINE_STYLE));
  %>

    <ul>
    <li>local IP : <%=request.getRemoteAddr() %></li>
    <li>local host : <%=request.getRemoteHost() %></li>
    <li>context : <%=request.getContextPath() %></li>
    <li>url : <%=request.getRequestURL() %></li>
    <li>uri : <%=request.getRequestURI() %></li>
    <li>path : <%=request.getServletPath() %></li>
    <li>port : <%=request.getServerPort() %></li>
    <li>root : <%=application.getRealPath("/") %></li>
    <li>str server ip : <%=request.getServerName() %></li>
    <li>str server port : <%=Integer.toString(request.getServerPort()) %></li>
  </ul>
  <%--
  InetAddress inet = InetAddress.getLocalHost();
    server ip: <%=inet.getHostAddress()%>
  --%>



<%
  out.println( System.getProperty("os.name") );
%>
  <br/>

<%
  RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
  out.println("\tJAVA_OPTS  : " + mxBean.getInputArguments() );
%>
<br/>
<%
  String serverRoll = "";
  List<String> args = mxBean.getInputArguments();
  for( String arg : args ) {
    if( arg.startsWith("-Dserver.was.roll=")) {
      serverRoll = arg;
    }
  }
  serverRoll = serverRoll.split("=")[1];
  out.println("serverRoll : " + serverRoll);
  out.println("<br/>uptime : " + mxBean.getUptime() );


%>



</body>

</html>
