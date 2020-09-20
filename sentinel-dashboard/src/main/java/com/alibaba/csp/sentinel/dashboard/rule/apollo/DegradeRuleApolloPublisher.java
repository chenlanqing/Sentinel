package com.alibaba.csp.sentinel.dashboard.rule.apollo;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author QingFan 2020-09-19
 * @version 1.0.0
 */
@Component("degradeRuleApolloPublisher")
public class DegradeRuleApolloPublisher implements DynamicRulePublisher<List<DegradeRuleEntity>> {

    private FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    @Resource
    private Converter<List<DegradeRuleEntity>, String> converter;

    @Override
    public void publish(String app, List<DegradeRuleEntity> rules) throws Exception {
        if (CollectionUtils.isEmpty(rules)) {
            return;
        }

        ApolloOpenApiClient client = ApolloConfigUtil.createApolloOpenApiClient(app);
        if (client == null) {
            return;
        }
        String currentDateString = fastDateFormat.format(new Date());

        // 1、提交配置：修改或者创建
        String degradeDataId = ApolloConfigUtil.getDegradeDataId(app);
        String appId = ApolloConfigUtil.getAppIdWithAppName(app);

        OpenItemDTO dto = new OpenItemDTO();
        dto.setKey(degradeDataId);
        dto.setValue(converter.convert(rules));
        dto.setComment("modify:" + currentDateString);
        dto.setDataChangeCreatedBy(ApolloConfig.USER_ID);
        dto.setDataChangeLastModifiedBy(ApolloConfig.USER_ID);

        client.createOrUpdateItem(appId, ApolloConfig.ENV, ApolloConfig.CLUSTER_NAME, ApolloConfig.NAME_SPACE, dto);

        // 2、发布配置
        NamespaceReleaseDTO releaseDTO = new NamespaceReleaseDTO();
        releaseDTO.setEmergencyPublish(true);
        releaseDTO.setReleaseComment("modify comment:" + currentDateString);
        releaseDTO.setReleaseTitle("发布新属性:" + currentDateString);
        releaseDTO.setReleasedBy(ApolloConfig.USER_ID);

        client.publishNamespace(appId, ApolloConfig.ENV, ApolloConfig.CLUSTER_NAME, ApolloConfig.NAME_SPACE, releaseDTO);

    }
}
