package com.lendico.repayment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
public class HomeController {

    /**
     * Mapping home url to swagger
     */
    @RequestMapping("/")
    public String home()
    {
        return "redirect:swagger-ui.html";
    }
}
