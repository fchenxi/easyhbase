package cn.easyhbase.web.dao;

import cn.easyhbase.client.hbase.HBaseTables;
import cn.easyhbase.client.hbase.HbaseOperations2;
import cn.easyhbase.client.hbase.RowMapper;
import cn.easyhbase.common.AgentStatType;
import cn.easyhbase.common.Range;
import cn.easyhbase.common.hbase.distributor.RowKeyDistributorByHashPrefix;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-example.xml")
@Component
public class EasyHBaseClientExample {

    @Autowired
    private HbaseOperations2 hbaseTemplate;

    @Autowired
    private RowKeyDistributorByHashPrefix baseRowKeyDistributor;

    private final byte[] COLUMN_FAMILY_NAME = Bytes.toBytes("S");
    private final byte[] QUALIFIER_NAME = Bytes.toBytes("f");
    private static final int AGENT_STAT_VER2_NUM_PARTITIONS = 32;

    @Test
    public void syncPutTest() {
        Put put = new Put(Bytes.toBytes("put1"));
        put.addColumn(COLUMN_FAMILY_NAME, QUALIFIER_NAME, Bytes.toBytes(String.valueOf("value1")));
        hbaseTemplate.put(HBaseTables.EASYHBASE, put);
    }

    @Test
    public void asyncPutTest() {
        List<Put> puts = new ArrayList<>();
        Put put1 = new Put(Bytes.toBytes("asyncPut1"));
        Put put2 = new Put(Bytes.toBytes("asyncPut2"));
        put1.addColumn(COLUMN_FAMILY_NAME, QUALIFIER_NAME, Bytes.toBytes(String.valueOf
                ("asncyValue1")));
        put2.addColumn(COLUMN_FAMILY_NAME, QUALIFIER_NAME, Bytes.toBytes(String.valueOf
                ("asyncValue2")));
        puts.add(put1);
        puts.add(put2);
        hbaseTemplate.asyncPut(HBaseTables.EASYHBASE, puts);
    }

    @Test
    public void asyncDistributedPutTest() {
        List<Put> puts = new ArrayList<>();
        Put put1 = new Put(baseRowKeyDistributor.getDistributedKey(Bytes.toBytes("asyncPut1")));
        Put put2 = new Put(baseRowKeyDistributor.getDistributedKey(Bytes.toBytes("asyncPut2")));
        put1.addColumn(COLUMN_FAMILY_NAME, QUALIFIER_NAME, Bytes.toBytes(String.valueOf
                ("asncyValue1")));
        put2.addColumn(COLUMN_FAMILY_NAME, QUALIFIER_NAME, Bytes.toBytes(String.valueOf
                ("asyncValue2")));
        puts.add(put1);
        puts.add(put2);
        hbaseTemplate.asyncPut(HBaseTables.EASYHBASE, puts);
    }

    @Test
    public void distributedScanTest() {
        Scan scan = this.createScan(null, null, null);
        int resultLimit = 20;
        RowMapper mapper = null;
        List<List> result = hbaseTemplate.findParallel(HBaseTables.AGENT_STAT_VER2, scan,
                baseRowKeyDistributor, resultLimit, mapper,
                AGENT_STAT_VER2_NUM_PARTITIONS);

    }

    // if out of max scan cache size, then how to do it?
    private Scan createScan(AgentStatType agentStatType, String agentId, Range range) {
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
