<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String contextPath = request.getContextPath();
%>
<link rel="shortcut icon" href="<%=contextPath%>/images/favicon.ico" type="image/x-icon">
<meta name="force-rendering" contect="webkit">
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="chrome=1">
<meta charset="utf-8">
<meta name="keywords" content="${seoKeyWords}" />
<meta name="description" content="${seoDescription}" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1, user-scalable=no">
<meta name="format-detection" content="telephone=no" />

<link href="<%=contextPath%>${sessionScope.PAGE_SKIN}/css/comm.css" rel="stylesheet">
<c:if test="${sessionScope.PAGE_SKIN eq '/skins/mobile'}">
    <script type="text/javascript" src="<%=contextPath%>${sessionScope.PAGE_SKIN}/js/fastclick.js"></script>
</c:if>

<script type="text/javascript" src="<%=contextPath%>/js/jquery-3.5.1.min.js"></script>



<script type="text/javascript" src="<%=contextPath%>${sessionScope.PAGE_SKIN}/js/common.js"></script>
<script type="text/javascript" src="<%=contextPath%>/js/md5_util.js"></script>

<!--[if lte IE 9]>
<style type="text/css">
html{
    overflow: auto !important;
}
body{
    margin: 0 !important;
}
.content-all{
    min-height: 500px !important;
}
.footer-all{
    position: relative !important;
}
input[type=checkbox]{
    visibility: visible !important;
    display: inline-block !important;
}
.checkbox-label{
    display: none !important;
}
.nav-main .nav-box{
    width: 800px;
}
.footer-content .footer-down .contact UL{
    width: 80% !important;
}
</style>
<![endif]-->

