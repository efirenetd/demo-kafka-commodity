package org.efire.net;

import org.efire.net.entity.Image;
import org.efire.net.service.ImageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProducerApplication implements CommandLineRunner {

	@Autowired
	private ImageProducer imageProducer;

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
/*		var jpg = Image.builder("jpg").build();
		var png = Image.builder("png").build();  // this it to throw exception
		var svg = Image.builder("svg").build();*/

		//This is for DLQ demo
		for (int i = 0; i < 10; i++) {
			var png = Image.builder("png").build();
			imageProducer.sendImage(png);
		}
	}

}
