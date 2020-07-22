<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@include file="/resources.jsp" %>
    <link href="<%=contextPath%>${sessionScope.PAGE_SKIN}/css/index.css" rel="stylesheet">
    <title>自动交易</title>
</head>
<body onbeforeunload="stomp_disconnect()">
<%@include file="/header.jsp" %>


<div id="main" style="height:400px;width:1500px;"></div>
<div style="">
    时间间隔
    <a class="timeGape" href="#" title="1">1分钟</a>
    <a class="timeGape" href="#" title="5">5分钟</a>
    <a class="timeGape" href="#" title="10">10分钟</a>
    <a class="timeGape" href="#" title="30">30分钟</a>
    <a class="timeGape" href="#" title="60">60分钟</a>
</div>
<div id="balance"></div>

<br/>
<form id="adjustForm" action="#" method="post" title="">
    价格偏差：
    <table id="table_adjPrice" cellpadding=0   cellspacing=0 >

    </table>
    <br><br>
    <input type="button" id="setPrice" value="设置偏差" style="width:70px;height: 30px"/> &nbsp;&nbsp;&nbsp;
    <input type="button" id="stop" value="停止" style="width:70px;height: 30px"/>&nbsp;&nbsp;&nbsp;
    <input type="button" id="start" value="启动" style="width:70px;height: 30px"/>
    <div id="retMsg" style="color: red;"></div>
</form>
<div id="rootLog" class="console" > </div>

<%@include file="/footer.jsp" %>

<script src="<%=contextPath%>/js/echarts.min.js" ></script>
<script src="<%=contextPath%>/js/index.js"></script>
<script src="<%=contextPath%>/js/chat_room/sockjs.min.js"></script>
<script src="<%=contextPath%>/js/chat_room/stomp.min.js"></script>
<script src="<%=contextPath%>/js/chat_room/stomp_manager.js"></script>
</body>
</html>
