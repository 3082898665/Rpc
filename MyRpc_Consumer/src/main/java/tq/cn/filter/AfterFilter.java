package tq.cn.filter;

/**
 * @description:
 */
public class AfterFilter implements ClientAfterFilter {

    @Override
    public void doFilter(FilterData filterData) {
        System.out.println("客户端后置处理器启动咯");
    }
}
