package cn.easyhbase.server.bo;

public class EasyHBaseBo implements BaseDataPoint {

    private String rowkey;

    private String value;

    private long timestamp;

    private StatType type;


    @Override
    public String getRowkey() {
        return this.rowkey;
    }

    @Override
    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    @Override
    public String getValue() {
        return this.rowkey;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public StatType getStatType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "EasyHBaseBo{" +
                "rowkey='" + rowkey + '\'' +
                ", value='" + value + '\'' +
                ", timestamp=" + timestamp +
                ", type=" + type +
                '}';
    }
}
