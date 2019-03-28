package ge.vakho.spring_boot.logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple logger which logs Felix's messages directly to Spring Boot.
 * 
 * @author v.laluashvili
 */
public class FelixLogger extends org.apache.felix.framework.Logger {

	private static final Logger LOGGER;

	static {
		LOGGER = LoggerFactory.getLogger(FelixLogger.class);
	}

	public FelixLogger() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void doLog(final Bundle bundle, final ServiceReference sr, final int level, final String msg,
			final Throwable throwable) {
		switch (level) {
		case LOG_DEBUG: {
			LOGGER.debug("[{}]: {}: ", bundle.getSymbolicName(), msg, throwable);
			break;
		}
		case LOG_ERROR: {
			LOGGER.error("[{}]: {}: ", bundle.getSymbolicName(), msg, throwable);
			break;
		}
		case LOG_INFO: {
			LOGGER.info("[{}]: {}: ", bundle.getSymbolicName(), msg, throwable);
			break;
		}
		case LOG_WARNING: {
			LOGGER.warn("[{}]: {}: ", bundle.getSymbolicName(), msg, throwable);
			break;
		}
		}
	}

	@Override
	protected void doLog(final int level, final String msg, final Throwable throwable) {
		switch (level) {
		case LOG_DEBUG: {
			LOGGER.debug("{}: ", msg, throwable);
			break;
		}
		case LOG_ERROR: {
			LOGGER.error("{}: ", msg, throwable);
			break;
		}
		case LOG_INFO: {
			LOGGER.info("{}: ", msg, throwable);
			break;
		}
		case LOG_WARNING: {
			LOGGER.warn("{}: ", msg, throwable);
			break;
		}
		}
	}
}