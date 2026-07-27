package com.midea.cloud.deploy;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

/**
 * @author huanglj50
 * @since 1.0
 */
public class DeployService {

    public String BASE_CMD;
    public String SNAPSHOT_REPOSITORY_ID;
    public String RELEASE_REPOSITORY_ID;
    public String BASE_SCAN_FILE; // 同步maven的仓库路径

    public DeployService(String BASE_CMD, String SNAPSHOT_REPOSITORY_ID, String RELEASE_REPOSITORY_ID, String BASE_SCAN_FILE) {
        this.BASE_CMD = BASE_CMD;
        this.SNAPSHOT_REPOSITORY_ID = SNAPSHOT_REPOSITORY_ID;
        this.RELEASE_REPOSITORY_ID = RELEASE_REPOSITORY_ID;
        this.BASE_SCAN_FILE = BASE_SCAN_FILE;

    }



    public static final Pattern DATE_PATTERN = Pattern.compile("-[\\d]{8}\\.[\\d]{6}-");

    public static final Runtime CMD = Runtime.getRuntime();

    public static final Writer ERROR;

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(10); // 10并发

    static {
        Writer err = null;
        try {
            err = new OutputStreamWriter(new FileOutputStream("deploy-error.log"), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        ERROR = err;
    }

    public void run() throws InterruptedException {
        System.err.println("////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n" +
                "//          首次运行检查如下事项：                                                                                     //\n" +
                "//          1、检查BASE_CMD变量nexus私仓地址【-Durl=xx】及指向本地setting.xml文件【-s xx】 是否修改?                        //\n" +
                "//          2、检查BASE_SCAN_FILE变量即上传同步的文件夹路径是否修改?                                                      //\n" +
                "//          3、检查RELEASE_REPOSITORY_ID和SNAPSHOT_REPOSITORY_ID变量对应nexus仓库的id是否修改【一般该项不需要改动】?         //\n" +
                "//             3.1 RELEASE_REPOSITORY_ID   ==>  jar上传release仓库ID                                                //\n" +
                "//             3.2 SNAPSHOT_REPOSITORY_ID  ==>  jar上传snapshot仓库ID                                               //\n" +
                "//          4、BASE_CMD变量 -s 指向的setting.xml文件模板可参考工程中的settings_local.xml文件                              //\n" +
                "//             4.1 <servers>   ==>  修改该标签下对应nexus仓库ID的账号密码                                               //\n" +
                "//          5、详细错误日志会打印到工程主目录下的【deploy-error.log】文件                                                  //\n" +
                "////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        System.out.println("============= 文件上传开始 =============");
        long start = System.currentTimeMillis();
        if (checkBaseScanFile()) {
            File file = new File(BASE_SCAN_FILE);
            deploy(file.listFiles());
        }
        EXECUTOR_SERVICE.shutdown();
        while (true) {
            ThreadPoolExecutor currentThreadPoolExecutor = ((ThreadPoolExecutor) EXECUTOR_SERVICE);
            long taskCount = currentThreadPoolExecutor.getTaskCount();
            long completedTaskCount = currentThreadPoolExecutor.getCompletedTaskCount();
            long queueSize = currentThreadPoolExecutor.getQueue().size();
            System.out.println("线程池总数: " + taskCount + "\n" +
                    "已完成的任务数量: " + completedTaskCount + "\n" +
                    "等待队列中的任务数量: " + queueSize);
            if (taskCount == completedTaskCount || queueSize == 0) {
                long end = System.currentTimeMillis();
                System.out.println("============= 文件上传结束, 耗时: " + (end - start) + "毫秒 =============");
                Thread.sleep(60000); // 60秒后任务强制退出
                EXECUTOR_SERVICE.shutdownNow();
                break;
            }
            Thread.sleep(30000); // 30秒进行一次任务状态打印
        }
    }

    public void error(String error, String detailError) {
        try {
            System.err.println(error);
            ERROR.write(error + detailError + "\n");
            ERROR.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkBaseScanFile() {
        File file = new File(BASE_SCAN_FILE);
        if (!file.exists()) {
            System.out.println(BASE_SCAN_FILE + " 目录不存在!");
            return false;
        }
        if (!file.isDirectory()) {
            System.out.println(BASE_SCAN_FILE + " 不是目录，必须指定为目录!");
            return false;
        }
        return true;
    }

    public void deploy(File[] files) {
        if (files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deploy(file.listFiles());
            }
        }
        boolean isSnapshot = false;
        if (files[0].isFile()) {
            File pom = null;
            File jar = null;
            File source = null;
            File javadoc = null;
            //忽略日期快照版本，如 xxx-mySql-2.2.6-20170714.095105-1.jar
            for (File file : files) {
                String name = file.getName();
                if (name.contains("SNAPSHOT")) { // SNAPSHOT.jar 上传到 SNAPSHOT仓库
                    isSnapshot = true;
                }
                if (DATE_PATTERN.matcher(name).find()) {
                    //skip SNAPSHOT
                    isSnapshot = true;
                    continue;
                } else if (name.endsWith(".pom")) {
                    pom = file;
                } else if (name.contains("-javadoc") && name.endsWith(".jar")) {
                    javadoc = file;
                } else if (name.contains("-sources") && name.endsWith(".jar")) {
                    source = file;
                } else if (name.endsWith(".jar")) {
                    jar = file;
                }
            }
            if (pom != null) {
                if (jar != null) {
                    deploy(isSnapshot, pom, jar, source, javadoc);
                } else if (packingIsPom(pom)) {
                    deployPom(isSnapshot, pom);
                }
            }
        }
    }

    public boolean packingIsPom(File pom) {
        BufferedReader reader = null;
        try {
            reader =
                    new BufferedReader(new InputStreamReader(new FileInputStream(pom)));
            String line;
            boolean existsPackaging = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().indexOf("<packaging>") != -1) {
                    existsPackaging = true;
                }
                if (line.trim().indexOf("<packaging>pom</packaging>") != -1) {
                    return true;
                }
            }
            // 不存在packaging，则默认识别为pom类型
            if (!existsPackaging) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    public void deployPom(boolean isSnapshot, final File pom) {
        EXECUTOR_SERVICE.execute(new Runnable() {
            @Override
            public void run() {
                StringBuffer cmd = new StringBuffer(isSnapshot ? String.format(BASE_CMD, SNAPSHOT_REPOSITORY_ID, SNAPSHOT_REPOSITORY_ID) : String.format(BASE_CMD, RELEASE_REPOSITORY_ID, RELEASE_REPOSITORY_ID));
                cmd.append(" -DpomFile=").append(pom.getAbsolutePath());
                cmd.append(" -Dfile=").append(pom.getAbsolutePath());
                cmd.append(" -Dpackaging=pom");
                StringBuffer logBuffer = new StringBuffer();
                try {
                    final Process proc = CMD.exec(cmd.toString(), null, pom.getParentFile());
                    InputStream inputStream = proc.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String line;
                    logBuffer.append("\n" + cmd + "\n\n==================================\n");
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("[INFO]") || line.startsWith("Upload") || line.startsWith("[ERROR]")) {
                            logBuffer.append(Thread.currentThread().getName() + " : " + line + "\n");
                        }
                    }
                    int result = proc.waitFor();
                    if (result != 0) {
                        error("上传失败：" + pom.getAbsolutePath(), logBuffer.toString());
                    } else {
                        System.out.println("上传成功：" + pom.getAbsolutePath());
                    }
                } catch (Exception e) {
                    error(cmd.toString(), "");
                    error("上传失败：" + pom.getAbsolutePath(), e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public void deploy(boolean isSnapshot, final File pom, final File jar, final File source, final File javadoc) {
        EXECUTOR_SERVICE.execute(new Runnable() {
            @Override
            public void run() {
                StringBuffer cmd = new StringBuffer(isSnapshot ? String.format(BASE_CMD, SNAPSHOT_REPOSITORY_ID, SNAPSHOT_REPOSITORY_ID) : String.format(BASE_CMD, RELEASE_REPOSITORY_ID, RELEASE_REPOSITORY_ID));
                cmd.append(" -DpomFile=").append(pom.getAbsolutePath());

                if (jar != null) {
                    //当有bundle类型时，下面的配置可以保证上传的jar包后缀为.jar
                    cmd.append(" -Dpackaging=jar -Dfile=").append(jar.getAbsolutePath());
                    if (!jar.getName().endsWith(jar.getParentFile().getName() + ".jar")) {
                        String versionFix = jar.getParentFile().getName() + "-";
                        String classiferName = jar.getName().substring(jar.getName().lastIndexOf(versionFix) + versionFix.length(), jar.getName().lastIndexOf("."));
                        cmd.append(" -Dclassifier=" + classiferName);
                        System.err.println("上传警告：" + pom.getAbsolutePath() + "存在classifier, classifierName=" + classiferName);
                    }
                } else {
                    cmd.append(" -Dfile=").append(pom.getAbsolutePath());
                }

                if (source != null) {
                    cmd.append(" -Dsources=").append(source.getAbsolutePath());
                }
                if (javadoc != null) {
                    cmd.append(" -Djavadoc=").append(javadoc.getAbsolutePath());
                }
                try {
                    System.out.println(cmd.toString());
                    final Process proc = CMD.exec(cmd.toString(), null, pom.getParentFile());
                    InputStream inputStream = proc.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String line;
                    StringBuffer logBuffer = new StringBuffer();
                    logBuffer.append("\n" + cmd + "\n\n==================================\n");
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("[INFO]") || line.startsWith("Upload") || line.startsWith("[ERROR]")) {
                            logBuffer.append(Thread.currentThread().getName() + " : " + line + "\n");
                        }
                    }
                    int result = proc.waitFor();
                    if (result != 0) {
                        error("上传失败：" + pom.getAbsolutePath(), logBuffer.toString());
                    } else {
                        System.out.println("上传成功：" + pom.getAbsolutePath());
                    }
                } catch (Exception e) {
                    error(cmd.toString(), "");
                    error("上传失败：" + pom.getAbsolutePath(), e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

}
