package test.org.test.conroller;

import com.huchonglin.anno.Controller;
import com.huchonglin.anno.RequestMapping;

/**
 * @author: hcl
 * @date: 2020/7/4 08:50
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/login")
    public void login(){
        System.out.println("login...");
    }
}
