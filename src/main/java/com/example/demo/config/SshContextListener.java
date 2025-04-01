package com.example.demo.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.stereotype.Component;

@Component
@WebListener
public class SshContextListener implements ServletContextListener {

    private SshConnectionTool conexionssh;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Context initialized ... !");
        try {
            conexionssh = new SshConnectionTool();
        } catch (Throwable e) {
            System.err.println("SSH连接失败，请检查配置或网络！");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Context destroyed ... !");
        if (conexionssh != null) {
            conexionssh.closeSSH();
        }
    }
}
