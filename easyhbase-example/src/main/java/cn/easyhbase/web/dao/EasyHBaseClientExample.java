package cn.easyhbase.web.dao;

import cn.easyhbase.HBaseTables;
import cn.easyhbase.client.hbase.HbaseOperations2;
import cn.easyhbase.client.hbase.RowMapper;
import cn.easyhbase.common.Range;
import cn.easyhbase.common.hbase.distributor.RowKeyDistributorByHashPrefix;
import cn.easyhbase.server.bo.BaseDataPoint;
import cn.easyhbase.web.mapper.EasyHBaseMapperV2;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
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
    private HbaseOperations2 hbaseScanTemplate;

    @Autowired
    private HbaseOperations2 hbasePutTemplate;


    @Autowired
    private RowKeyDistributorByHashPrefix baseRowKeyDistributor;

    private final byte[] COLUMN_FAMILY_NAME = Bytes.toBytes("S");
    private final byte[] QUALIFIER_NAME = Bytes.toBytes("f");
    private static final int AGENT_STAT_VER2_NUM_PARTITIONS = 32;

    @Test
    public void syncPutTest() {
        Put put = new Put(Bytes.toBytes("put1"));
        put.addColumn(COLUMN_FAMILY_NAME, QUALIFIER_NAME, Bytes.toBytes(String.valueOf("value1")));
        hbasePutTemplate.put(HBaseTables.EASYHBASE, put);
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
        hbasePutTemplate.asyncPut(HBaseTables.EASYHBASE, puts);
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
        hbasePutTemplate.asyncPut(HBaseTables.EASYHBASE, puts);
    }

    @Test
    public void existsTest() {
        Scan scan = new Scan();
        int resultLimit = 20;
        RowMapper mapper = new EasyHBaseMapperV2();
        List<List> result = hbaseScanTemplate.findParallel(HBaseTables.EASYHBASE, scan,
                baseRowKeyDistributor, resultLimit, mapper,
                AGENT_STAT_VER2_NUM_PARTITIONS);
        boolean exists = result.size() > 0;
        Assert.assertTrue(exists);

    }

    @Test
    public void distributedScanTest() {
        Range range = new Range(System.currentTimeMillis(), System.currentTimeMillis() + 100000);
        Scan scan = new Scan();
        RowMapper mapper = new EasyHBaseMapperV2();
        List<BaseDataPoint> results = hbaseScanTemplate.findParallel(HBaseTables.EASYHBASE,
                scan,
                baseRowKeyDistributor, mapper,
                AGENT_STAT_VER2_NUM_PARTITIONS);
        for (BaseDataPoint baseDataPoint : results) {
            System.out.println(baseDataPoint.toString());
        }
    }

    @Test
    public void distributedLimitedScanTest() {
        Range range = new Range(System.currentTimeMillis(), System.currentTimeMillis() + 100000);
        Scan scan = new Scan();
        int resultLimit = 3;
        RowMapper mapper = new EasyHBaseMapperV2();
        List<BaseDataPoint> results = hbaseScanTemplate.findParallel(HBaseTables.EASYHBASE,
                scan,
                baseRowKeyDistributor,resultLimit, mapper,
                AGENT_STAT_VER2_NUM_PARTITIONS);
        for (BaseDataPoint baseDataPoint : results) {
            System.out.println(baseDataPoint.toString());
        }
    }

    // if out of max scan cache size, then how to do it?
    private Scan createScan(byte[] startRowkey, byte[] stopRowkey, Range range) {
        long scanRange = range.getTo() - range.getFrom();
        return new Scan();
    }

}
