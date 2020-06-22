package com.authine.cloudpivot.web.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author 1031
 */
@Configuration
public class SpringSessionConfig {

    /**
     * 用于spring session，防止每次创建一个线程
     *
     * @return ThreadPoolTaskExecutor
     */
    @Bean
    public ThreadPoolTaskExecutor springSessionRedisTaskExecutor() {
        ThreadPoolTaskExecutor springSessionRedisTaskExecutor = new ThreadPoolTaskExecutor();
        springSessionRedisTaskExecutor.setCorePoolSize(8);
        springSessionRedisTaskExecutor.setMaxPoolSize(8);
        springSessionRedisTaskExecutor.setKeepAliveSeconds(500);
        springSessionRedisTaskExecutor.setQueueCapacity(2000);
        springSessionRedisTaskExecutor.setThreadNamePrefix("Spring-session-Redis-Executor:");
        return springSessionRedisTaskExecutor;
    }

}
