package com.cartravel.ordermonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

//配置扫描的包路径
//@SpringBootApplication
//@SpringBootApplication(scanBasePackages = "com.cartravel.*")
//@MapperScan("com.cartravel.mapper.*")
@Controller
@RequestMapping("/home")
public class CarTravelOrderMonitor {
    private final static Logger logger = LoggerFactory.getLogger(CarTravelOrderMonitor.class);

    /**
     * 项目首页
     * @return
     */
    @RequestMapping("/index")
    String home(){
        return "/index.html";
    }


    /**
     * 轨迹监控
     * @return
     */
    @RequestMapping("/trackmonitor")
    public ModelAndView TrackMonitor(){
        logger.warn("trackmonitor.............");
        return new ModelAndView("/trackmonitor.html");
    }

    /**
     * 出行迁途
     * @return
     */
    @RequestMapping("/movingway")
    public ModelAndView MovingWay(){
        logger.warn("movingway.............");
        return new ModelAndView("/movingway.html");
    }

    public static void main(String[] args) {
        SpringApplication.run(CarTravelOrderMonitor.class,args);
    }

    @Configuration
    public static class WebSocketConfig {
        @Bean
        public ServerEndpointExporter serverEndpointExporter() {
            return new ServerEndpointExporter();
        }
    }
}
