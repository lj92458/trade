<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/resources.jsp" %>
<link href="<%=contextPath%>/css/error.css" rel="stylesheet">
<title>服务器内部错误 </title>
</head>
<body>

<%@ include file="/header.jsp" %>

<div class="content-all">
	<div class="error-page-content">
		<div class="up"></div>
		<div class="down">
			<div class="content">
				<span>Error [${errCode}]：</span>
				<p>${errMsg}</p>
			</div>
			<div class="buttons">
				<a href="javascript:history.go(-1)">返回上一页</a>
				<a href="<%=contextPath%>/index.html">返回首页</a>
			</div>
		</div>
	</div>
</div>


<%@ include file="/footer.jsp" %>

</body>
</html>