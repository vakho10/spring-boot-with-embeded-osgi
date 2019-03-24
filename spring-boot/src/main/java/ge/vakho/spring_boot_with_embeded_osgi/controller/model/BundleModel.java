package ge.vakho.spring_boot_with_embeded_osgi.controller.model;

public class BundleModel {

	private long id;
	private String name;
	private int state;

	public BundleModel() {
	}

	public BundleModel(long id, String name, int state) {
		this();
		this.id = id;
		this.name = name;
		this.state = state;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
