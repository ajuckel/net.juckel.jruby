package net.juckel.jruby.osgi;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Application implements IApplication {

	private ServiceTracker<HttpService, HttpService> httpServiceTracker;
	private HttpContext httpContext;
	private volatile boolean keepRunning = true;

	//	@Override
	public Object start(IApplicationContext context) throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(Application.class);
		httpServiceTracker = new ServiceTracker<HttpService, HttpService>(bundle.getBundleContext(), HttpService.class.getName(), null);
		httpServiceTracker.open();
		HttpService httpService = (HttpService) httpServiceTracker.getService();
		httpContext = new OSGiHttpContext(bundle);
		OsgiRackServlet servlet = new OsgiRackServlet(bundle);
		Hashtable<String, String> initParams = new Hashtable<String, String>();
		initParams.put("jruby.compat.version", "1.8");
		httpService.registerResources("/", "/WEB-INF/static", httpContext);
		httpService.registerServlet("/services", servlet, initParams, httpContext);
		while(keepRunning) {
			try {
				Thread.sleep(10);
			} catch(InterruptedException ex) {
				return Integer.valueOf(0);
			}
		}
		return Integer.valueOf(0);
	}

//	@Override
	public void stop() {
		httpServiceTracker.close();
		httpServiceTracker = null;
		keepRunning = false;
	}
	
	private class OSGiHttpContext implements HttpContext {
		private Bundle bundle;
		
		public OSGiHttpContext(Bundle bundle) {
			this.bundle = bundle;
		}

		@Override
		public boolean handleSecurity(HttpServletRequest request,
				HttpServletResponse response) throws IOException {
			return true;
		}

		@Override
		public URL getResource(String name) {
			return bundle.getResource(name);
		}

		@Override
		public String getMimeType(String name) {
			return null;
		}
		
		@SuppressWarnings("unused")
		public Set<String> getResourcePaths(String path) {
			Enumeration<String> strings = bundle.getEntryPaths(path);
			Set<String> stringSet = new HashSet<String>();
			while(strings.hasMoreElements()) {
				stringSet.add("/" + strings.nextElement());
			}
			return stringSet;
		}
	}
}
