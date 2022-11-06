package com.fyp.searcher.util;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class SingletonWebSocket{
    // Static variable reference of single_instance
    // of type Singleton
    private static Socket webSocket = null;

    // Static method
    // Static method to create instance of Singleton class
    public static Socket getInstance() throws URISyntaxException {
        if (webSocket == null) {
            String url = "http://127.0.0.1:8000";
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"};
            //失败重试次数
            options.reconnectionAttempts = 10;
            //失败重连的时间间隔
            options.reconnectionDelay = 1000;
            //连接超时时间(ms)
            options.timeout = 500;

            webSocket = IO.socket(url, options);
        }
        return webSocket;
    }
}
