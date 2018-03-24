package jdk.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * @author zeyu 2018/2/5
 */
public class HotClassLoader extends URLClassLoader {
    public HotClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public HotClassLoader(URL[] urls) {
        super(urls);
    }

    public HotClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }
}
