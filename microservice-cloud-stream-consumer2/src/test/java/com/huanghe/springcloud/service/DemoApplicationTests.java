package com.huanghe.springcloud.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.huanghe.springcloud.service.service.TestService;

@SpringBootTest(classes = TestApplication.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore({
        "javax.management.*", "javax.net.ssl.*", "javax.crypto.*", "javax.xml.*", "org.xml.*", "org.w3c.dom.*",
        "org.apache.*"
})
public class DemoApplicationTests {

    @Autowired
    TestService testService;

    @Value("${my}")
    private String port;

    @Test
    public void testDemo() {
        System.out.println("application port is " + port);
    }

}