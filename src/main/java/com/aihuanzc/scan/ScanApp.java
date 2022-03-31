package com.aihuanzc.scan;

/**
 * 扫描器启动类
 */
public class ScanApp {
    public static void main(String[] args) {
        // 待扫描的ip地址或ip地址组
        String ips = null;
        ips = "39.105.7.152";
        // 待扫描的port范围
        String ports = "0-9999";
        Scanner.start(ips, ports);


    }
}
