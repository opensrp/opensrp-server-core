package org.opensrp.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class TestResourceLoader {
	
	protected String openmrsOpenmrsUrl;
	
	protected String openmrsUsername;
	
	protected String openmrsPassword;
	
	protected String formDirPath;
	
	protected boolean pushToOpenmrsForTest;
	
	public TestResourceLoader() {
		try {
			Resource resource = new ClassPathResource("test-opensrp.properties");
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			formDirPath = props.getProperty("form.directory.name");
		}
		catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		
	}
	
	protected JSONObject getJsonFormSubmissionFor(String formName, Integer number) throws IOException {
		ResourceLoader loader = new DefaultResourceLoader();
		String path = loader.getResource(formDirPath).getURI().getPath();
		File fsfile = new File(path + "/" + formName + "/form_submission" + (number == null ? "" : number) + ".json");
		return new JSONObject(new FileReader(fsfile));
	}
	
	protected String getFullPath(String fileName) throws IOException {
		ResourceLoader loader = new DefaultResourceLoader();
		String path = loader.getResource(fileName).getURI().getPath();
		return path;
	}
	
}
