package ge.vakho.logger_service;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An SLF4J-based log reader.
 * 
 * @author v.laluashvili
 */
public final class SLF4JLogReader implements LogListener {
	
	private static final Logger LOG;

	static {
		LOG = LoggerFactory.getLogger(SLF4JLogReader.class);
	}

	SLF4JLogReader() {
	}

	@Override
	public void logged(final LogEntry entry) {
		final int level = entry.getLevel();
		switch (level) {
		case LogService.LOG_DEBUG: {
			SLF4JLogReader.debug(entry);
			break;
		}
		case LogService.LOG_ERROR: {
			SLF4JLogReader.error(entry);
			break;
		}
		case LogService.LOG_INFO: {
			SLF4JLogReader.info(entry);
			break;
		}
		case LogService.LOG_WARNING: {
			SLF4JLogReader.warn(entry);
			break;
		}
		default: {
			SLF4JLogReader.warn(entry);
			break;
		}
		}
	}

	private static void warn(final LogEntry entry) {
		final String name = entry.getBundle().getSymbolicName();
		final String message = entry.getMessage();
		final Throwable ex = entry.getException();
		if (ex != null) {
			SLF4JLogReader.LOG.warn("[{}]: {}: ", name, message, ex);
		} else {
			SLF4JLogReader.LOG.warn("[{}]: {}", name, message);
		}
	}

	private static void info(final LogEntry entry) {
		final String name = entry.getBundle().getSymbolicName();
		final String message = entry.getMessage();
		final Throwable ex = entry.getException();
		if (ex != null) {
			SLF4JLogReader.LOG.info("[{}]: {}: ", name, message, ex);
		} else {
			SLF4JLogReader.LOG.info("[{}]: {}", name, message);
		}
	}

	private static void error(final LogEntry entry) {
		final String name = entry.getBundle().getSymbolicName();
		final String message = entry.getMessage();
		final Throwable ex = entry.getException();
		if (ex != null) {
			SLF4JLogReader.LOG.error("[{}]: {}: ", name, message, ex);
		} else {
			SLF4JLogReader.LOG.error("[{}]: {}", name, message);
		}
	}

	private static void debug(final LogEntry entry) {
		final String name = entry.getBundle().getSymbolicName();
		final String message = entry.getMessage();
		final Throwable ex = entry.getException();
		if (ex != null) {
			SLF4JLogReader.LOG.debug("[{}]: {}: ", name, message, ex);
		} else {
			SLF4JLogReader.LOG.debug("[{}]: {}", name, message);
		}
	}
}
