package tq.cn.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 拦截器链
 */
public class FilterChain {


    private List<tq.cn.filter.Filter> filters = new ArrayList<>();

    public void addFilter(tq.cn.filter.Filter filter){
        filters.add(filter);
    }


    public void addFilter(List<Object> filters){
        for (Object filter : filters) {
            addFilter((tq.cn.filter.Filter) filter);
        }
    }
    public void doFilter(tq.cn.filter.FilterData data){
        for (tq.cn.filter.Filter filter : filters) {
            filter.doFilter(data);
        }
    }
}
