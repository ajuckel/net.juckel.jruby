package net.juckel.jruby.osgi.extender;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import net.juckel.jruby.osgi.internal.Activator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.log.LogService;


public class JRubyGemExtender implements SynchronousBundleListener {
    private static final String MANIFEST_HEADER = "JRuby-GemPath";
    private static final String[] GEM_DIRS = { "cache", "gems", "bin", "specifications" };

    @Override
    public void bundleChanged(BundleEvent event) {
        switch(event.getType()) {
        case BundleEvent.STARTING:
            ensureGemsAvailable(event.getBundle());
            break;
        case BundleEvent.UNINSTALLED:
            removeAvailableGems(event.getBundle());
            break;
        }
    }

    public void ensureGemsAvailable(Bundle b) {
        String value = b.getHeaders().get(MANIFEST_HEADER);
        if (null != value) {
            setupRubyGems(b, value);
        }
    }

    public void removeAvailableGems(Bundle b) {
        File explodedGemPath = explodedGemPath(b);
        deleteRecursive(explodedGemPath);
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteRecursive(f);
            }
        }
        if (!file.delete()) {
            
        }
    }

    private File explodedGemPath(Bundle b) {
        Bundle extenderBundle = FrameworkUtil.getBundle(JRubyGemExtender.class);
        return extenderBundle.getDataFile("gems/" + b.getSymbolicName() + "_" + b.getVersion().toString());
    }

    private void setupRubyGems(Bundle b, String pathSpec) {
        String[] paths = pathSpec.split(",");
        List<String> directories = new ArrayList<String>(paths.length * 4);
        for (String path : paths) {
            for (String gemDir : GEM_DIRS) {
                directories.add(path + gemDir);
            }
        }
        File explodedGemPath = explodedGemPath(b);
        explodeEntries(b, explodedGemPath, directories.toArray(new String[directories.size()]));

        String gemPath = System.getenv().get("GEM_PATH");
        if (gemPath == null || gemPath.isEmpty()) {
            gemPath = explodedGemPath.getAbsolutePath(); 
        } else {
            gemPath = explodedGemPath + File.separator + gemPath;
        }
        forceEnv("GEM_PATH", gemPath);
    }

    private static void forceEnv(String envVarName, String envVarValue) {
        Map<String, String> systemEnv = System.getenv();
        // We don't want the UnmodifiableMap, we want the underlying (modifiable) map
        Class<?> clazz = systemEnv.getClass();
        Field m;
        try {
            m = clazz.getDeclaredField("m");
            // You'll run into problems if you have a security manager in place.
            m.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, String> modifiableEnv = (Map<String, String>)m.get(systemEnv);
            modifiableEnv.put(envVarName, envVarValue);
        } catch (Exception e) {
            // Just do nothing if we failed
            e.printStackTrace();
        }
    }

    private void explodeEntries(Bundle b, File explodedGemPath, String... paths) {
        if (!explodedGemPath.exists()) {
            if (! explodedGemPath.mkdirs()) {
                Activator.getDefault().log(LogService.LOG_ERROR, "Could not create directories " + explodedGemPath);
            }
            for(String path : paths) {
                int delimiterIndex = path.indexOf('!');
                if(delimiterIndex >= 0) {
                    String jarLocation = path.substring(0, delimiterIndex);
                    String entryName = path.substring(delimiterIndex+1);
                    URL url = b.getEntry(jarLocation);
                    JarInputStream jar = null;
                    try {
                        jar = new JarInputStream(url.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (jar != null) {
                        saveEntriesFromJar(jar, entryName, explodedGemPath);
                    }
                }
            }
        }
    }
    
    private static void saveEntriesFromJar(JarInputStream jar, String entryName, File destination) {
        JarEntry entry = null;
        try {
            while ((entry = jar.getNextJarEntry()) != null) {
                if (entry.getName().startsWith(entryName)) {
                    if (entry.isDirectory()) {
                        File dir  = new File(destination, entry.getName());
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                    } else {
                        File file = new File(destination, entry.getName());
                        if (file.createNewFile()) {
                            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                            int bufferSize = 1024;
                            byte[] buffer = new byte[bufferSize];
                            long bytesRead = 0;
                            long bytesToRead = entry.getSize();
                            while(bytesRead < bytesToRead) {
                                long bytesLeft = bytesToRead - bytesRead;
                                int readSize = bufferSize;
                                if (bytesLeft < bufferSize) {
                                    readSize = (int) bytesLeft; // less than bufferSize, so must fit in an int
                                }
                                readSize = jar.read(buffer, 0, readSize);
                                bytesRead += readSize;
                                os.write(buffer, 0, readSize);
                            }
                            os.close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
