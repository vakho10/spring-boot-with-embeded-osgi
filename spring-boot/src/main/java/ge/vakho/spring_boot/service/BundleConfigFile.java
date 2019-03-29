package ge.vakho.spring_boot.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ge.vakho.spring_boot.property.BundleProperties;

@Service
@EnableConfigurationProperties(BundleProperties.class)
public class BundleConfigFile {

	private static final Logger LOGGER = LoggerFactory.getLogger(BundleConfigFile.class);
	
	private final ObjectMapper mapper = new ObjectMapper();
	private final Map<Long, String> entries = new TreeMap<Long, String>();
	private final BundleProperties bundleProperties; 
	
	public BundleConfigFile(BundleProperties bundleProperties) {
		this.bundleProperties = bundleProperties;
	}

	public void insert(Map<Long, String> entries) {
		this.entries.putAll(entries);
		save();
	}
	
	public void insert(long bundleId, String fileName) {
		entries.put(bundleId, fileName);
		save();		
	}
	
	public boolean removeBy(String fileName) {
		boolean removed = false;
		for (Long bundleId : entries.keySet()) {
			if (entries.get(bundleId).equals(fileName)) {
				entries.remove(bundleId);
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
		if (entries.remove(bundleId) != null) {
			removed = true;
			save();
		}
		return removed;
	}
	
	private void save() {
		try {
			mapper.writeValue(bundleProperties.getConfigFilePath().toFile(), new HashSet<String>(entries.values()));
		} catch (IOException e) {			
			LOGGER.error(e.getMessage(), e);
		}
	}

	public String findBy(long bundleId) {
		return entries.get(bundleId);
	}

	
}