package demo;

import org.junit.rules.ExternalResource;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class EmbeddedDataStore extends ExternalResource {
	private static LocalServiceTestHelper helper;

	static {
		LocalDatastoreServiceTestConfig localDatastoreServiceTestConfig = new LocalDatastoreServiceTestConfig();

		localDatastoreServiceTestConfig.setBackingStoreLocation("local_db.bin");
		localDatastoreServiceTestConfig.setNoStorage(false);

		helper = new LocalServiceTestHelper(localDatastoreServiceTestConfig);
//		String xml;
//		try {
//			xml = new String(readAllBytes(get("src/main/webapp/WEB-INF/appengine-web.xml")));
////			String applicationId = xml.substring(xml.indexOf("<application>") + 13, xml.indexOf("</application>"));
////			String version = xml.substring(xml.indexOf("<version>") + 9, xml.indexOf("</version>"));
//			// TODO : hardcoding now for simplicity. 
//			// Need to get those values from pom.xml, since appengine-web.xml contains tokens which are filtered by maven war plugin
////			helper.setEnvAppId(applicationId);
////			helper.setEnvVersionId(version);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		helper.setEnvAppId("psychic-city-78613");
		helper.setEnvVersionId("1.0.1");
	}

	@Override
	protected void before() throws Throwable {

		// ,
		// new LocalBlobstoreServiceTestConfig(),
		// new LocalTaskQueueTestConfig(),
		// new LocalMemcacheServiceTestConfig()
		// );
		helper.setUp();
	}

	@Override
	protected void after() {
		helper.tearDown();
	}
}