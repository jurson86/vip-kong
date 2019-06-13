package com.tuandai.ms.ar.controller.user;

import com.tuandai.ms.ar.constants.KongAdminConstants;
import com.tuandai.ms.ar.controller.BaseController;
import com.tuandai.ms.ar.dto.ResponseDTO;
import com.tuandai.ms.ar.dto.req.LoginReq;
import com.tuandai.ms.ar.dto.resp.LoginResp;
import com.tuandai.ms.ar.model.enums.ResCodeEnum;
import com.tuandai.ms.ar.model.user.KongUser;
import com.tuandai.ms.ar.service.inf.KongUserService;
import com.tuandai.ms.ar.service.inf.KongUserTokenService;
import com.tuandai.ms.ar.utils.ShiroUtils;
import com.tuandai.ms.common.spring.ApplicationConstant;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * 登录相关功能
 *
 * @author wanggang
 * @createTime 2018-08-21 11:50:00
 */
@RestController
public class LoginController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    private static final char[] CHAR_ARRAY = {'1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'i', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z'};

    @Autowired
    KongUserService userService;

    @Autowired
    KongUserTokenService tokenService;

    @Autowired
    ApplicationConstant applicationConstant;

    @RequestMapping("captcha.jpg")
    public void getImageCode(HttpServletResponse response) {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        // 生成随机码
        int count = 4,temp = -1;
        char[] code =new  char[4];
        Random rand = new Random();
        for (int i = 0; i < count; i++){
            if (temp != -1){
                rand = new Random();
            }
            int t = rand.nextInt(CHAR_ARRAY.length);
            while (temp == t){
                t = rand.nextInt(CHAR_ARRAY.length);
            }
            temp = t;
            code[i] = CHAR_ARRAY[t];
        }

        //将验证码写至cookie和会话
        String upCode = String.valueOf(code).toUpperCase();
        //	logger.info("生成验证码：{}",upCode);

        //保存到shiro session
        ShiroUtils.setSessionAttribute(KongAdminConstants.KAPTCHA_SESSION_KEY, upCode);

        BufferedImage image = new BufferedImage(56, 20, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(new Color(153,193,203));
        g.fillRect(0, 0, 56, 20);
        //g.setFont(new Font("Arial ", Font.PLAIN, 10));
        for (int i = 0; i < 4; i++) {
            if (code[i] >= '0' && code[i] <= '9'){
                //数字用红色
                g.setColor(new Color(166, 8, 8));
            }else{
                //字母用黑色显示
                g.setColor(Color.black);
            }
            g.drawString(String.valueOf(code[i]), 7 + (i * 12), 13);
        }
        g.dispose();
        try {
            ImageIO.write(image, "jpg", response.getOutputStream());
            //logger.info("成功输出验证码：{}",upCode);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @RequestMapping("login")
    public ResponseDTO<LoginResp> login(@Validated @RequestBody LoginReq loginReq, BindingResult formValid){
        checkForm(formValid);

        //验证图片
        boolean isProd = applicationConstant.isProdProfile() || applicationConstant.isPrevProfile();
        if(isProd){
            String captcha = ShiroUtils.getKaptcha(KongAdminConstants.KAPTCHA_SESSION_KEY);
            if(!loginReq.getCaptcha().equalsIgnoreCase(captcha)){
                return ResponseDTO.error(ResCodeEnum.USER_LOGIN_CAPTCHA_ERROR);
            }
        }

        KongUser user = userService.queryByUserName(loginReq.getUsername());

        //账号不存在、密码错误
        if(user == null || !user.getPassword().equals(new Sha256Hash(loginReq.getPassword(), user.getSalt()).toHex())) {
            return ResponseDTO.error(ResCodeEnum.USER_LOGIN_ERROR);
        }

        //账号锁定
        if(user.getStatus() == 0){
            return ResponseDTO.error(ResCodeEnum.USER_STATUS_INVALID);
        }

        //更新 token
        String token = tokenService.updateToken(user);

        return ResponseDTO.success(new LoginResp(token,user.getUsername(),user.getRealName()));
    }
}
