package hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author zeyu 2018/1/17
 */
public class HdfsClientTest {
    public static final String KEYTAB_FILE_KEY = "hdfs.keytab.file";
    public static final String USER_NAME_KEY = "hdfs.kerberos.principal";

    public static void main(String[] args) throws Exception {
        String uri = "hdfs://vm253:9000/";
        Configuration conf = new Configuration();
        //keytab文件的路径
        conf.set(KEYTAB_FILE_KEY, "C:\\Users\\zeyu\\Desktop\\udaldump_test\\hdfs\\op.keytab");
        //principal
        conf.set(USER_NAME_KEY, "hdfs");
        login(conf,conf);
        //指定用户名连接HDFS，以获取linux用户对应的文件访问权限
        //FileSystem fs = FileSystem.get(URI.create(uri), config,"hdfs");
        //或者不使用用户，将hdfs对应的文件目录设置为777以便可以写入
        //hdfs dfs -chmod -R 777 /test
        FileSystem fs = FileSystem.get(URI.create(uri), conf);
        // 列出hdfs上/user/fkong/目录下的所有文件和目录
        FileStatus[] statuses = fs.listStatus(new Path("/"));
        for (FileStatus status : statuses) {
            System.out.println(status);
        }
        // 创建或覆盖一个文件，并写入一行文本
        FSDataOutputStream os = fs.create(new Path("/test/test.log"));
        os.write("Hello World!".getBytes());
        os.flush();
        os.close();
        // 显示文件的内容
        InputStream is = fs.open(new Path("/test/test.log"));
        IOUtils.copyBytes(is, System.out, 1024, true);
    }


    public static void login(Configuration conf, Configuration hdfsConfig) throws IOException {
        if (UserGroupInformation.isSecurityEnabled()) {
            String keytab = conf.get(KEYTAB_FILE_KEY);
            if (keytab != null) {
                hdfsConfig.set(KEYTAB_FILE_KEY, keytab);
            }
            String userName = conf.get(USER_NAME_KEY);
            if (userName != null) {
                hdfsConfig.set(USER_NAME_KEY, userName);
            }
            SecurityUtil.login(hdfsConfig, KEYTAB_FILE_KEY, USER_NAME_KEY);
        }
    }
}