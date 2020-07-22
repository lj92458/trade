package com.liujun.trade;


import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * 使用junit5
 */
@ExtendWith(SpringExtension.class)//junit整合spring的测试//立马开启了spring的注解
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:application.xml",
        "classpath:spring-mvc.xml"

})//加载核心配置文件，自动构建spring容器
@ActiveProfiles("dev")
public class SpringTest {

}
