package org.cloud.console.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin.autoconfigure.ui.ZipkinUiProperties;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;

/**
 * 功能说明: 将原有 root 目录拦截取消<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/19<br>
 * <br>
 */
@Configuration
@EnableConfigurationProperties(ZipkinUiProperties.class)
@ConditionalOnProperty(name = "zipkin.ui.enabled", matchIfMissing = true)
@RestController
public class MyZipkinUiAutoConfiguration extends WebMvcConfigurerAdapter {


    @Autowired
    ZipkinUiProperties ui;

    @Value("classpath:zipkin-ui/index.html")
    Resource indexHtml;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/zipkin/**")
                .addResourceLocations("classpath:/zipkin-ui/")
                .setCachePeriod((int) TimeUnit.DAYS.toSeconds(365));
    }

    /**
     * This opts out of adding charset to png resources.
     *
     * <p>By default, {@linkplain CharacterEncodingFilter} adds a charset qualifier to all resources,
     * which helps, as javascript assets include extended character sets. However, the filter also
     * adds charset to well-known binary ones like png. This creates confusing content types, such as
     * "image/png;charset=UTF-8".
     *
     * See https://github.com/spring-projects/spring-boot/issues/5459
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter() {
            @Override
            protected boolean shouldNotFilter(HttpServletRequest request) {
                return request.getServletPath().endsWith(".png");
            }
        };
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @RequestMapping(value = "/zipkin/config.json", method = GET)
    public ResponseEntity<ZipkinUiProperties> serveUiConfig() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
                .contentType(MediaType.APPLICATION_JSON)
                .body(ui);
    }

    @RequestMapping(value = "/zipkin/index.html", method = GET)
    public ResponseEntity<Resource> serveIndex() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
                .body(indexHtml);
    }

    /**
     * This cherry-picks well-known routes the single-page app serves, and forwards to that as opposed
     * to returning a 404.
     */
    // TODO This approach requires maintenance when new UI routes are added. Change to the following:
    // If the path is a a file w/an extension, treat normally.
    // Otherwise instead of returning 404, forward to the index.
    // See https://github.com/twitter/finatra/blob/458c6b639c3afb4e29873d123125eeeb2b02e2cd/http/src/main/scala/com/twitter/finatra/http/response/ResponseBuilder.scala#L321
    @RequestMapping(value = {"/zipkin/", "/zipkin/traces/{id}", "/zipkin/dependency","/zipkin"}, method = GET)
    public ModelAndView forwardUiEndpoints() {
        return new ModelAndView("forward:/zipkin/index.html");
    }

    /** The UI looks for the api relative to where it is mounted, under /zipkin */
    @RequestMapping(value = "/zipkin/api/v1/**", method = GET)
    public ModelAndView forwardApi(HttpServletRequest request) {
        String path = (String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return new ModelAndView("forward:" + path.replaceFirst("/zipkin", ""));
    }


}
