package ge.vakho.spring_boot.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ge.vakho.spring_boot.property.BundleProperties;

@Component
@EnableConfigurationProperties(BundleProperties.class)
public class BundleConfigFile {

	private static final Logger LOGGER = LoggerFactory.getLogger(BundleConfigFile.class);
	
	private final ObjectMapper mapper = new ObjectMapper();
	private final BundleProperties bundleProperties;

	private final List<Long> bundleIds = new ArrayList<>(); 
	private final List<String> fileNames = new ArrayList<>();
	
	public BundleConfigFile(BundleProperties bundleProperties) {
		this.bundleProperties = bundleProperties;
	}

	public void bashInsert(List<Long> installedBundleIds, List<String> installedFileNames) {
		
		if (installedBundleIds.size() != installedFileNames.size()) {
			throw new IllegalArgumentException("Two array sizes differ!");
		}
		
		bundleIds.addAll(installedBundleIds);
		fileNames.addAll(installedFileNames);
	}
	
	public void insert(long bundleId, String fileName) {
		if (fileNames.contains(fileName)) {
			if (bundleIds.contains(bundleId)) {
				return; // Ignore duplicate installation request!
			}
			// Remove old bundle id
			bundleIds.remove(findBy(bundleId));
		} else {
			fileNames.add(fileName);			
		}
		bundleIds.add(bundleId);
		save();		
	}
	
	public boolean removeBy(String fileName) {
		boolean removed = false;
		for (int i = fileNames.size() - 1; i >= 0; i--) {
			if (fileNames.get(i).equals(fileName)) {
				bundleIds.remove(i);
				fileNames.remove(i);
				removed = true;
			}
		}
		if (removed) {
			save();
		}
		return removed;
	}
	
	public boolean removeBy(long bundleId) {
		boolean removed = false;
		for (int i = bundleIds.size() - 1; i >= 0; i--) {
			if (bundleIds.get(i).equals(bundleId)) {
				bundleIds.remove(i);
				fileNames.remove(i);
				removed = true;
			}
		}
		if (removed) {
			save();
		}
		return removed;
	}
	
	public String findBy(long bundleId) {
		for (int i = 0; i < bundleIds.size(); i++) {
			if (bundleIds.get(i).equals(bundleId)) {
				return fileNames.get(i);
			}
		}
		return null;
	}
	
	public Long findBy(String fileName) {
		for (int i = 0; i < fileNames.size(); i++) {
			if (fileNames.get(i).equals(fileName)) {
				return bundleIds.get(i);
			}
		}
		return null;
	}

	private void save() {
		try {
			mapper.writeValue(bundleProperties.getConfigFilePath().toFile(), fileNames);
		} catch (IOException e) {			
			LOGGER.error(e.getMessage(), e);
		}
	}
}