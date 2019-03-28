package ge.vakho.spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @see <a href="https://stackoverflow.com/questions/41721988/how-to-programmatically-start-osgi">Link</a>
 * 
 * @author v.laluashvili
 *
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}