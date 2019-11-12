package org.cloud.console.client;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/11<br>
 * <br>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({EnableConsoleClientImportSelector.class})
public @interface EnableConsoleClient {
}
