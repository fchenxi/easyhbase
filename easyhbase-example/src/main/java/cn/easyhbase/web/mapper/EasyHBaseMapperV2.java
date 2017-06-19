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

import cn.easyhbase.HBaseTables;
import cn.easyhbase.client.hbase.RowMapper;
import cn.easyhbase.server.bo.BaseDataPoint;
import cn.easyhbase.server.bo.EasyHBaseBo;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Comparator;

public class EasyHBaseMapperV2 implements RowMapper<BaseDataPoint> {

    public final static Comparator<BaseDataPoint> REVERSE_TIMESTAMP_COMPARATOR = new
            Comparator<BaseDataPoint>() {
                @Override
                public int compare(BaseDataPoint o1, BaseDataPoint o2) {
                    long x = o2.getTimestamp();
                    long y = o1.getTimestamp();
                    return (x < y) ? -1 : ((x == y) ? 0 : 1);
                }
            };


    public EasyHBaseMapperV2() {
    }

    @Override
    public BaseDataPoint mapRow(Result result, int rowNum) throws Exception {
        if (result.isEmpty()) {
//            return Collections.emptyList();
            return null;
        }
        final byte[] distributedRowKey = result.getRow();
//        List<BaseDataPoint> dataPoints = new ArrayList<>();
        EasyHBaseBo bo = new EasyHBaseBo();
        for (Cell cell : result.rawCells()) {
            if (CellUtil.matchingFamily(cell, HBaseTables.EASYHBASE_CF)) {

                bo.setRowkey(Bytes.toString(cell.getRow()));
                bo.setValue(Bytes.toString(cell.getValue()));
                bo.setTimestamp(cell.getTimestamp());
//                dataPoints.add(bo);
//                List<T> candidates = new ArrayList<>();
//                for (T candidate : candidates) {
//                    candidate.setRowkey(candidate.getRowkey());
//                    candidate.setValue(candidate.getValue());
//                    candidate.setTimestamp(candidate.getTimestamp());
//                    dataPoints.add(candidate);
//                }
            }
        }
        // Reverse sort as timestamp is stored in a reversed order.
//        Collections.sort(dataPoints, REVERSE_TIMESTAMP_COMPARATOR);
        return bo;
    }
}
