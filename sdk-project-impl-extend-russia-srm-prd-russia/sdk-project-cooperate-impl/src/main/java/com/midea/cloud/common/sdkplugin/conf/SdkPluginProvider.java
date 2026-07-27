package com.midea.cloud.common.sdkplugin.conf;

import com.midea.cloud.common.sdkplugin.ISdkFunctionPlugin;
import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.sdkplugin.conf.properties.SdkPluginEnvSet;
import com.midea.cloud.common.sdkplugin.conf.properties.SdkPluginProperties;
import com.midea.cloud.common.sdkplugin.conf.properties.SdkPluginSet;
import com.midea.cloud.common.utils.AssertUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.Advised;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * sdk插件配置解析
 * PS: 参考 mql - SchemaProviderV2
 *
 * @author zhangwk12@meicluod.com
 */
@Slf4j
public class SdkPluginProvider {

    private final Map<Class<? extends ISdkPlugin>, SdkPluginSet<? extends ISdkPlugin>> PLUGIN_MAP = new HashMap<>(1024);

    /**
     * 从 sdk plugin 的json配置文件中加载数据
     */
    public void loadSdkPlugins(SdkPluginProperties properties) {
        // 1: 从配置文件中获取配置信息
        this.loadSdkPluginsFromEnvs(properties);
        // 2: 从sprint容器中获取配置信息(根据 ISdkPlugin.getOrder 来判断实现类优先级; 如果json配置文件中已描述插件，则不考虑从容器中获取了)
        this.loadSdkPluginsFromBeans();
    }

    private void loadSdkPluginsFromEnvs(SdkPluginProperties properties) {
        if (CollectionUtils.isEmpty(properties.getProduct()) && CollectionUtils.isEmpty(properties.getIndustry()) && CollectionUtils.isEmpty(properties.getExtend())) { return; }

        // 1: 处理二开配置定义
        this.doLoadEnvs(properties.getExtend(), SdkPluginProperties.PLUGIN_TYPE_EXTEND);
        // 2: 处理行业包配置定义
        this.doLoadEnvs(properties.getIndustry(), SdkPluginProperties.PLUGIN_TYPE_INDUCTRY);
        // 3: 处理产品配置定义
        this.doLoadEnvs(properties.getProduct(), SdkPluginProperties.PLUGIN_TYPE_PRODUCT);
    }

    private void doLoadEnvs(List<SdkPluginEnvSet> envs, String pluginType) {
        if (CollectionUtils.isEmpty(envs)) { return; }

        for (SdkPluginEnvSet env : envs) {
            // 1.1: 插件声明
            env.setPlugin(StringUtils.trimToNull(env.getPlugin()));
            AssertUtils.notNull(env.getPlugin(), "sdk-plugin:配置文件定义错误:[sdk.plugins.{0}.plugin]未描述", pluginType);
            Class<ISdkPlugin> pluginClass = getPluginByClassName(env.getPlugin());

            //noinspection unchecked
            SdkPluginSet<ISdkPlugin> pluginSet = (SdkPluginSet<ISdkPlugin>) PLUGIN_MAP.computeIfAbsent(pluginClass, k -> new SdkPluginSet<>());
            pluginSet.setPlugin(pluginClass);

            // 1.2: 场景实现类定义
            AssertUtils.notEmpty(env.getScene(), "sdk-plugin:配置文件定义错误:[sdk.plugins.{0}.scene]未描述", pluginType);
            if (pluginSet.getScene() == null) {
                pluginSet.setScene(new HashMap<>(env.getScene().size() << 2));
            }
            env.getScene().forEach((scene, impls) -> {
                scene = StringUtils.trimToNull(scene);
                AssertUtils.notNull(scene, "sdk-plugin:配置文件定义错误:[sdk.plugins.{0}.scene]未描述具体的scene", pluginType);
                if (impls != null) {
                    impls = impls.stream().map(StringUtils::trimToNull).filter(Objects::nonNull).collect(Collectors.toList());
                }
                if (impls == null || impls.isEmpty()) {
                    throw new IllegalArgumentException(MessageFormat.format("sdk-plugin:配置文件定义错误:[sdk.plugins.{0}.scene]未描述scene的实现类", pluginType));
                }

                List<SdkPluginSet.SdkPluginType<? extends ISdkPlugin>> list = pluginSet.getScene().computeIfAbsent(scene, k->  new ArrayList<>(8));
                Set<String> implNames = new HashSet<>(impls.size());
                for (String impl : impls) {
                    if (!implNames.add(impl)) { continue; }
                    list.add(new SdkPluginSet.SdkPluginType<>(getPluginImplByClassName(impl, pluginClass, env.getPlugin()), pluginType));
                }
            });

            // 1.3: 方法级别强制指定实现类
            Set<String> pluginMethods; {
                Method[] methods = pluginSet.getPlugin().getDeclaredMethods();
                pluginMethods = new HashSet<>(methods.length);
                for (Method method : methods) {
                    pluginMethods.add(method.getName());
                }
            }
            if (env.getSolid() != null && !env.getSolid().isEmpty()) {
                if (pluginSet.getSolid() == null) {
                    pluginSet.setSolid(new HashMap<>(env.getSolid().size()));
                }
                env.getSolid().forEach((scene, implMethods) -> {
                    scene = StringUtils.trimToNull(scene);
                    AssertUtils.notNull(scene, "sdk-plugin:配置文件定义错误:[sdk.plugins.{0}.solid]未描述具体的scene", pluginType);
                    AssertUtils.notEmpty(implMethods, "sdk-plugins:配置文件定义错误:[sdk.plugins.{0}.solid]未描述scene的方法说明", pluginType);

                    Map<String/* methodName */, Class<? extends ISdkPlugin>> methodMap = pluginSet.getSolid().computeIfAbsent(scene, k -> new HashMap<>(16));
                    Set<String> methodNames = new HashSet<>(16);
                    implMethods.forEach((methodName, impl) -> {
                        methodName = StringUtils.trimToNull(methodName);
                        AssertUtils.notNull(methodName, "sdk-plugin:配置文件定义错误:[sdk.plugins.{0}.solid]未描述方法名", pluginType);
                        if (methodNames.add(methodName)) {
                            impl = StringUtils.trimToNull(impl);
                            AssertUtils.notNull(impl, "sdk-plugin:配置文件定义错误:[sdk.plugins.{0}.solid]未描述方法实现类", pluginType);

                            AssertUtils.isTrue(pluginMethods.contains(methodName), "sdk-plugin:配置文件定义错误:[sdk.plugin.{0}.solid]定义的方法名{1}在插件接口{2}中不存在",
                                    pluginType, methodName, env.getPlugin());

                            methodMap.put(methodName, getPluginImplByClassName(impl, pluginSet.getPlugin(), env.getPlugin()));
                        }
                    });
                });
            } else {
                pluginSet.setSolid(Collections.emptyMap());
            }
        }
    }

    private void loadSdkPluginsFromBeans() {
        List<ISdkPlugin> sdkPluginList; {
            Collection<ISdkPlugin> beanList = SdkPluginProxy.getBeans(ISdkPlugin.class).values();
            // 如果拿到的bean属于代理类，需要拿到对应的实际类才行
            sdkPluginList = beanList.stream()
                    .map(bean -> {
                        if (bean instanceof Advised) {
                            try {
                                Object obj = ((Advised)bean).getTargetSource().getTarget();
                                AssertUtils.notNull(obj, "代理对象" + bean.getClass().getName() + "拿到的真实对象是空的");
                                return (ISdkPlugin) obj;
                            } catch (IllegalArgumentException e) {
                                throw e;
                            } catch (Exception e) {
                                throw new IllegalArgumentException("无法拿到代理对象" + bean.getClass().getName() + "的真实对象");
                            }
                        } else {
                            return bean;
                        }
                    })
                    .collect(Collectors.toList());
        }
        if (sdkPluginList.isEmpty()) { return; }

        // 1: 获取bean对象类所继承的接口(extend ISdkPlugin的接口定义)
        List<Class<? extends ISdkPlugin>> interfaceList = new ArrayList<>(sdkPluginList.size() << 2);
        sdkPluginList.forEach(sdkPlugin -> {
            // 如果指定接口，在前面的配置文件中已被加载，则忽略掉(配置文件加载的优先)
            Class<? extends ISdkPlugin> pluginClass = getSdkPluginInterfacesFromObj(sdkPlugin);
            if (!PLUGIN_MAP.containsKey(pluginClass)) {
                interfaceList.add(pluginClass);
            }
        });
        if (interfaceList.isEmpty()) { return; }

        // 2: 确定每个接口的实现类信息
        extracted(sdkPluginList, interfaceList);
    }

    /**
     * 确定每个接口的实现类信息
     * @param sdkPluginList 参数
     * @param interfaceList 参数
     */
    private void extracted(List<ISdkPlugin> sdkPluginList, List<Class<? extends ISdkPlugin>> interfaceList) {
        interfaceList.forEach(sdkPlugin -> {
            List<ISdkPlugin> list = sdkPluginList.stream()
                    .filter(impl -> sdkPlugin.isAssignableFrom(impl.getClass())) // 实现了该接口
                    .collect(Collectors.toList());

            SdkPluginSet<ISdkPlugin> pluginSet = new SdkPluginSet<>();
            PLUGIN_MAP.put(sdkPlugin, pluginSet);
            //noinspection unchecked
            pluginSet.setPlugin((Class<ISdkPlugin>)sdkPlugin);
            // 场景处理
            pluginSet.setScene(new HashMap<>(16)); {
                boolean isDefaultMatchAllScene = list.get(0).isDefaultMatchAllScene();
                List<ISdkPlugin> defaultPlugins = new ArrayList<>(8);
                Map<String/* scene */, List<ISdkPlugin>> scenePluginMap = new HashMap<>(list.size());
                list.forEach(plugin -> {
                    scenePluginMap.computeIfAbsent(plugin.matchScene(), k -> new ArrayList<>(5))
                            .add(plugin);
                    if (isDefaultMatchAllScene && ISdkPlugin.DEFAULT_SCENE.equals(plugin.matchScene())) {
                        defaultPlugins.add(plugin);
                    }
                });
                defaultPlugins.sort(Comparator.comparing(ISdkPlugin::getOrder).reversed()); {
                    Map<Integer, ISdkPlugin> sortIndexMap = new HashMap<>(defaultPlugins.size());
                    defaultPlugins.forEach(plugin -> {
                        ISdkPlugin lastPlugin = sortIndexMap.put(plugin.getOrder(), plugin);
                        if (lastPlugin != null) {
                            throw new IllegalArgumentException("sdk-plugin:插件[" + lastPlugin.getClass().getSimpleName() + "]与[" + plugin.getClass().getSimpleName() + "]的优先级相同，这是不合法的");
                        }
                    });
                }
                for (Map.Entry<String/* scene */, List<ISdkPlugin>> entry : scenePluginMap.entrySet()) {
                    Map<Integer, ISdkPlugin> sortIndexMap = new HashMap<>(entry.getValue().size());
                    entry.getValue().sort(Comparator.comparing(ISdkPlugin::getOrder).reversed());
                    entry.getValue().forEach(plugin -> {
                        ISdkPlugin lastPlugin = sortIndexMap.put(plugin.getOrder(), plugin);
                        if (lastPlugin != null) {
                            throw new IllegalArgumentException("sdk-plugin:插件[" + lastPlugin.getClass().getSimpleName() + "]与[" + plugin.getClass().getSimpleName() + "]的优先级相同，这是不合法的");
                        }
                    });
                    entry.getValue().addAll(defaultPlugins);
                }

                // 排序处理，非"default"的排前面，并按照order顺序排列，"default"的排后面，并按照order顺序排列
                scenePluginMap.forEach((scene, plugins) -> {
                    plugins = plugins.stream()
                            .sorted((o1, o2) -> {
                                long o1Weight = 0, o2Weight = 0;
                                long heightWeight = Long.MAX_VALUE;
                                if (!ISdkPlugin.DEFAULT_SCENE.equals(o1.matchScene())) {
                                    o1Weight += heightWeight;
                                } else {
                                    o1Weight += heightWeight >>> 3;
                                }
                                if (!ISdkPlugin.DEFAULT_SCENE.equals(o2.matchScene())) {
                                    o2Weight += heightWeight;
                                } else {
                                    o2Weight += heightWeight >>> 3;
                                }
                                o1Weight += o1.getOrder();
                                o2Weight += o2.getOrder();
                                // 处理long值到顶溢出成负数的情况
                                if (o1Weight < 0) {
                                    o1Weight = Long.MAX_VALUE;
                                } else {
                                    o1Weight -= 1;
                                }
                                if (o2Weight < 0) {
                                    o2Weight = Long.MAX_VALUE;
                                } else {
                                    o2Weight -= 1;
                                }

                                return Math.negateExact(Long.compare(o1Weight, o2Weight));
                            })
                            .collect(Collectors.toList());

                    for (ISdkPlugin plugin : plugins) {
                        pluginSet.getScene().computeIfAbsent(scene, k -> new ArrayList<>(10))
                                .add(new SdkPluginSet.SdkPluginType<>(plugin.getClass(), "非json创建"));
                    }
                });
            }

            pluginSet.setSolid(Collections.emptyMap());
        });
    }

    private Class<ISdkPlugin> getPluginByClassName(String pluginClassName) {
        log.info("二开sdkPlugin生效");
        @SuppressWarnings("rawtypes") Class clazz;
        try {
            clazz = Class.forName(pluginClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("sdk-plugin:配置文件定义错误:'plugin'对应的接口无法找到:" + pluginClassName);
        }

        AssertUtils.isTrue(clazz.isInterface(), "sdk-plugin:配置文件定义错误:'plugin'定义的类必须定义为接口", pluginClassName);
        AssertUtils.isTrue(ISdkPlugin.class.isAssignableFrom(clazz), "sdk-plugin:配置文件定义错误:'plugin'定义的接口必须继承ISdkPlugin" + pluginClassName);

        // 确保接口中不存在方法重载
        Method[] methods = clazz.getDeclaredMethods();
        Set<String> methodSet = new HashSet<>(methods.length);
        for (Method method : methods) {
            AssertUtils.isTrue(methodSet.add(method.getName()), "sdk-plugin:'plugin'定义的接口不允许出现方法重载:" + pluginClassName);
        }

        // 确保接口中的方法均标记为 default，尽量不要让实现类必须实现某个方法
        for (Method method : methods) {
            AssertUtils.isTrue(method.isDefault(), "sdk-plugin:'plugin'定义的接口中所有方法必须为default:" + pluginClassName);
        }

        //noinspection unchecked
        return clazz;
    }

    private Class<ISdkPlugin> getPluginImplByClassName(String implClassName, Class<? extends ISdkPlugin> pluginClass, String pluginClassName) {
        log.info("二开sdkPlugin生效");
        implClassName = StringUtils.trimToNull(implClassName);
        AssertUtils.notNull(implClassName, "sdk-plugin解析得到的json文件存在问题:'plugin'[{0}]下的项中不能定义空字符串", pluginClassName);

        @SuppressWarnings("rawtypes") Class clazz;
        try {
            clazz = Class.forName(implClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(MessageFormat.format("sdk-plugin解析得到的json文件存在问题:'plugin'[{0}]中的实现类[{1}]对应的接口无法找到",
                    pluginClassName, implClassName));
        }

        AssertUtils.isTrue(!clazz.isInterface(), "sdk-plugin解析得到的json文件存在问题:'plugin'[{0}]中的实现类[{0}]不能是接口", pluginClassName, implClassName);
        AssertUtils.isTrue(pluginClass.isAssignableFrom(clazz), "sdk-plugin解析得到的json文件存在问题:'plugin'[{0}]中的实现类[{1}]必须继承自[{2}]",
                pluginClassName, implClassName, pluginClassName);

        //noinspection unchecked
        return clazz;
    }

    public List<SdkPluginSet.SdkPluginType<? extends ISdkPlugin>> getPluginImpls(Class<? extends ISdkPlugin> pluginClass, String scene) {
        SdkPluginSet<? extends ISdkPlugin> pluginSet = PLUGIN_MAP.get(pluginClass);
        AssertUtils.notNull(pluginSet, "未定义sdk-plugin[{0}]的配置信息", pluginClass.getName());
        List<SdkPluginSet.SdkPluginType<? extends ISdkPlugin>> pluginImpls; {
            pluginImpls = new ArrayList<>(10);
            Set<String> set = new HashSet<>();
            List<? extends SdkPluginSet.SdkPluginType<? extends ISdkPlugin>> list = pluginSet.getScene().get(scene);
            if (list != null) {
                for (SdkPluginSet.SdkPluginType<? extends ISdkPlugin> sdkPluginType : list) {
                    if (set.add(sdkPluginType.getClazz().getName())) {
                        pluginImpls.add(sdkPluginType);
                    }
                }
            }
            if (pluginImpls.isEmpty()) {
                // 可能没有定义特地场景的，尝试寻找默认的
                list = pluginSet.getScene().get(ISdkPlugin.DEFAULT_SCENE);
                if (!list.isEmpty()) {
                    ISdkPlugin bean = SdkPluginProxy.getBean(list.get(0).getClazz());
                    if (bean.isDefaultMatchAllScene()) {
                        for (SdkPluginSet.SdkPluginType<? extends ISdkPlugin> sdkPluginType : list) {
                            if (set.add(sdkPluginType.getClazz().getName())) {
                                pluginImpls.add(sdkPluginType);
                            }
                        }
                    }
                }
            }
        }
        return pluginImpls;
    }

    public Map<String/* methodName */, Class<? extends ISdkPlugin>> getSolidPluginImpls(Class<? extends ISdkPlugin> pluginClass, String scene) {
        SdkPluginSet<? extends ISdkPlugin> pluginSet = PLUGIN_MAP.get(pluginClass);
        AssertUtils.notNull(pluginSet, "未定义sdk-plugin[{0}]的配置信息", pluginClass.getName());
        if (pluginSet.getSolid().containsKey(scene)) {
            // 用这个写法，因为泛型的get好难接收
            return new HashMap<>(pluginSet.getSolid().get(scene));
        } else {
            return Collections.emptyMap();
        }
    }

    private static Class<? extends ISdkPlugin> getSdkPluginInterfacesFromObj(ISdkPlugin sdkPlugin) {
        log.info("二开sdkPlugin生效");
        Map<String/* className */, Class<?>> interfaceMap = new HashMap<>(4);
        getInterfaces(sdkPlugin.getClass(), interfaceMap, ISdkPlugin.class);
        // 这个特别，要忽略掉
        interfaceMap.remove(ISdkFunctionPlugin.class.getName());
        // 多继承形式的处理逻辑复杂，难以解释
        AssertUtils.isTrue(interfaceMap.size() == 1,
                "sdk-plugin:目前不支持基于ISdkPlugin接口的多继承形式，例如 ISdkPlugin -> IMySdkPlugin1 -> IMySdkPlugin2(非法): {0}", sdkPlugin.getClass().getName());

        //noinspection unchecked
        Class<? extends ISdkPlugin> zz = (Class<? extends ISdkPlugin>) interfaceMap.values().stream().findAny().orElse(null);
        AssertUtils.notNull(zz, "sdk-plugin:无法从示例找到合适的接口定义:" + sdkPlugin.getClass().getName());
        return zz;
    }

    private static void getInterfaces(Class<?> clazz, Map<String/* className */, Class<?>> interfaceMap, Class<?> targetClazz) {
        log.info("二开sdkPlugin生效");
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> in : interfaces) {
            if (!targetClazz.equals(in) && targetClazz.isAssignableFrom(in)) {
                if (in.isInterface()) {
                    interfaceMap.put(in.getName(), in);
                }
                getInterfaces(in, interfaceMap, targetClazz);
            }
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && targetClazz.isAssignableFrom(superClass)) {
            getInterfaces(superClass, interfaceMap, targetClazz);
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor
    public static class SdkPluginLocation {
        private String origin;
        private String type;
        private String addr;

        public static SdkPluginLocation of(String location) {
            int num2 = 2;
            location = StringUtils.trimToNull(location);
            String[] parts = StringUtils.split(location, ":", num2);
            if (parts.length != num2) {
                throw new IllegalArgumentException("the location of sdk-plugin-json [" + location + "] format is error.");
            }

            SdkPluginLocation thiz = new SdkPluginLocation();
            thiz.origin = location;
            thiz.type = parts[0];
            thiz.addr = parts[1];
            return thiz;
        }
    }

}
