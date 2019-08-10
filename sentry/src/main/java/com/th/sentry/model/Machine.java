package com.th.sentry.model;

import lombok.Data;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author 王虹波
 * @since 1.0, 2019/8/10 16:55
 */
@Data
public class Machine {
    private String ip;
    private String name;

    public Machine() {
        InetAddress ia;
        try {
            ia = InetAddress.getLocalHost();
            setName(ia.getHostName());
            setIp(ia.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
