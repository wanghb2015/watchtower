package com.th.sentry.client;

import com.th.sentry.model.Machine;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author 王虹波
 * @since 1.0, 2019/8/10 15:19
 */
public class Sentry {
    private static final Logger logger = LoggerFactory.getLogger(Sentry.class);
    private static WebSocketClient client;
    private static Machine machine = new Machine();

    public static void main(String[] args) {
        try {
            client = new WebSocketClient(new URI("ws://111.111.111.224:8899/watchtower")) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    logger.info("连接到望楼");
                }

                @Override
                public void onMessage(String s) {
                    logger.info("望楼传讯==>" + s);
                    client.send("喏");
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    logger.info("望楼失守，散班回家");
                }

                @Override
                public void onError(Exception e) {
                    logger.info("omg");
                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        client.connect();
        while (!client.getReadyState().equals(ReadyState.OPEN)) {
            logger.debug("呼叫望楼···");
        }
        client.send(machine.toString());
    }
}
