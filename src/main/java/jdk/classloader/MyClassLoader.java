package jdk.classloader;

import java.io.File;
import java.util.StringTokenizer;

/**
 * @author zeyu 2018/2/5
 */
public class MyClassLoader {
    public static File[] getExtDirs() {
        String var0 = System.getProperty("java.ext.dirs");
        File[] var1;
        if (var0 != null) {
            StringTokenizer var2 = new StringTokenizer(var0, File.pathSeparator);
            int var3 = var2.countTokens();
            var1 = new File[var3];
            for (int var4 = 0; var4 < var3; ++var4) {
                var1[var4] = new File(var2.nextToken());
            }
        } else {
            var1 = new File[0];
        }
        return var1;
    }
}
