package com.midea.cloud.deploy;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

/**
 * 上传依赖到 Maven 私服
 * org.apache.maven.plugins:maven-deploy-plugin:2.8.2:deploy-file
 * <p>
 * mvn -s F:\.m2\settings.xml
 * -Durl=http://IP:PORT/nexus/content/repositories/thirdpart
 * -DrepositoryId=thirdpart
 * -Dfile=antlr-2.7.2.jar
 * -DpomFile=antlr-2.7.2.pom
 * -Dpackaging=jar
 * -DgeneratePom=false
 * -Dsources=./path/to/artifact-name-1.0-sources.jar
 * -Djavadoc=./path/to/artifact-name-1.0-javadoc.jar
 * <p>
 * public static final String BASE_CMD = "cmd /c mvn " +
 * "-s F:\\.m2\\settings.xml " +
 * "deploy:deploy-file " +
 * "-Durl=http://IP:PORT/nexus/content/repositories/thirdpart " +
 * "-DrepositoryId=thirdpart " +
 * "-DgeneratePom=false";
 */
public class DeployCC {
    /* ======================== *** 首次运行参数修改 start *** ============================= */
    public static final String BASE_CMD = "cmd /c C:\\apache-maven-3.3.9\\bin\\mvn " +
            " -s D:\\develop-cc\\sdk-project-impl-extend-master\\sdk-project-main\\deploy\\src\\main\\resources\\settings_local.xml " +
            " deploy:deploy-file " +
            " -Durl=http://10.168.130.235:8081/repository/%s/ " +
            " -DrepositoryId=%s " +
            " -DgeneratePom=false ";
    public static final String SNAPSHOT_REPOSITORY_ID = "maven-snapshot";
    public static final String RELEASE_REPOSITORY_ID = "maven-release";
    public static final String BASE_SCAN_FILE = "D:\\maven\\product-dev"; // 同步maven的仓库路径
    /* ======================== *** 首次运行参数修改 end *** ============================= */


    public static void main(String[] args) throws InterruptedException {
        DeployService deployService = new DeployService(BASE_CMD, SNAPSHOT_REPOSITORY_ID, RELEASE_REPOSITORY_ID, BASE_SCAN_FILE);
        deployService.run();
    }

}