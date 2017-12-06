package com.zero.webmagic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Year: 2017-2017/12/2-18:32
 * Project:webmagic-demo
 * Package:com.zero.webmagic.controller
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("index")
public class IndexController {
    @GetMapping
    public String index() {
        return "index";
    }
}
