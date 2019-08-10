package com.th.tower.controller;

import com.alibaba.fastjson.JSONArray;
import com.th.tower.model.Sentry;
import com.th.watchtower.tongchuan.util.Netease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 王虹波
 * @since 1.0, 2019/8/10 14:58
 */
@ServerEndpoint("/watchtower")
@Controller
public class WatchtowerServer {
    private static final Logger logger = LoggerFactory.getLogger(WatchtowerServer.class);
    private static CopyOnWriteArraySet<Sentry> SentrySet = new CopyOnWriteArraySet<>();
    private Sentry sentry;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        logger.info("有新的连接{}", session.getId());
        session.getBasicRemote().sendText("天王盖地虎");
    }
    @OnClose
    public void onClose(Session session) {
        logger.info("连接断开");
        JSONArray machines = new JSONArray();
        for(Sentry s : SentrySet) {
            if (s.getId() == session.getId()) {
                machines.add(regIp(s.getMachine()));
                machines.add(regName(s.getMachine()));
                machines.add("bb");
                send(machines);
                SentrySet.remove(s);
            }
        }
    }
    @OnMessage
    public void onMessage(String message, Session session) {
        if (Pattern.matches("^Machine.*", message)) {
            sentry = new Sentry();
            sentry.setId(session.getId());
            sentry.setMachine(message);
            SentrySet.add(sentry);
        }
        logger.info("收到新的消息==>" + message);
    }
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error(error.getMessage());
    }

    /**
     * 发送短信通知
     * @param machines
     */
    public void send(JSONArray machines) {
        new Netease(machines);
    }

    /**
     * 正则截取IP
     * @param machine
     * @return
     */
    public String regIp(String machine) {
        String ip = null;
        String pattern = "(\\d{1,3}\\.){3}\\d{1,3}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(machine);
        while (m.find()) {
            ip = m.group();
        }
        return ip;
    }

    /**
     * 截取主机名
     * @param machine
     * @return
     */
    public String regName(String machine) {
        String name = "";
        String pattern = "name=(\\S)+\\)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(machine);
        while (m.find()) {
            name = m.group();
        }
        name = name.substring(5, name.length()-1);
        return name;
    }
}
