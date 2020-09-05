package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ccc
 * @create 2020-09-02 21:08
 */
@Controller
public class PassportController {

    @RequestMapping("login")
    public String index(Model model,String originUrl){
        model.addAttribute("originUrl",originUrl);
        return "login";
    }
}
