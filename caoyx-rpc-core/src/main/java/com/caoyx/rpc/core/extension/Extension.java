package com.caoyx.rpc.core.extension;

import com.caoyx.rpc.core.enums.ExtensionType;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.annotation.Implement;
import com.caoyx.rpc.core.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-03 12:03
 */
@Slf4j
public class Extension {

    private static final String SPI_DIRECTORY = "META-INF/caoyxRpc/";

    private Class clazz;
    private ExtensionType type;
    private String name;

    @Getter
    private Object validExtensionInstance;


    public Extension(Class clazz, ExtensionType type, String name) throws CaoyxRpcException {
        this.clazz = clazz;
        this.type = type;
        this.name = name;

        Class validClazz = loadValidExtension(clazz, name);
        if (validClazz == null) {
            throw new CaoyxRpcException(clazz.getName() + "| not exist " + name + " class");
        }
        try {
            this.validExtensionInstance = validClazz.newInstance();
        } catch (Throwable e) {
            log.error(validClazz.getName() + " newInstance fail", e);
        }
    }

    private Class loadValidExtension(Class clazz, String name) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        int maxOrder = -1;
        Class maxOrderClazz = null;
        boolean useNameToSelect = StringUtils.isNotBlank(name);
        try {
            String filePath = SPI_DIRECTORY + clazz.getName();

            URL url = classLoader.getResource(filePath);
            List<Class> extensionClasses = loadExtension(url, classLoader);
            if (extensionClasses == null || extensionClasses.isEmpty()) {
                log.error("Fail to load class extension" + clazz.getName());
                return null;
            }
            for (Class extensionClazz : extensionClasses) {
                if (!extensionClazz.isAnnotationPresent(Implement.class)) {
                    log.error("Fail to load class extension, because " + extensionClazz.getName() + " is not annotated with @Implement");
                    return null;
                }
                Implement implement = (Implement) extensionClazz.getAnnotation(Implement.class);
                String extensionName = implement.name();
                if (useNameToSelect) {
                    if (extensionName.equals(name)) {
                        return extensionClazz;
                    }
                    continue;
                }

                int extensionOrder = implement.order();
                if (extensionOrder > maxOrder) {
                    maxOrderClazz = extensionClazz;
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return maxOrderClazz;
    }

    private List<Class> loadExtension(URL url, ClassLoader classLoader) {
        List<Class> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        result.add(Class.forName(line, true, classLoader));
                    } catch (Throwable throwable) {
                        log.error("Fail to load " + line);
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }
}