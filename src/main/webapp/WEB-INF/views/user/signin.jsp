<%--
  Created by IntelliJ IDEA.
  User: fengping
  Date: 2020/5/14
  Time: 22:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="/resources.jsp" %>
    <link href="<%=contextPath%>/${sessionScope.PAGE_SKIN}/css/user.css" rel="stylesheet">
    <title>登录</title>
</head>
<body>

<%@ include file="/header.jsp" %>

<!-- sign in -->
<div class="content-all">
    <div class="rs-box-left">
        <p class="signin-bg">
        </p>
    </div>
    <div class="rs-box-right">
        <div class="title-container">
            <span class="title">登录</span>
        </div>
        <div class="content">
            <form action="<%=contextPath%>/user/signin.html" method="post" onsubmit="return ValidForm(this)">
                <div class="form-tip-line">
                    <span>${signInObj.errMsg}</span>
                </div>
                <div class="form-line">
                    <label>账号</label>
                    <input type="text" name="account" value="${signInObj.account}" placeholder="邮箱/手机号码" class="err-not-null err-mobile-email err-long-64 icon-account"/>
                    <span>${signInObj.errMsgAccount}</span>
                </div>
                <div class="form-line">
                    <label>密码</label>
                    <input type="password" name="password" value="" class="err-not-null err-long-30 icon-password"/>
                    <span>${signInObj.errMsgPassword}</span>
                </div>
                <div class="form-line">
                    <label>验证码</label>
                    <input class="verify-code-input err-not-null err-long-4 err-min-4 icon-verify-code" type="text" maxlength="4" name="verifyCode"/>
                    <a class="verify-code-show" href="javascript:void(0)" onclick="changeImgVerifyCode('signin_verify_code')">
                        <img src="<%=contextPath%>/common/imgVerifyCode.html" id="signin_verify_code"/>
                    </a>
                    <span>${signInObj.errMsgVerifyCode}</span>
                </div>
                <div class="form-line">
                    <label></label>
                    <input type="checkbox" name="isAutoSign" id="is_auto_sign" value="true"/>
                    <label class="checkbox-label" for="is_auto_sign"></label>
                    <em id="is_auto_sign_desc">下次自动登录</em>
                </div>
                <div class="form-line submit-form-line content-center">
                    <input type="submit" value="登录"/>
                    <a class="form-link underline" href="<%=contextPath%>/user/retrieve.html">忘记密码？</a>
                    <a class="form-link underline" href="<%=contextPath%>/user/register.html">立即注册</a>
                </div>
            </form>
        </div>
    </div>
</div>


<%@ include file="/footer.jsp" %>

</body>
</html>
