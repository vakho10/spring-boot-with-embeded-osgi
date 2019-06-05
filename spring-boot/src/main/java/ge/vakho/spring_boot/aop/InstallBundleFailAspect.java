//package ge.vakho.spring_boot.aop;
//
//import java.nio.file.Path;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Aspect;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import ge.vakho.spring_boot.service.BundleCleanupService;
//
//@Aspect
//@Component
//public class InstallBundleFailAspect {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(InstallBundleFailAspect.class);
//
//	@Autowired
//	private BundleCleanupService bundleCleanupService;
//	
//	@AfterThrowing(pointcut = "@annotation(InstallBundleFailMarker)", throwing = "ex")
//	public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
//
//		// Extract bundle path from first argument
//		Path bundlePath = (Path) joinPoint.getArgs()[0];
//		
//		LOGGER.error("Error occurred while installing bundle JAR: {}", bundlePath.getFileName());
//		
//		bundleCleanupService.removeJar(bundlePath);
//	}
//}
