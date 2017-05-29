package cn.easyhbase.client.util;


import cn.easyhbase.client.hbase.HBaseTables;
import cn.easyhbase.common.AgentStatType;
import cn.easyhbase.common.Range;
import org.apache.hadoop.hbase.client.Scan;

public class ScanUtils {
    private static final int MAX_SCAN_CACHE_SIZE = 256;

    // if out of max scan cache size, then how to do it?
    public Scan createScan(AgentStatType agentStatType, String agentId, Range range) {
        long scanRange = range.getTo() - range.getFrom();
        long expectedNumRows = ((scanRange - 1) / HBaseTables.AGENT_STAT_TIMESPAN_MS) + 1;
//        if (range.getFrom() != AgentStatUtils.getBaseTimestamp(range.getFrom())) {
//            expectedNumRows++;
//        }
//        if (expectedNumRows > MAX_SCAN_CACHE_SIZE) {
//            return this.createScan(agentStatType, agentId, range, MAX_SCAN_CACHE_SIZE);
//        } else {
//            // expectedNumRows guaranteed to be within integer range at this point
//            return this.createScan(agentStatType, agentId, range, (int) expectedNumRows);
//        }
        return new Scan();
    }

}
