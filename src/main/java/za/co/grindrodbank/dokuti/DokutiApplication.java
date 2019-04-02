/****************************************************
* Copyright (c) 2019, Grindrod Bank Limited
* License MIT: https://opensource.org/licenses/MIT
****************************************************/
package za.co.grindrodbank.dokuti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import za.co.grindrodbank.dokuti.configuration.SecurityProperties;
import za.co.grindrodbank.dokuti.service.documentdatastoreservice.StorageProperties;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class })
@EnableConfigurationProperties({ StorageProperties.class, SecurityProperties.class })
public class DokutiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DokutiApplication.class, args);
	}
}
