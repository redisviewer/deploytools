package com.thinkjoy.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by shaojun on 8.21 021.
 */
public class DeployImplement extends DeployAbstract {

    /**
     * 输入部署目标项目路径
     */
    @Override
    public void inputDeployTargetPath() throws IOException {
        logger.info("\n################### 请输入部署[目标]文件夹绝对路径： ###################");

        String inputString = null;
        while (!validPath(inputString)) {
            inputString = inputLine.readLine();
            logger.info("您的输入为:" + inputString);
        }

        deployTargetPath = inputString;
    }

    /**
     * 验证路径
     *
     * @param fileDir
     * @return
     */
    private boolean validPath(String fileDir) {
        if (StringUtils.isBlank(fileDir))
            return false;

        // 判断部署目录与目标路径不能相同
        if (StringUtils.isNotBlank(deployTargetPath) && deployTargetPath.equalsIgnoreCase(fileDir)) {
            logger.error("目标路径与部署文件路径不能一样！\n请重新输入 ：");
            return false;
        }

        boolean result = false;
        File exsitFile = new File(fileDir);
        if (!exsitFile.isAbsolute()) {
            logger.error("输入并不是绝对路径！\n请重新输入 ：");
            return false;
        } else if (!exsitFile.exists()) {
            logger.error("文件夹不存在！\n请重新输入 ：");
        } else if (exsitFile.isFile()) {
            logger.error("输入的并不是个文件夹！\n请重新输入 ：");
        } else if (exsitFile.isDirectory()) {
            result = true;
        }
        return result;
    }

    /**
     * 输入部署文件夹的名称
     */
    @Override
    public void inputDeploySourceName() throws IOException {
        logger.info("\n################### 请输入[部署]文件夹绝对路径：###################");

        String inputString = null;
        while (!validPath(inputString)) {
            inputString = inputLine.readLine();
            logger.info("您的输入为:" + inputString);
        }

        deploySourcePath = inputString;
    }

    /**
     * 读取部署文件
     *
     * @param path
     * @return
     */
    @Override
    public Map<String, File> readDeployFiles(String path) {
        logger.info("################### 读取部署文件： ###################");
        Map<String, File> result = new LinkedHashMap<String, File>();
        if (StringUtils.isNotBlank(path)) {
            File deployDir = new File(path);
            loadFileMap(deployDir, result);
        }
        return result;
    }

    /**
     * 递归读取部署文件
     *
     * @param dir
     * @param result
     */
    private void loadFileMap(File dir, Map<String, File> result) {
        if (dir.isDirectory()) {
            File[] childFiles = dir.listFiles();
            for (File child : childFiles) {
                if(child.isFile())
                    logger.info("读取成功！总数“" + (++deployCount) + "” ： " + child.getPath());

                String relativePath = child.getAbsolutePath().replace(deploySourcePath, File.separator);
                result.put(relativePath, child);

                // 文件夹递归
                if (child.isDirectory()) {
                    loadFileMap(child, result);
                }
            }
        }
    }

    /**
     * 备份目标文件
     *
     * @param deloyDataMap
     */
    @Override
    public void backupTarget(Map<String, File> deloyDataMap) throws IOException {
        logger.info("################### 备份文件： ###################");

        // 执行备份
        for (String path : deloyDataMap.keySet()) {
            File exsitFile = new File(deployTargetPath + path);
            if (exsitFile.exists()) {
                try {
                    File backupFile = new File(backupDir + File.separator + "files" + path);
                    if (!backupFile.getParentFile().exists())
                        backupFile.getParentFile().mkdirs();

                    // 只备份文件
                    if (exsitFile.isFile()){
                        FileUtils.copyFile(exsitFile, backupFile);
                        if(!backupFile.exists()){
                            throw new IOException("备份文件出错！");
                        }
                        logger.info("备份成功(存在文件)！总数“" + (++backupCount) + "” ： " + backupFile.getPath());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("备份文件出错！" + path, e);
                    throw new IOException(e);
                }
            } else {
                logger.info("无需备份(新增文件)！： " + path);
            }
        }
    }

    /**
     * 执行部署
     */
    @Override
    public void executeDeply(Map<String, File> fileMap) throws IOException {
        logger.info("\n\n################### 执行部署： ###################");
        for (String path : fileMap.keySet()) {
            File deployFile = fileMap.get(path);

            // 文件删除，文件夹忽略
            File exsitFile = new File(deployTargetPath + path);
            if (exsitFile.isFile() && exsitFile.exists() && !exsitFile.delete()) {
                logger.error("部署前，删除目标失败！");
                throw new IOException("部署前，删除目标失败！");
            }

            // 复制部署文件至目标目录
            File targetFile = new File(deployTargetPath + path);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            try {
                // 文件夹不存在则创建
                if(deployFile.isDirectory()){
                    if(!targetFile.exists())
                        targetFile.mkdirs();
                    continue;
                }

                // 复制、更新最后修改时间为当前时间
                FileUtils.copyFile(deployFile, targetFile, false);
                logger.info("部署成功！总数“" + ++successCount + "” ： " + deployFile.getPath());


                // 检查是否部署上了
                if (!targetFile.exists()) {
                    logger.error("部署后，文件不存在！  没部署上？");
                    throw new IOException("部署后，文件不存在！  没部署上？");
                }
            } catch (IOException e) {
                logger.error("部署时发生错误！【" + e.getMessage() + "】", e);
                logger.error("source path : " + deployFile.getCanonicalPath());
                logger.error("target path : " + targetFile.getCanonicalPath());
                throw new IOException(e);
            }
        }
    }

    @Override
    boolean confimContinue() throws IOException, InterruptedException {
        logger.info("\n 请仔细检查输入，以及阅读控制台日志，确认无误后继续！y/n ");

        String inputString = inputLine.readLine();
        if ("n".equalsIgnoreCase(inputString)) {
            logger.info("程序正在停止...");
            Thread.sleep(1000);
            return false;
        } else if ("y".equalsIgnoreCase(inputString)) {
            logger.info("程序继续执行！");
            return true;
        } else {
            logger.error("输入无效，请重新输入：");
            return confimContinue();
        }
    }
}
