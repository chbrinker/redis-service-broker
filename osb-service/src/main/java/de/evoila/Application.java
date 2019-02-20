/**
 * 
 */
package de.evoila;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cloud.bus.BusAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
@RefreshScope
@SpringBootApplication(exclude = { RabbitAutoConfiguration.class, BusAutoConfiguration.class })
public class Application {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(new ApplicationPidFileWriter());
        ApplicationContext ctx = springApplication.run(args);

        Assert.notNull(ctx, "ApplicationContext must not be null.");
    }

}