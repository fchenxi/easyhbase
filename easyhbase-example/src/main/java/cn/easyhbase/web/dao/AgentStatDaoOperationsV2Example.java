/*
 * Copyright 2016 Naver Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.easyhbase.web.dao;


import cn.easyhbase.client.hbase.HBaseTables;
import cn.easyhbase.client.hbase.HbaseOperations2;
import cn.easyhbase.client.util.AgentStatUtils;
import cn.easyhbase.common.Range;
import cn.easyhbase.common.hbase.distributor.RowKeyDistributorByHashPrefix;
import cn.easyhbase.server.bo.AgentStatDataPoint;
import cn.easyhbase.server.bo.AgentStatType;
import cn.easyhbase.web.mapper.AgentStatMapperV2;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HyunGil Jeong
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-example.xml")
@Component
public class AgentStatDaoOperationsV2Example {

    private static final int AGENT_STAT_VER2_NUM_PARTITIONS = 32;
    private static final int MAX_SCAN_CACHE_SIZE = 256;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HbaseOperations2 hbaseOperations2;

    @Autowired
    private RowKeyDistributorByHashPrefix baseRowKeyDistributor;


    <T extends AgentStatDataPoint> List<T> getAgentStatList(AgentStatType agentStatType,
                                                            AgentStatMapperV2<T> mapper, String
                                                                    agentId, Range range) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (range == null) {
            throw new NullPointerException("range must not be null");
        }

        Scan scan = this.createScan(agentStatType, agentId, range);

        List<List<T>> intermediate = hbaseOperations2.findParallel(HBaseTables.AGENT_STAT_VER2,
                scan, baseRowKeyDistributor, mapper, AGENT_STAT_VER2_NUM_PARTITIONS);
        int expectedSize = (int) (range.getRange() / HBaseTables.AGENT_STAT_TIMESPAN_MS);
        List<T> merged = new ArrayList<>(expectedSize);
        for (List<T> each : intermediate) {
            merged.addAll(each);
        }
        return merged;
    }

    <T extends AgentStatDataPoint> boolean agentStatExists(AgentStatType agentStatType,
                                                           AgentStatMapperV2<T> mapper, String
                                                                   agentId, Range range) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (range == null) {
            throw new NullPointerException("range must not be null");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("checking for stat data existence : agentId={}, {}", agentId, range);
        }

        int resultLimit = 1;
        Scan scan = this.createScan(agentStatType, agentId, range, resultLimit);

        List<List<T>> result = hbaseOperations2.findParallel(HBaseTables.AGENT_STAT_VER2, scan,
                baseRowKeyDistributor, resultLimit, mapper, AGENT_STAT_VER2_NUM_PARTITIONS);
        if (result.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }


    private Scan createScan(AgentStatType agentStatType, String agentId, Range range) {
        long scanRange = range.getTo() - range.getFrom();
        long expectedNumRows = ((scanRange - 1) / HBaseTables.AGENT_STAT_TIMESPAN_MS) + 1;
        if (range.getFrom() != AgentStatUtils.getBaseTimestamp(range.getFrom())) {
            expectedNumRows++;
        }
        if (expectedNumRows > MAX_SCAN_CACHE_SIZE) {
            return this.createScan(agentStatType, agentId, range, MAX_SCAN_CACHE_SIZE);
        } else {
            // expectedNumRows guaranteed to be within integer range at this point
            return this.createScan(agentStatType, agentId, range, (int) expectedNumRows);
        }
    }

    private Scan createScan(AgentStatType agentStatType, String agentId, Range range, int
            scanCacheSize) {
        Scan scan = new Scan();
//        Scan scan = this.operationFactory.createScan(agentId, agentStatType, range.getFrom(),
// range.getTo());
        scan.setCaching(scanCacheSize);
        scan.setId("AgentStat_" + agentStatType);
        scan.addFamily(HBaseTables.AGENT_STAT_CF_STATISTICS);
        return scan;
    }
}
