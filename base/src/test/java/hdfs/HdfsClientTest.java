package hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zeyu 2018/1/17
 */
public class HdfsClientTest {
    public String host = "vm253";
    public String realm = "VM.COM";
    //namenode
    public String uri = "hdfs://vm253:9000/";
    public String keyPath = "C:\\Users\\zeyu\\Desktop\\HDFS\\hdfs.keytab";
    public String principal = "hdfs/vm253@VM.COM";
    Configuration conf = new Configuration();

    public void login() throws IOException {
        //可以直接设置以下属性，也可以设置krb5.conf
        System.setProperty("java.security.krb5.realm",realm);
        System.setProperty("java.security.krb5.kdc",host);
        //System.setProperty("java.security.krb5.conf", krbPath);
        //这里设置namenode
        conf.set("fs.defaultFS", uri);
        //需要增加hadoop开启了安全的配置
        conf.setBoolean("hadoop.security.authorization", true);
        //配置安全认证方式为kerberos
        conf.set("hadoop.security.authentication", "Kerberos");
        //设置namenode的principal
        conf.set("dfs.namenode.kerberos.principal", principal);
        //设置datanode的principal值为“hdfs/_HOST@YOU-REALM.COM”
        conf.set("dfs.datanode.kerberos.principal", principal);
        //通过hadoop security下中的 UserGroupInformation类来实现使用keytab文件登录
        UserGroupInformation.setConfiguration(conf);
        //设置登录的kerberos principal和对应的keytab文件，其中keytab文件需要kdc管理员生成给到开发人员
        UserGroupInformation.loginUserFromKeytab(principal, keyPath);
    }

    public void access() throws IOException {
        //hdfs dfs -chmod -R 777 /test
        FileSystem fs = FileSystem.get(conf);
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

    @Test
    public void test1() throws Exception {
        login();
        access();
    }
}