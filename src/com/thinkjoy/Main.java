package com.thinkjoy;

import com.thinkjoy.common.Constants;
import com.thinkjoy.service.DeployAbstract;
import com.thinkjoy.service.DeployImplement;
import com.thinkjoy.util.CacheHandle;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {

        // 执行时传递部署目标路径
        String deployTargetPath = args.length > 0 ? args[0] : null;
        if (StringUtils.isBlank(deployTargetPath)) {
            System.out.println("执行请携带参数：部署目录!");
            System.exit(0);
        } else {
            File exsitFile = new File(deployTargetPath);
            if (!exsitFile.isAbsolute()) {
                System.out.println("部署目录必须是绝对路径！");
                System.exit(0);
            } else if (!exsitFile.exists()) {
                System.out.println("部署目录不存在！");
                System.exit(0);
            } else if (exsitFile.isFile()) {
                System.out.println("部署目录必须是个文件夹！");
                System.exit(0);
            } else if (exsitFile.isDirectory()) {
                System.out.println("部署目录验证成功！");

                // 设置缓存：目标部署目录（绝对路径）
                CacheHandle.setCache(Constants.DEPLOY_TARGET_PATH, deployTargetPath);
            } else {
                System.out.println("部署目录验证失败！");
                System.exit(0);
            }
        }

        // 设置缓存：color eggs
        InputStream inputStream = Main.class.getResourceAsStream("/coloreggs.properties");
        Properties properties = new Properties();
        try {
            List<String> colorEggs = new ArrayList<>();
            properties.load(new InputStreamReader(inputStream, "gbk"));
            for (String key : properties.stringPropertyNames()) {
                colorEggs.add(properties.getProperty(key));
            }
            CacheHandle.setCache(Constants.COLOR_EGGS, colorEggs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 设置缓存：项目名称
        String projectName = new File(deployTargetPath).getName();
        if (StringUtils.isBlank(projectName)) {
            System.out.println("部署目录中获取项目名称失败，很难失败吧！");
            System.exit(0);
        }
        CacheHandle.setCache(Constants.PROJECT_NAME, projectName);

        // 设置缓存：备份目录
        Date executeTime = new Date();
        String[] dateTimes = new SimpleDateFormat("yyyyMMdd HHmmss").format(executeTime).split(" ");
        String backupDir = "backup_" + dateTimes[0] + File.separator + projectName + "_" + dateTimes[1] + File.separator;
        CacheHandle.setCache(Constants.BACKUP_DIR, backupDir);

        // 执行部署
        DeployAbstract executor = new DeployImplement();
        executor.execute();
    }
}
