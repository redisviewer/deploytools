package com.thinkjoy.service;

import com.thinkjoy.common.Constants;
import com.thinkjoy.util.CacheHandle;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by shaojun on 8.21 021.
 */
public abstract class DeployAbstract {

    /**
     * 部署目标路径
     */
    public static String deployTargetPath = CacheHandle.getCache(Constants.DEPLOY_TARGET_PATH);
    /**
     * 部署文件路径
     */
    public static String deploySourcePath = null;
    static final Logger logger = Logger.getLogger(DeployAbstract.class.getName());
    /**
     * 备份目录
     */
    static String backupDir = CacheHandle.getCache(Constants.BACKUP_DIR);

    /**
     * 将日志输出到备份目录
     */
    static {
        System.setProperty("log.dir", backupDir);
        List<String> colorEggs = CacheHandle.<List<String>>getCache(Constants.COLOR_EGGS);
        String logFileName = colorEggs.get(new Random().nextInt(colorEggs.size())) + ".log";
        System.setProperty("log.file", logFileName);
    }

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
            Map<String, File> fileMap = readDeployFiles(deploySourcePath);
            if (!confimContinue())
                return;


            logger.info("\n\n\n\n===================================================== 第二步：备份目标 =======================================================");
            // 输入部署项目路径
            //inputDeployTargetPath();
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
