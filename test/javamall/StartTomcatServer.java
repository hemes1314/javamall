package javamall;
import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

public class StartTomcatServer {

	public static void main(String[] args) throws Exception {
		String webappDirLocation = "WebContent/";
		Tomcat tomcat = new Tomcat();
		tomcat.getConnector().setURIEncoding("UTF-8");
		tomcat.setBaseDir(".");   
		tomcat.setPort(8080);

		Context context = tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        File configFile = new File(webappDirLocation + "WEB-INF/web.xml");
        context.setConfigFile(configFile.toURI().toURL());
		        
		long startTime = System.currentTimeMillis(); 
		tomcat.start(); 
		System.out.println("display-name:" + context.getDisplayName());
		System.out.println("Embedded Tomcat started in " + (System.currentTimeMillis() - startTime) + " ms."); 

        tomcat.getServer().await();
	}
}