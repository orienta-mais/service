package umc.pfc.orientamais;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrientamaisApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrientamaisApplication.class, args);
  }
}
