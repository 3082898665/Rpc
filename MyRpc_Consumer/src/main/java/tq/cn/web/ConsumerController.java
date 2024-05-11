package tq.cn.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tq.cn.annotation.MyRpcReference;
import tq.cn.service.TestService;

@RestController
@RequestMapping("/rpc")
public class ConsumerController {

    @MyRpcReference
    private TestService testService;

    //测试
    @RequestMapping("/test")
    public int test(){
        return testService.test();
    }

    //简单远程调用
    @RequestMapping("/test1")
    public int test1(){
        return testService.add(2, 3);
    }
}
