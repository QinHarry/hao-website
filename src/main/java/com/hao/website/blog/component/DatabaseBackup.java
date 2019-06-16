package com.hao.website.blog.component;

import com.hao.website.blog.service.ISiteService;
import com.hao.website.blog.utils.DateKit;
import com.hao.website.blog.utils.TaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@Component
public class DatabaseBackup implements DisposableBean, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackup.class);

    private Thread thread;

    private static final int WAIT_TIME = 1000 * 60 * 60 * 24;

    @Autowired
    private ISiteService siteService;

    public DatabaseBackup() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(WAIT_TIME);
                String time = DateKit.dateFormat(new Date(), "yyyy-MM-dd HH:mm");
                if (siteService.databaseBackup(System.getProperty("user.home") + "/backup/")) {
                    logger.info("Database backup successfully at " + time);
                } else {
                    logger.info("Database backup fail at " + time);
                }
            }
        } catch (InterruptedException e) {
            logger.info("database backup thread interrupted", e);
        }


    }

    @Override
    public void destroy() {
    }
}
