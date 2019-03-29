package ge.vakho.spring_boot.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ge.vakho.spring_boot.property.BundleProperties;

@Component
@EnableConfigurationProperties(BundleProperties.class)
public class BundleConfigFile {

	private static final Logger LOGGER = LoggerFactory.getLogger(BundleConfigFile.class);

	private final ObjectMapper mapper = new ObjectMapper();
	private final BundleProperties bundleProperties;

	private final List<Entry> entries = new ArrayList<>();

	public BundleConfigFile(BundleProperties bundleProperties) {
		this.bundleProperties = bundleProperties;
	}

	@PostConstruct
	public void init() throws JsonParseException, JsonMappingException, IOException {
		// Load from configuration file
		entries.addAll(
				mapper.readValue(bundleProperties.getConfigFilePath().toFile(), new TypeReference<List<Entry>>() {
				}));
	}

	public List<Entry> getEntries() {
		return entries;
	}
	
	public void insert(long bundleId, String fileName) {
		insert(bundleId, fileName, false);
	}
	
	public void insert(long bundleId, String fileName, boolean forceStart) {
		Entry pair = new Entry(bundleId, fileName, forceStart);
		if (entries.contains(pair)) {
			return; // Ignore duplicate installation request!
		}
		removeBy(fileName); // Remove old file name entry
		entries.add(pair); // Add new entry
		save();
	}

	public boolean removeBy(String fileName) {
		Optional<Entry> foundPair = entries.parallelStream() //
				.filter(i -> i.fileName.equals(fileName)) //
				.findAny();
		if (foundPair.isPresent()) {
			entries.remove(foundPair.get()); // Remove old entry
			save();
			return true;
		}
		return false;
	}

	public boolean removeBy(long bundleId) {
		Optional<Entry> foundPair = entries.parallelStream() //
				.filter(i -> i.bundleId == bundleId) //
				.findAny();
		if (foundPair.isPresent()) {
			entries.remove(foundPair.get()); // Remove old entry
			save();
			return true;
		}
		return false;
	}

	public String getFileNameBy(long bundleId) {
		Entry pair = getBy(bundleId);
		if (pair != null) {
			return pair.getFileName();
		}
		return null;
	}
	
	public Entry getBy(long bundleId) {
		return entries.parallelStream() //
				.filter(i -> i.bundleId == bundleId) //
				.findAny() //
				.get();
	}

	public Long getBundleIdBy(String fileName) {
		Entry pair = getBy(fileName);
		if (pair != null) {
			return pair.getBundleId();
		}
		return null;
	}
	
	public Entry getBy(String fileName) {
		return entries.parallelStream() //
				.filter(i -> i.fileName.equals(fileName)) //
				.findAny() //
				.get();
	}

	public void setForceStartTo(long bundleId, boolean forceStart) {
		Entry entry = getBy(bundleId);
		if (entry == null) {
			return;
		}
		entry.setForceStart(forceStart);
		save();
	}
	
	private void save() {
		try {
			mapper.writeValue(bundleProperties.getConfigFilePath().toFile(), entries);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@JsonPropertyOrder({ "fileName", "state" })
	public static final class Entry {

		@JsonIgnore
		private long bundleId;
		
		private String fileName;
		private boolean forceStart;

		public Entry() {
		}

		public Entry(long bundleId, String fileName, boolean forceStart) {
			this();
			this.bundleId = bundleId;
			this.fileName = fileName;
			this.forceStart = forceStart;
		}

		public long getBundleId() {
			return bundleId;
		}

		public void setBundleId(long bundleId) {
			this.bundleId = bundleId;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public boolean isForceStart() {
			return forceStart;
		}

		public void setForceStart(boolean forceStart) {
			this.forceStart = forceStart;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (bundleId ^ (bundleId >>> 32));
			result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
			result = prime * result + (forceStart ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Entry other = (Entry) obj;
			if (bundleId != other.bundleId)
				return false;
			if (fileName == null) {
				if (other.fileName != null)
					return false;
			} else if (!fileName.equals(other.fileName))
				return false;
			if (forceStart != other.forceStart)
				return false;
			return true;
		}
		
	}

}