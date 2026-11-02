package com.jobhunter.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JobhunterApiApplicationTests extends AbstractIntegrationTest {


	@Test
	void contextLoads() {
		assertTrue(MYSQL.isRunning());
	}

}
