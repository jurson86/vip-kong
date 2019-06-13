package com.tuandai.ms.ar;

import com.tuandai.ms.utils.JsonUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

/**
 * @author wangg
 * @create 2018-08-24 09:45:00
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AdminApplication.class)
public class TestBase {

    @Autowired
    private WebApplicationContext context;

    private DefaultMockMvcBuilder builder;

    protected MockMvc mockMvc;

    @Before
    public void init(){
        //mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        builder = MockMvcBuilders.webAppContextSetup(context);
        builder.addFilters((Filter) context.getBean("shiroFilter"));
        mockMvc = builder.build();
    }

    protected <T> void printResult(T t) {
        System.err.println(JsonUtils.object2Json(t));
    }

    protected void print(String t) {
        System.err.println(t);
    }
}
