package cn.easyhbase.client.hbase;

public interface RowReducer<T> {


    T reduce(T map) throws Exception;
}
