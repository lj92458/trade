
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<div class="window-model-layer"></div>


<!-- footer start -->
<div class="footer-all">
	<div class="footer-content">
		<c:choose>
			<c:when test="${sessionScope.PAGE_SKIN eq '/skins/mobile'}">
				<%-- 移动端 --%>
				<div class="footer-section">
					<div class="section-title">
						<span>自动交易</span>
					</div>
					<ul class="links">
						<li><a href="<%=contextPath%>/company.html">企业信息</a></li>
						<li><a href="<%=contextPath%>/contact.html">联系我们</a></li>
						<li><a href="<%=contextPath%>/legal.html">法律声明</a></li>
						<li><a href="<%=contextPath%>/faq.html">常见问题</a></li>
						<li><a href="<%=contextPath%>/siteMap.html">网站地图</a></li>
						<li><a href="<%=contextPath%>/authquery.html">防伪查询</a></li>
						<li><a class="computer-ui-switch" href="<%=contextPath%>/index.html?skin=default-ui">电脑版</a></li>
					</ul>
				</div>
				<div class="footer-section">
					<div class="section-title">
						<span>联系方式</span>
					</div>
					<ul class="contacts">
						<li class="icon-phone">电话xxx</li>
						<li class="icon-email">邮箱xxx</li>
						<li class="icon-addr">
							公司地址xxx
						</li>
					</ul>
				</div>
				<div class="footer-section">
					<div class="section-title">
						<span>关注我们</span>
					</div>
					<div class="section-content">
						<em class="wechat-image">
							<img src="<%=contextPath%>/images/footer/wechat-qr-image.jpg"/>
						</em>
						<em class="webo-link">
							<a href="http://m.weibo.com/u/6152941556" target="_blank">
								<img src="<%=contextPath%>/images/weibo_logo.png"/>
							</a>
						</em>
					</div>
				</div>
				<div class="footer-section">
					<div class="copyright">
						<span>Copyright© 2016-2035 版权所有xxx</span>
						<br/>
						<a href="http://www.miitbeian.gov.cn/" target="_blank">备案号xxx</a>
					</div>
				</div>
			</c:when>


			<c:otherwise>
				<%-- PC端 --%>
				<%--<div class="footer-up"></div>--%>
				<div class="footer-center">
					<ul>
						<li><a href="<%=contextPath%>/authquery.html">防伪查询</a></li>
						<li><a href="<%=contextPath%>/siteMap.html">网站地图</a></li>
						<li><a href="<%=contextPath%>/faq.html">常见问题</a></li>
						<li><a href="<%=contextPath%>/legal.html">法律声明</a></li>
						<li><a href="<%=contextPath%>/contact.html">联系我们</a></li>
						<li><a href="<%=contextPath%>/company.html">企业信息</a></li>
					</ul>
					<div class="wechat-qr-image">
						<img src="<%=contextPath%>/images/footer/wechat-qr-image.jpg"/><br>
						<a>微信公众号:自动交易</a>
					</div>
					<div class="weibo-link">
						<img src="<%=contextPath%>/images/footer/weibo-qr-image.jpg"/><br>
						<a>新浪官方微博</a>
					</div>
				</div>
				<div class="footer-down">
					<div class="contact">
						<ul>
							<li class="icon-phone">热线：xxx</li>
							<li class="icon-email">邮箱：xxx</li>
							<li class="icon-wechat">公众号：xxx</li>
							<li class="icon-addr">
								公司地址
							</li>
						</ul>
					</div>
					<div class="copyright">
						<span>Copyright© 2016-2035 版权所有xxx</span>
						<em> | </em>
						<a href="http://www.miitbeian.gov.cn/" target="_blank">备案号xxxx</a>
						<em> | </em>
						<a href="http://sdxy.gov.cn/" class="e-biz-license" target="_blank">企业信用公示</a>
						<em> | </em>
						<a class="mobile-ui-switch" href="<%=contextPath%>/index.html?skin=wap-ui">手机版</a>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>
<!-- footer end -->