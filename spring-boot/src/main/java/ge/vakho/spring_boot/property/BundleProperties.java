package ge.vakho.spring_boot.property;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:bundle.properties")
@ConfigurationProperties(prefix = "bundle")
public class BundleProperties {

	private Path folderPath;
	private String configFileName;

	public Path getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(Path folderPath) {
		if (!Files.exists(folderPath)) {
			throw new IllegalArgumentException("Bundles path doesn't exist!");
		}
		if (!Files.isDirectory(folderPath)) {
			throw new IllegalArgumentException("Bundles path should be a folder!");
		}
		this.folderPath = folderPath;
	}

	public String getConfigFileName() {
		Path configFilePath = getConfigFilePath();
		if (!Files.exists(configFilePath)) {
			throw new IllegalArgumentException("Bundles config file doesn't exist!");
		}
		if (!Files.isRegularFile(configFilePath)) {
			throw new IllegalArgumentException("Bundles config file is not a regular file!");
		}
		return configFileName;
	}
	
	public Path getConfigFilePath() {
		return folderPath.resolve(configFileName);
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

}