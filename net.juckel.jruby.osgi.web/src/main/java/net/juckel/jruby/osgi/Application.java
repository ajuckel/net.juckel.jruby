package net.juckel.jruby.osgi;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.jruby.embed.osgi.OSGiScriptingContainer;
import org.jruby.rack.DefaultRackDispatcher;
import org.jruby.rack.RackServlet;
import org.jruby.rack.servlet.ServletRackConfig;
import org.jruby.rack.servlet.ServletRackContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

public class Application implements IApplication, ServletContextListener {

	private ServiceTracker<HttpService, HttpService> httpServiceTracker;
	private HttpContext httpContext;

	//	@Override
	public Object start(IApplicationContext context) throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(Application.class);
		httpServiceTracker = new ServiceTracker<HttpService, HttpService>(bundle.getBundleContext(), HttpService.class.getName(), null);
		httpServiceTracker.open();
		HttpService httpService = (HttpService) httpServiceTracker.getService();
		httpContext = httpService.createDefaultHttpContext();
		System.out.println("!!!!!! <<>> " + this.getClass().getResource("/rack/handler/servlet.rb"));
		// Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		OSGiScriptingContainer engine = new OSGiScriptingContainer(bundle);
		Map<String, String> environ = new HashMap<String, String>(engine.getEnvironment());
		environ.put("GEM_HOME", "/WEB-INF/gems/");
		engine.setEnvironment(environ);
		engine.runScriptlet(bundle, "/src/main/ruby/foo.rb");
		OsgiRackServlet servlet = new OsgiRackServlet();
		// ServletContext ctx = servlet.getServletContext();
		Hashtable<String, String> initParams = new Hashtable<String, String>();
		initParams.put("jruby.compat.version", "1.9");
		httpService.registerServlet("/foo", servlet, initParams, httpContext);
//		ServletRackContext rackContext = new ServletRackContext(new ServletRackConfig(null));
//		RackServlet rackServlet = new RackServlet(new DefaultRackDispatcher(rackContext));
//		httpService.registerServlet("/rack", new RackServlet(new DefaultRackDispatcher(rackContext)), null, httpContext);
//		httpService.registerServlet("/rack", new RackServlet(new DefaultRackDispatcher(rackContext)), null, httpContext);
		engine.put("foo", this.getClass().getClassLoader());
		engine.put("out", System.out);
		try {
			String scriptPath = "/src/main/ruby/simple.rb";
			Object returnValue =  engine.runScriptlet(bundle.getSymbolicName(), scriptPath);
			System.out.println("Value: " + returnValue);
			return Integer.valueOf(0);
		} catch ( Exception e ) {
			e.printStackTrace();
			throw e;
		}
	}

//	@Override
	public void stop() {
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		HttpService httpService = (HttpService) httpServiceTracker.getService();
		ServletRackContext rackContext = new ServletRackContext(new ServletRackConfig(sce.getServletContext()));
		RackServlet rackServlet = new RackServlet(new DefaultRackDispatcher(rackContext));
		try {
			httpService.registerServlet("/rack", rackServlet, null, httpContext);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamespaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
}
