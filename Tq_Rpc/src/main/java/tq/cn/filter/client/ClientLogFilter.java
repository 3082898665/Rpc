package tq.cn.filter.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tq.cn.filter.ClientBeforeFilter;
import tq.cn.filter.FilterData;


/**
 * @description: 日志
 */
public class ClientLogFilter implements ClientBeforeFilter {

    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);


    @Override
    public void doFilter(FilterData filterData) {
        logger.info(filterData.toString());
    }
}
