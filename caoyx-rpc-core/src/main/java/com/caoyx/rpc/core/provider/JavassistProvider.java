package com.caoyx.rpc.core.provider;

import com.caoyx.rpc.core.classloader.SingleClassLoader;
import com.caoyx.rpc.core.utils.ClassUtils;
import com.caoyx.rpc.core.utils.ThrowableUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-20 18:04
 */
@Slf4j
public class JavassistProvider implements MethodProvider {
    private String className;
    private String methodName;
    private String implVersion;
    private Class[] paramTypeNames;
    private Class returnType;
    private Object serviceBean;
    private MethodProvider methodProvider;

    public JavassistProvider(String className, String implVersion, Method method, Object serviceBean) {
        this.className = className;
        this.methodName = method.getName();
        this.implVersion = implVersion;
        this.paramTypeNames = method.getParameterTypes();
        this.returnType = method.getReturnType();
        this.serviceBean = serviceBean;

        try {
            this.methodProvider = generateMethodProvider();
        } catch (Exception e) {
            log.error(ThrowableUtils.throwable2String(e));
        }
    }

    private MethodProvider generateMethodProvider() throws Exception {
        String methodProviderName = "com.github.iancao.caoyxRpc."
                + this.className
                + "." + this.implVersion
                + "." + this.methodName
                + "." + Arrays.hashCode(paramTypeNames);

        // 初始化代理类
        ClassPool classPool = ClassPool.getDefault();
        CtClass methodProviderCtClass = classPool.makeClass(methodProviderName);
        methodProviderCtClass.setInterfaces(new CtClass[]{classPool.getCtClass(MethodProvider.class.getName())});

        // 设置私有成员 service
        CtField serviceField = new CtField(classPool.get(className), "service", methodProviderCtClass);
        serviceField.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
        methodProviderCtClass.addField(serviceField);

        // 添加有参构造函数
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{classPool.get(serviceBean.getClass().getName())}, methodProviderCtClass);
        ctConstructor.setBody("{$0.service = $1;}");
        methodProviderCtClass.addConstructor(ctConstructor);

        // 添加目标方法,并进行强制类型转换
        StringBuilder invokeMethodBuilder = new StringBuilder();
        invokeMethodBuilder.append("public Object invoke(Object[] params){\r\n");

        StringBuilder invokeBuilder = new StringBuilder();
        invokeBuilder.append("  service.").append(methodName).append("(");
        for (int i = 0; i < paramTypeNames.length; i++) {
            Class paramType = paramTypeNames[i];
            if (ClassUtils.isPrimitive(paramType)) {
                invokeBuilder.append(ClassUtils.castPrimivate(paramType.getName(), "params[" + i + "]"));
            } else {
                invokeBuilder.append("(").append(paramType.getName()).append(")").append("params[").append(i).append("]");
            }
            if (i != paramTypeNames.length - 1) {
                invokeBuilder.append(", ");
            }
        }
        invokeBuilder.append(")");

        String boxInvokeBuilder = ClassUtils.box(returnType, invokeBuilder.toString());
        if (ClassUtils.isVoid(returnType)) {
            invokeMethodBuilder.append(boxInvokeBuilder).append(";\r\n");
            invokeBuilder.append("return null");
        } else {
            invokeMethodBuilder.append("return ").append(boxInvokeBuilder).append("\r\n; }");

        }

        CtMethod m = CtNewMethod.make(invokeMethodBuilder.toString(), methodProviderCtClass);
        methodProviderCtClass.addMethod(m);

        byte[] bytes = methodProviderCtClass.toBytecode();

        Class<?> MethodProviderClass = SingleClassLoader.loadClass(serviceBean.getClass().getClassLoader(), bytes);
        return (MethodProvider) MethodProviderClass.getConstructor(serviceBean.getClass()).newInstance(serviceBean);
    }


    @Override
    public Object invoke(Object[] params) {
        return methodProvider.invoke(params);
    }
}