package ge.vakho.hello_service_geo;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ge.vakho.hello_service_api.HelloService;
import ge.vakho.hello_service_geo.impl.HelloServiceGeo;

public class Activator implements BundleActivator {

	private ServiceRegistration<HelloService> helloServiceRegistration;

	public void start(BundleContext context) throws Exception {
		helloServiceRegistration = context.registerService(HelloService.class, new HelloServiceGeo(), null);
	}

	public void stop(BundleContext context) throws Exception {
		helloServiceRegistration.unregister();
	}
}