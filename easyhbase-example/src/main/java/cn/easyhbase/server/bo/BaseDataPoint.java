package cn.easyhbase.server.bo;


public interface BaseDataPoint {
    String getRowkey();

    void setRowkey(String rowkey);

    String getValue();

    void setValue(String value);

    long getTimestamp();

    void setTimestamp(long timestamp);

    StatType getStatType();

}