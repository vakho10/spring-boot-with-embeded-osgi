package ge.vakho.spring_boot.property;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@Configuration
@PropertySource("classpath:osgi.properties")
@ConfigurationProperties(prefix = "osgi")
@Validated
public class OsgiProperties {

	@NotBlank
	private String logLevel;

	@NotEmpty
	private List<String> extraPackages;

	@NotNull
	private File bundleConfigFile;

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

	public File getBundleConfigFile() {
		return bundleConfigFile;
	}

	public void setBundleConfigFile(File bundleConfigFile) {
		this.bundleConfigFile = bundleConfigFile;
	}

}