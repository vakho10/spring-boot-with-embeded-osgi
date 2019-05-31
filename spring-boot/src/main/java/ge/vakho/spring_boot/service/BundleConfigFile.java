package ge.vakho.spring_boot.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ge.vakho.spring_boot.configuration.model.BundleConfigurationFile;
import ge.vakho.spring_boot.property.OsgiProperties;

@Component
public class BundleConfigFile {

	private static final Logger LOGGER = LoggerFactory.getLogger(BundleConfigFile.class);

	private final OsgiProperties osgiProperties;
	private final ObjectMapper mapper = new ObjectMapper();

	private BundleConfigurationFile configurationFileModel;

	@Autowired
	public BundleConfigFile(OsgiProperties osgiProperties) {
		this.osgiProperties = osgiProperties;		
	}
	
	@PostConstruct
	private void init() throws JsonParseException, JsonMappingException, IOException {
		configurationFileModel = mapper.readValue(osgiProperties.getBundleConfigFile(), BundleConfigurationFile.class);
	}

	public List<BundleConfigurationFile.Entry> getEntries() {
		return configurationFileModel.getBundleEntries();
	}
	
	public void insert(long bundleId, String fileName) {
		insert(bundleId, fileName, false);
	}
	
	public void insert(long bundleId, String fileName, boolean forceStart) {
		BundleConfigurationFile.Entry pair = new BundleConfigurationFile.Entry(bundleId, fileName, forceStart);
		if (configurationFileModel.getBundleEntries().contains(pair)) {
			return; // Ignore duplicate installation request!
		}
		removeBy(fileName); // Remove old file name entry
		configurationFileModel.getBundleEntries().add(pair); // Add new entry
		save();
	}

	public boolean removeBy(String fileName) {
		Optional<BundleConfigurationFile.Entry> foundPair = configurationFileModel.getBundleEntries().parallelStream() //
				.filter(i -> i.getFileName().equals(fileName)) //
				.findAny();
		if (foundPair.isPresent()) {
			configurationFileModel.getBundleEntries().remove(foundPair.get()); // Remove old entry
			save();
			return true;
		}
		return false;
	}

	public boolean removeBy(long bundleId) {
		Optional<BundleConfigurationFile.Entry> foundPair = configurationFileModel.getBundleEntries().parallelStream() //
				.filter(i -> i.getBundleId() == bundleId) //
				.findAny();
		if (foundPair.isPresent()) {
			configurationFileModel.getBundleEntries().remove(foundPair.get()); // Remove old entry
			save();
			return true;
		}
		return false;
	}

	public String getFileNameBy(long bundleId) {
		BundleConfigurationFile.Entry pair = getBy(bundleId);
		if (pair != null) {
			return pair.getFileName();
		}
		return null;
	}
	
	public BundleConfigurationFile.Entry getBy(long bundleId) {
		return configurationFileModel.getBundleEntries().parallelStream() //
				.filter(i -> i.getBundleId() == bundleId) //
				.findAny() //
				.get();
	}

	public Long getBundleIdBy(String fileName) {
		BundleConfigurationFile.Entry pair = getBy(fileName);
		if (pair != null) {
			return pair.getBundleId();
		}
		return null;
	}
	
	public BundleConfigurationFile.Entry getBy(String fileName) {
		return configurationFileModel.getBundleEntries().parallelStream() //
				.filter(i -> i.getFileName().equals(fileName)) //
				.findAny() //
				.get();
	}

	public void setForceStartTo(long bundleId, boolean forceStart) {
		BundleConfigurationFile.Entry entry = getBy(bundleId);
		if (entry == null) {
			return;
		}
		entry.setForceStart(forceStart);
		save();
	}
	
	private void save() {
		try {
			mapper.writeValue(osgiProperties.getBundleConfigFile(), configurationFileModel);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}