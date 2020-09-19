package com.alibaba.csp.sentinel.dashboard.rule.apollo;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author QingFan 2020-09-19
 * @version 1.0.0
 */
@Component("flowRuleApolloProvider")
public class FlowRuleApolloProvider implements DynamicRuleProvider<List<FlowRuleEntity>> {

    @Resource
    private Converter<String,  List<FlowRuleEntity>> convert;

    @Override
    public List<FlowRuleEntity> getRules(String appName) throws Exception {
        ApolloOpenApiClient client = ApolloConfigUtil.createApolloOpenApiClient(appName);

        if (client == null) {
            return Lists.newArrayList();
        }

        String flowDataId = ApolloConfigUtil.getFlowDataId(appName);
        String appId = ApolloConfigUtil.getAppIdWithAppName(appName);

        OpenNamespaceDTO dto = client.getNamespace(appId, ApolloConfig.ENV, ApolloConfig.CLUSTER_NAME, ApolloConfig.NAME_SPACE);

        String flowDataIdValue = dto.getItems().stream()
                .filter(p -> p.getKey().equals(flowDataId))
                .map(OpenItemDTO::getValue)
                .findFirst()
                .orElse("");

        return convert.convert(flowDataIdValue);
    }
}
