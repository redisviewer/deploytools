package com.thinkjoy.service;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * Created by shaojun on 8.21 021.
 */
public abstract class DeployAbstract {
    static String[] logFiles = {
            "You can't find me, you can't find me.",
            "Hey guy, look here",
            "Look at me look at me.",
            "too young too simple", // 太年轻太简单
            "U can u up", // 你行你上啊
            "No can no bb", // 不行别BB
            "Down your sister's rain", // 下你妹的雨
            "we two who and who", // 咱俩谁跟谁阿
            "you don’t bird me,I don’t bird you", // 你不鸟我，我也不鸟你
            "you have seed ，I will give you some color to see see", // 你有种，我要给你点颜色看看
            "At KFC, We do Chichen Right", // 在肯德基，我们做鸡是对的
            "You Give Me Stop", // 你给我站住
            "watch sister", // 表妹
            "take iron coffee", // 拿铁咖啡
            "American Chinese not enough", // 美中不足
            "Where cool where you stay", // 哪凉快上哪呆着
            "heart flower angry open", // 心花怒放
            "Hai long is a colour wolf", // 海龙是一条色狼
            "dry goods", // 干货
            "want money no, want life one", // 要钱没有，要命一条
            "People mountain and people sea", // 人山人海
            "you have two down son", // 你有两下子
            "let the horse come on", // 放马过来
            "red face know me", // 红颜知己
            "seven up eight down", // 七上八下
            "no three no four", // 不三不四
            "do morning fuck", // 做早操
            "you try try see", // 你试试看
            "love who who", // 爱谁谁
            "look through autumn water", // 望穿秋水
            "morning three night four", // 朝三暮四
            "king eight eggs", // 王八蛋
            "no care three seven twenty one", // 不管三七二十一
            "go and look", // 走着瞧
            "poor light egg", // 穷光蛋
            "ice snow clever", // 冰雪聪明
            "first see you，i shit love you", //第一次见你，我便爱上了你
            "horse horse tiger tiger", // 马马虎虎
            "no money no talk" // 没钱免谈
    };

    /**
     * 备份目录
     */
    static String backupDir = null;

    /**
     * 将日志输出到备份目录
     */
    private static Date executeTime = null;
    private static String executeTimeFormat = null;

    static {
        executeTime = new Date();
        executeTimeFormat = new SimpleDateFormat("yyyyMMdd_HHmmss").format(executeTime);
        backupDir = "backup_" + executeTimeFormat;
        System.setProperty("log.dir", backupDir);
        System.setProperty("log.file", logFiles[new Random().nextInt(logFiles.length)] + ".log");
    }

    static final Logger logger = Logger.getLogger(DeployAbstract.class.getName());

    /**
     * 部署目标路径
     */
    public static String deplyTargetPath = null;
    static {
        executeTime = new Date();
        executeTimeFormat = new SimpleDateFormat("yyyyMMdd_HHmmss").format(executeTime);
        backupDir = "backup_" + executeTimeFormat;
        System.setProperty("log.dir", backupDir);
        System.setProperty("log.file", logFiles[new Random().nextInt(logFiles.length)] + ".log");
    }

    /**
     * 部署文件路径
     */
    public static String deplySourcePath = null;

    /**
     * 部署的文件个数
     */
    public int deployCount = 0;

    /**
     * 部署成功个数
     */
    public int successCount = 0;

    /**
     * 备份文件的个数
     */
    public int backupCount = 0;

    /**
     * 用户输入读取
     */
    BufferedReader inputLine = new BufferedReader(new InputStreamReader(System.in));

    /**
     * 输入目标项目路径
     */
    public abstract void inputDeployTargetPath() throws IOException;

    /**
     * 输入部署文件夹名称
     */
    public abstract void inputDeploySourceName() throws IOException;

    /**
     * 读取需要部署的文件信息
     *
     * @param path
     * @return
     */
    public abstract Map<String, File> readDeployFiles(String path);

    /**
     * 备份目标文件
     *
     * @param deloyDataMap 部署信息Map
     */
    public abstract void backupTarget(Map<String, File> deloyDataMap) throws IOException;

    /**
     * 确认是否继续提示
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    abstract boolean confimContinue() throws IOException, InterruptedException;

    /**
     * 执行部署
     */
    public abstract void executeDeply(Map<String, File> fileMap) throws IOException;

    public void execute() {
        logger.info("=======================================================================================");
        logger.info("======================================= start =========================================");
        logger.info("=======================================================================================");

        try {

            logger.info("\n\n\n\n===================================================== 第一步：读取部署文件 =======================================================");
            // 输入部署文件名称
            inputDeploySourceName();
            // 扫描部署文件、并且读取进缓存中
            Map<String, File> fileMap = readDeployFiles(deplySourcePath);
            if (!confimContinue())
                return;


            logger.info("\n\n\n\n===================================================== 第二步：备份目标 =======================================================");
            // 输入部署项目路径
            inputDeployTargetPath();
            // 备份文件
            backupTarget(fileMap);
            if (!confimContinue())
                return;


            logger.info("\n\n\n\n===================================================== 第三步：执行部署 =======================================================");
            // 执行部署
            executeDeply(fileMap);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("执行过程中出现异常！", e);
        } finally {

            // 结束信息，日志打印
            if (successCount == deployCount) {
                logger.info("\n\n################################ 部署成功！ ################################");
            } else {
                logger.error("\n\n################################ 异常！！部署的数量与成功的数量不符！ ################################");
            }
            logger.info("读取部署文件数量：" + deployCount);
            logger.info("成功部署文件数量：" + successCount);
            logger.info("备份目标文件数量：" + backupCount);

            logger.info("=======================================================================================");
            logger.info("======================================== end ==========================================");
            logger.info("=======================================================================================");

            // 关闭资源
            if (inputLine != null) {
                try {
                    inputLine.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
