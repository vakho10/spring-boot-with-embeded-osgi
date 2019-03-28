package ge.vakho.spring_boot.property;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgi.properties")
@ConfigurationProperties(prefix = "osgi")
public class OsgiProperties {

	private String logLevel;
	private List<String> extraPackages;

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public List<String> getExtraPackages() {
		return extraPackages;
	}

	public void setExtraPackages(List<String> extraPackages) {
		this.extraPackages = extraPackages;
	}

}