package ge.vakho.spring_boot.configuration.model;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "bundleFolder", "bundleEntries" })
public class BundleConfigurationFile {

	private File bundleFolder;	
	private List<Entry> bundleEntries;

	public File getBundleFolder() {
		return bundleFolder;
	}

	public void setBundleFolder(File bundleFolder) {
		this.bundleFolder = bundleFolder;
	}

	public List<Entry> getBundleEntries() {
		return bundleEntries;
	}

	public void setBundleEntries(List<Entry> bundleEntries) {
		this.bundleEntries = bundleEntries;
	}
	
	@JsonPropertyOrder({ "fileName", "state" })
	public static class Entry {

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

	}
	
}