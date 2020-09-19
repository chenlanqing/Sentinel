package com.alibaba.csp.sentinel.dashboard.rule.apollo;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author QingFan 2020-09-19
 * @version 1.0.0
 */
public final class ApolloConfigUtil {

    private static final String FLOW_RULE_TYPE = "flow";

    private static final String DEGRADE_RULE_TYPE = "degrade";
    /**
     * *-flow-rules
     */
    private static final String FLOW_DATA_ID_POSTFIX = "-" + FLOW_RULE_TYPE + "-rules";
    /**
     * *-degrade-rules
     */
    private static final String DEGRADE_DATA_ID_POSTFIX = "-" + DEGRADE_RULE_TYPE + "-rules";

    private static ConcurrentHashMap<String, ApolloOpenApiClient> CLIENT_MAP = new ConcurrentHashMap<>();

    public static String getFlowDataId(String appName) {
        return String.format("%s%s", appName, FLOW_DATA_ID_POSTFIX);
    }

    public static String getDegardeDataId(String appName) {
        return String.format("%s%s", appName, DEGRADE_DATA_ID_POSTFIX);
    }

    public static ApolloOpenApiClient createApolloOpenApiClient(String appName) {
        ApolloOpenApiClient client = CLIENT_MAP.get(appName);
        if (client != null) {
            return client;
        }

        String token = ApolloConfig.tokenMap.get(appName);
        if (StringUtil.isBlank(token)) {
            throw new RuntimeException("Apollo 开发平台未注册 " + appName);
        }

        client = ApolloOpenApiClient.newBuilder()
                .withPortalUrl(ApolloConfig.URL)
                .withToken(token)
                .build();

        CLIENT_MAP.putIfAbsent(appName, client);
        return client;
    }

    public static String getAppIdWithAppName(String appName) {
        return ApolloConfig.appIdMap.get(appName);
    }
}
