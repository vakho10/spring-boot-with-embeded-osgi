//package ge.vakho.spring_boot.aop;
//
//import java.nio.file.Path;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Aspect;
//import org.osgi.framework.Bundle;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import ge.vakho.spring_boot.service.BundleCleanupService;
//
//@Aspect
//@Component
//public class InsertBundleEntryFailAspect {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(InsertBundleEntryFailAspect.class);
//
//	@Autowired
//	private BundleCleanupService bundleCleanupService;
//	
//	@AfterThrowing(pointcut = "@annotation(InsertBundleEntryFailMarker)", throwing = "ex")
//	public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
//
//		// Extract bundle object and bundle path from arguments
//		Bundle newInstalledBundle = (Bundle) joinPoint.getArgs()[0];
//		Path bundlePath = (Path) joinPoint.getArgs()[1];
//		
//		LOGGER.error("Error occurred while inserting bundle: {} into configuration file", newInstalledBundle.getSymbolicName());
//		
//		// Uninstall from framework
//		bundleCleanupService.uninstallBundle(newInstalledBundle);
//		
//		// Remove JAR file from the bundles' directory
//		bundleCleanupService.removeJar(bundlePath);
//	}
//}
