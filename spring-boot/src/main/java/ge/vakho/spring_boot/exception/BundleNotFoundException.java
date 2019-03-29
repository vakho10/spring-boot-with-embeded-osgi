package ge.vakho.spring_boot.exception;

@SuppressWarnings("serial")
public class BundleNotFoundException extends Exception {

	public BundleNotFoundException(long bundleId) {
		super("Bundle with id: " + bundleId + " coundn't be found!");
	}

}