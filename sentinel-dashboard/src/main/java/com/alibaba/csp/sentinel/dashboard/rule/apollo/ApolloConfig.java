package com.alibaba.csp.sentinel.dashboard.rule.apollo;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author QingFan 2020-09-19
 * @version 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "apollo.portal")
@ComponentScan("com.alibaba.csp.sentinel.dashboard.rule.apollo")
public class ApolloConfig implements InitializingBean {

    public static String USER_ID = "apollo";
    public static String ENV = "env";
    public static String CLUSTER_NAME = "default";
    public static String NAME_SPACE = "application";
    public static String URL = "";

    private String url;

    private List<String> appNameConfigList = new ArrayList<>();

    private String userId = USER_ID;

    private String env = ENV;

    private String clusterName = CLUSTER_NAME;

    private String namespace = NAME_SPACE;

    public static volatile ConcurrentHashMap<String/* appId */, String/* thirdId */> thirdIdMap = new ConcurrentHashMap<>();
    public static volatile ConcurrentHashMap<String/* applicationName */, String/* appId */> appIdMap = new ConcurrentHashMap<>();
    public static volatile ConcurrentHashMap<String/* applicationName */, String/* token */> tokenMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        ApolloConfig.ENV = env;
        ApolloConfig.USER_ID = userId;
        ApolloConfig.CLUSTER_NAME = clusterName;
        ApolloConfig.NAME_SPACE = namespace;
        ApolloConfig.URL = url;

        this.appNameConfigList.forEach(item -> {
            String[] items = item.split(":");

            if (items.length == 4) {
                String applicationName = items[0];
                String token = items[1];
                String appId = items[2];
                String thirdId = items[3];

                thirdIdMap.putIfAbsent(appId, thirdId);
                appIdMap.putIfAbsent(applicationName, appId);
                tokenMap.putIfAbsent(applicationName, token);
            }
        });
    }

    @Bean
    public Converter<List<FlowRuleEntity>, String> flowRuleEntityEncoder() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<FlowRuleEntity>> flowRuleEntityDecoder() {
        return s -> JSON.parseArray(s, FlowRuleEntity.class);
    }

    @Bean
    public Converter<List<DegradeRuleEntity>, String> degradeRuleEntityEncoder() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<DegradeRuleEntity>> degradeRuleEntityDecoder() {
        return s -> JSON.parseArray(s, DegradeRuleEntity.class);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getAppNameConfigList() {
        return appNameConfigList;
    }

    public void setAppNameConfigList(List<String> appNameConfigList) {
        this.appNameConfigList = appNameConfigList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
