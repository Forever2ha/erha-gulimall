package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Test
    void contextLoads() {
    }


    @Autowired
    OSSClient ossClient;
    @Test
    void TestOSS() throws FileNotFoundException {
        //上传文件流
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\联想Y7000\\Pictures\\Saved Pictures\\wallhaven-vg6kw3.png");

        ossClient.putObject("gulimall-erha","beauty.jpg",fileInputStream);

        ossClient.shutdown();
        System.out.println("Upload Successfully");

    }

}
