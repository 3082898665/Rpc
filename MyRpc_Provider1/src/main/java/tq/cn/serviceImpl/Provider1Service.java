package tq.cn.serviceImpl;

import tq.cn.annotation.MyRpcService;
import tq.cn.service.TestService;

@MyRpcService
public class Provider1Service implements TestService {
    @Override
    public int test() {
        return 0;
    }

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
