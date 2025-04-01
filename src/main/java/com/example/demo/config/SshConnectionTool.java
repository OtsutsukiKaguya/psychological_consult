package com.example.demo.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;

public class SshConnectionTool {
    // SSH 登录的用户名
    private static final String SSH_USER = "ubuntu";
    // SSH 密码
    private static final String SSH_PASSWORD = "sldkfjgh";
    // SSH 远程主机地址（不要加用户名！）
    private static final String SSH_REMOTE_SERVER = "1.tcp.vip.cpolar.top";
    // SSH 连接的端口
    private static final int SSH_REMOTE_PORT = 11930;

    // 本地转发的端口（你本地应用连接的）
    private static final int LOCAL_PORT = 23306;
    // 远端数据库服务监听的地址（通常是 localhost）
    private static final String REMOTE_DB_HOST = "localhost";
    // 远端数据库的端口
    private static final int REMOTE_DB_PORT = 53306;

    private Session session;

    public SshConnectionTool() throws Exception {
        JSch jsch = new JSch();
        session = jsch.getSession(SSH_USER, SSH_REMOTE_SERVER, SSH_REMOTE_PORT);
        session.setPassword(SSH_PASSWORD);

        // 关闭 host 校验
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        // 建立 SSH 连接
        session.connect();

        // 设置本地端口转发：将 localhost:3306 映射到远程 localhost:53306
        session.setPortForwardingL(LOCAL_PORT, REMOTE_DB_HOST, REMOTE_DB_PORT);

        System.out.println("SSH Connection established & port forwarded.");
    }

    public void closeSSH() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            System.out.println("SSH Connection closed.");
        }
    }
}
