package net.juckel.jruby.osgi.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.jruby.rack.OSGiRackApplicationFactory;
import org.jruby.rack.RackApplicationFactory;
import org.jruby.rack.RackServlet;
import org.jruby.rack.SharedRackApplicationFactory;
import org.jruby.rack.servlet.ServletRackConfig;
import org.jruby.rack.servlet.ServletRackContext;
import org.osgi.framework.Bundle;

@SuppressWarnings("serial")
public class OsgiRackServlet extends RackServlet {
	private Bundle bundle;
	public OsgiRackServlet(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public void init(ServletConfig servletConfig) {
        ServletContext ctx = servletConfig.getServletContext();
        ServletRackConfig config = new ServletRackConfig(ctx);
        final RackApplicationFactory fac = new SharedRackApplicationFactory(new OSGiRackApplicationFactory(bundle, this));
        ctx.setAttribute(RackApplicationFactory.FACTORY, fac);
        ServletRackContext rackContext = new ServletRackContext(config);
        ctx.setAttribute(RackApplicationFactory.RACK_CONTEXT, rackContext);
        try {
            fac.init(rackContext);
        } catch (Exception ex) {
            ctx.log("Error: application initialization failed", ex);
        }

		super.init(servletConfig);
	}
}
