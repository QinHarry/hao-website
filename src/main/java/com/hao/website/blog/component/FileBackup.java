package com.hao.website.blog.component;

import com.hao.website.blog.service.ISiteService;
import com.hao.website.blog.utils.DateKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class FileBackup implements DisposableBean, Runnable {

    private static final int WAIT_TIME = 1000 * 60 * 60 * 24;

    private Thread thread;

    @Autowired
    private ISiteService siteService;

    public FileBackup() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(WAIT_TIME);
                String time = DateKit.dateFormat(new Date(), "yyyy-MM-dd HH:mm");
                if (siteService.fileBackup(System.getProperty("user.home") + "/backup/")) {
                    log.info("File backup successfully at " + time);
                } else {
                    log.info("File backup fail at " + time);
                }
            }
        } catch (InterruptedException e) {
            log.info("database backup thread interrupted", e);
        }
    }

    @Override
    public void destroy() {

    }
}
