package ge.vakho.hello_service_rus;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import ge.vakho.hello_service_api.HelloService;
import ge.vakho.hello_service_rus.impl.HelloServiceRus;

public class Activator implements BundleActivator {

	private ServiceRegistration<HelloService> helloServiceRegistration;

	public void start(BundleContext context) throws Exception {
		helloServiceRegistration = context.registerService(HelloService.class, new HelloServiceRus(), null);
	}

	public void stop(BundleContext context) throws Exception {
		helloServiceRegistration.unregister();
	}
}