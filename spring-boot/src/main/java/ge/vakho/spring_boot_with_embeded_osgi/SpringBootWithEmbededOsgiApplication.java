package ge.vakho.spring_boot_with_embeded_osgi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @see <a href="https://stackoverflow.com/questions/41721988/how-to-programmatically-start-osgi">Link</a>
 * 
 * @author v.laluashvili
 *
 */
@SpringBootApplication
public class SpringBootWithEmbededOsgiApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringBootWithEmbededOsgiApplication.class, args);
	}
}