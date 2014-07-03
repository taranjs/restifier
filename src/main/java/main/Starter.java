package main;

import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * Main class
 * @author taranjeet
 *
 */
public class Starter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("works");
		try {
	        // Create a new Component.
	        Component component = new Component();

	        // Add a new HTTP server listening on port 16999.
	        component.getServers().add(Protocol.HTTP, 16999);

	        // Attach the sample application.
	        component.getDefaultHost().attach(new Rester());

	        // Start the component.
	        component.start();
	    } catch (Exception e) {
	        // Something is wrong.
	        e.printStackTrace();
	    }
	}

}
