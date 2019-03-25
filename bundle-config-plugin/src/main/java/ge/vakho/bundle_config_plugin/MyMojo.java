package ge.vakho.bundle_config_plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.google.gson.Gson;

/**
 * Generates config.json file with bundle entries into output directory.
 * 
 * @author v.laluashvili
 */
@Mojo(name = "generate")
public class MyMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	@Parameter(required = true)
	private String[] bundles;

	public void execute() throws MojoExecutionException {

		getLog().info("Generating bundle config.json file...");
		File f = outputDirectory;

		if (!f.exists()) {
			f.mkdirs();
			getLog().info("Created directory: " + f.getAbsolutePath());
		}

		File configFile = f.toPath().resolve("config.json").toFile();
		try (FileOutputStream fos = new FileOutputStream(configFile)){
			fos.write(new Gson().toJson(bundles).getBytes());
			fos.flush();
			getLog().info("Generated config.json file: " + configFile.getAbsolutePath());
		} catch (IOException e) {
			getLog().error(e.getMessage(), e);
		}
	}
}
