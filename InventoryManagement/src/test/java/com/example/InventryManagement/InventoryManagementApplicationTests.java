package com.example.InventryManagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import com.example.InventryManagement.config.TestSecurityConfig;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestSecurityConfig.class})
class InventoryManagementApplicationTests {

	@Test
	void contextLoads() {
		// This test will verify that the application context loads successfully
	}

}
