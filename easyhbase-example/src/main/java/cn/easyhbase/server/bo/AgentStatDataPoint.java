package cn.easyhbase.server.bo;

/**
 * @author HyunGil Jeong
 */
public interface AgentStatDataPoint {
    String getAgentId();
    void setAgentId(String agentId);
    long getTimestamp();
    void setTimestamp(long timestamp);
    AgentStatType getAgentStatType();
}