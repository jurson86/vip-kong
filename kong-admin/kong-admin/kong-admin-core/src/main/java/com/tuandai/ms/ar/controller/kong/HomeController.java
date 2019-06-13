package com.tuandai.ms.ar.controller.kong;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Gus Jiang
 * @date 2018/5/30  14:18
 */
@Controller
public class HomeController {

    @RequestMapping("/")
    public String index() {
        return "forward:/index.html";
    }


}
