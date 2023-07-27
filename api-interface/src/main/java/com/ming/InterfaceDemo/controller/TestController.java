package com.ming.InterfaceDemo.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ming.InterfaceDemo.utils.CommentUtil;
import com.ming.apiCommon.model.entity.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

@RestController
public class TestController {

    @GetMapping("/get/getName")
    public String getName(String name) throws IOException {
        return "get请求成功获取名字：" + name;
    }

    @PostMapping("/user/name")
    public String getUsernameByPost(@RequestBody User user) {
        return "POST 用户名字是：" + user.getUserName();
    }

    @GetMapping("/duJiTang")
    public String duJiTang(){
        HttpResponse response = HttpRequest.get("https://api.btstu.cn/yan/api.php").execute();
        return response.body();
    }
}
