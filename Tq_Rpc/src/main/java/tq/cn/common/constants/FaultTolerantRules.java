package tq.cn.common.constants;

/**
 * @description: 失败策略
 */
public interface FaultTolerantRules {

    String Failover = "failover";
    String FailFast = "failFast";
    String Failsafe = "failsafe";
}
