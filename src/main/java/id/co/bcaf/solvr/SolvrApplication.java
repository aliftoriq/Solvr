package id.co.bcaf.solvr;

import id.co.bcaf.solvr.config.cloudinary.CloudinaryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class SolvrApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolvrApplication.class, args);
		// System.out.println("HALO");

	}

}
