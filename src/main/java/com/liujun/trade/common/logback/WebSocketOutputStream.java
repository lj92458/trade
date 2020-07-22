package com.liujun.trade.common.logback;

import com.liujun.trade.utils.SpringContextUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 等spring加载完成，从spring获取SimpMessagingTemplate类的实例
 */
@Component //由spring实例化，监听才会生效
public class WebSocketOutputStream extends OutputStream implements ApplicationListener<ContextRefreshedEvent> {

    private static SimpMessagingTemplate template;
    private List<Byte> byteList = new ArrayList<>(512);
    /**
     * websocket发送的目的地址，例如：/topic/rootLog
     */
    private String destination;

    public WebSocketOutputStream() {

    }

    @Override
    public void write(int b) throws IOException {
        //只有当template被spring注入了值，才开始处理
        if (template != null) {

            if(b!='\n'&& b!='\r'){
                byteList.add((byte) b);
            }else if (b == '\n') {//如果是换行符，就发送消息
                flush();
            }
        }
    }

    @Override
    public void flush() throws IOException {
        if (template != null) {
            byte[] arr = new byte[byteList.size()];
            for (int i = 0; i < byteList.size(); i++) {
                arr[i] = byteList.get(i);
            }
            if(arr.length>0) {
                sendMsg(new String(arr));
            }
            byteList.clear();
        }
    }

    private void sendMsg(String chatMsg) throws IOException {
        if (destination == null) {
            throw new IOException("webSocket发送广播消息的目的地址不能为空");
        } else {
            template.convertAndSend(destination, chatMsg);
        }
    }

    @Override
    public void close() throws IOException {
    }



    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public static SimpMessagingTemplate getTemplate() {
        return template;
    }

    public static void setTemplate(SimpMessagingTemplate template) {
        WebSocketOutputStream.template = template;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        SimpMessagingTemplate template = SpringContextUtil.getBean(SimpMessagingTemplate.class);
        WebSocketOutputStream.setTemplate(template);
        System.out.println("SimpMessagingTemplate:" + template.toString());

    }
}
