package org.efire.net;

import org.efire.net.entity.Commodity;
import org.efire.net.service.CommodityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProducerApplicationTests {

	@Autowired
	private CommodityService commodityService;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldCreateDummyCommodity() {
		Commodity gold = commodityService.createDummyCommodity("gold");
		assertThat(gold.getName()).isEqualTo("gold");
	}
}
