<%@ page import="com.liujun.trade.common.CustomizedPropertyPlaceholderConfigurer" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!-- header start -->

<script type="text/javascript" src="<%=contextPath%>/js/js.cookie.js"></script>
<script type="text/javascript">

</script>

<!-- header start -->
<div class="header-all">
    <c:if test="${sessionScope.PAGE_SKIN ne '/skins/mobile'}">
        <div class="header-top">
            <div class="header-top-inner">
                <div class="account-area">
                    <c:if test="${sessionScope.userAccount eq null}">
                        <a href="<%=contextPath%>/user/signin.html">登录</a>
                        <a href="#">注册</a>
                    </c:if>
                    <c:if test="${sessionScope.userAccount ne null}">
                        <em>欢迎您，</em><a href="#">${sessionScope.userAccount.userAccount}</a>
                        <a href="<%=contextPath%>/user/signout.html" style="color:#a41e22">[退出]</a>
                    </c:if>

                </div>

            </div>
        </div>
    </c:if>
    <!-- <div class="header-top-bg"></div> -->
    <div class="header-content">
        <div class="nav-menu-main">
            <a href="javascript:void(0)"></a>
        </div>

        <div class="nav-main">
            <c:if test="${sessionScope.PAGE_SKIN eq '/skins/mobile'}">
                <div class="mob-nav-top">
                    <span class="shopping-cart-area">
                        <!--<a href="#" class="shopping-bag-link"><em id="shopping_bag_count">0</em></a> -->
                        <a class="nav-close"></a>
                    </span>

                    <span class="account-area">
                    <c:if test="${sessionScope.userAccount eq null}">
                        <a class="btn" href="<%=contextPath%>/user/signin.html">登录</a>
                        <a class="btn" href="#">注册</a>
                    </c:if>
                    <c:if test="${sessionScope.userAccount ne null}">
                        <em class="nick-name">欢迎您，${sessionScope.userAccount.userAccount}</em>
                        <a class="sign-out" href="<%=contextPath%>/user/signout.html">[退出]</a>
                    </c:if>
                </span>
                </div>
            </c:if>
            <ul class="nav-box">

                <li><a href="<%=contextPath%>/index.html">跨平台对冲</a></li>
                <li><a href="<%=contextPath%>/menu_over_coin.html">跨币种对冲</a></li>
                <li><a href="<%=contextPath%>/menu_over_futuer.html">期现对冲</a></li>
                <li><a href="<%=contextPath%>/menu_quant.html">量化交易Quant</a></li>
                <li><a href="<%=contextPath%>/menu_low_rate.html">低频策略</a></li>
                <li><a href="<%=contextPath%>/menu_cfmm.html">恒定函数CFMM</a></li>
                <li><a href="<%=contextPath%>/menu_dex.html">dex交易</a></li>
            </ul>

        </div>
        <div class="logo-main">
            <a href="<%=contextPath%>/index.html"></a>
        </div>
    </div>
</div>
<!-- header end -->
