package com.tuandai.ms.ar.config;

import com.google.common.collect.Lists;
import com.tuandai.ms.common.ds.MultipleDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Properties;

public class DataSourceConfig {

    /**
     * 数据源读KEY
     */
    public static final String DATASOURCE_READ_KEY = "database.kongadmin.read";

    /**
     * 数据源写KEY
     */
    public static final String DATASOURCE_WRITE_KEY = "database.kongadmin.write";

    /**
     * 重写sqlsession bean 的注入，使用mysql的dialect
     *
     * @param dynamicDataSource
     * @return
     * @throws Exception
     */
    @Bean(name = {"sqlSessionFactory"})
    @Primary
    public SqlSessionFactory sqlSessionFactory(AbstractRoutingDataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dynamicDataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath*:mappers/**/*.xml"));
        sessionFactory.setConfigLocation((new DefaultResourceLoader()).getResource("classpath:mybatis-config.xml"));
        Properties properties = new Properties();
        properties.put("dialect", "mysql");
        sessionFactory.setConfigurationProperties(properties);
        return sessionFactory.getObject();
    }

    @Bean
    public MultipleDataSource multipleDataSource() {
        return new MultipleDataSource(Lists.newArrayList(DATASOURCE_WRITE_KEY, DATASOURCE_READ_KEY));
    }
}
