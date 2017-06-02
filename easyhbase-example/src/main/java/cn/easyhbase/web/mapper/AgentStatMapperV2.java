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

package cn.easyhbase.web.mapper;

import cn.easyhbase.client.hbase.HBaseTables;
import cn.easyhbase.server.bo.AgentStatDataPoint;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author HyunGil Jeong
 */
public class AgentStatMapperV2<T extends AgentStatDataPoint> implements AgentStatMapper<T> {

    public final static Comparator<AgentStatDataPoint> REVERSE_TIMESTAMP_COMPARATOR = new
            Comparator<AgentStatDataPoint>() {
        @Override
        public int compare(AgentStatDataPoint o1, AgentStatDataPoint o2) {
            long x = o2.getTimestamp();
            long y = o1.getTimestamp();
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    };


    public AgentStatMapperV2() {
    }

    @Override
    public List<T> mapRow(Result result, int rowNum) throws Exception {
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        final byte[] distributedRowKey = result.getRow();
        List<T> dataPoints = new ArrayList<>();

        for (Cell cell : result.rawCells()) {
            if (CellUtil.matchingFamily(cell, HBaseTables.AGENT_STAT_CF_STATISTICS)) {

                List<T> candidates = new ArrayList<>();
                for (T candidate : candidates) {
                    long timestamp = candidate.getTimestamp();
                    dataPoints.add(candidate);
                }
            }
        }
        // Reverse sort as timestamp is stored in a reversed order.
        Collections.sort(dataPoints, REVERSE_TIMESTAMP_COMPARATOR);
        return dataPoints;
    }
}
