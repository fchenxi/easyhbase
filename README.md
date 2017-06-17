# easyhbase
A hbase client reference from pinpoint, see it here, https://github.com/naver/pinpoint.

1. It provide the salted rowkey, see it here, https://sematext.com/blog/2012/04/09/hbasewd-avoid-regionserver-hotspotting-[] despite-writing-records-with-sequential-keys.

2. Example

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-example.xml")
public class EasyHBaseClientExample {
    @Autowired
    private HbaseOperations2 hbaseScanTemplate;

    @Autowired
    private HbaseOperations2 hbaseAsyncTemplate;

    @Autowired
    private RowKeyDistributorByHashPrefix baseRowKeyDistributor;

    private final byte[] COLUMN_FAMILY_NAME = Bytes.toBytes("S");
    private final byte[] QUALIFIER_NAME = Bytes.toBytes("f");
    private static final int AGENT_STAT_VER2_NUM_PARTITIONS = 32;

    @Test
    public void syncPutTest() {
        Put put = new Put(Bytes.toBytes("put1"));
        put.addColumn(COLUMN_FAMILY_NAME, QUALIFIER_NAME, Bytes.toBytes(String.valueOf("value1")));
        hbaseAsyncTemplate.put(HBaseTables.EASYHBASE, put);
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
        hbaseAsyncTemplate.asyncPut(HBaseTables.EASYHBASE, puts);
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
        hbaseAsyncTemplate.asyncPut(HBaseTables.EASYHBASE, puts);
    }

    @Test
    public void distributedScanTest() {
        Scan scan = this.createScan(null, null, null);
        int resultLimit = 20;
        RowMapper mapper = null;
        List<List> result = hbaseAsyncTemplate.findParallel(HBaseTables.AGENT_STAT_VER2, scan,
                baseRowKeyDistributor, resultLimit, mapper,
                AGENT_STAT_VER2_NUM_PARTITIONS);

    }
} 

