package io.schell.p1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by dylan on 6/27/2016.
 */
@Controller
public class HomeController {
    @Autowired
    private P1Controller p1;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("m", p1.p1());
        return "index";
    }


}
