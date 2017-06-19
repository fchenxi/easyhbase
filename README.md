# easyhbase
A hbase client reference from [pinpoint](https://github.com/naver/pinpoint.).
It provides the salted rowkey, see it here https://sematext.com/blog/2012/04/09/hbasewd-avoid-regionserver-hotspotting-[] despite-writing-records-with-sequential-keys.

Examples

1. syncPut & asyncPut example

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(locations = "classpath:applicationContext-example.xml")
    @Component
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

} 

2. scan example

    @Test
    public void distributedScanTest() {
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
