package net.juckel.jruby.osgi;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jruby.rack.DefaultRackApplicationFactory;
import org.jruby.rack.RackApplicationFactory;
import org.jruby.rack.RackServlet;
import org.jruby.rack.SharedRackApplicationFactory;
import org.jruby.rack.servlet.ServletRackConfig;
import org.jruby.rack.servlet.ServletRackContext;

@SuppressWarnings("serial")
public class OsgiRackServlet extends RackServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getOutputStream().println("<html><head><title>Foo!</title></head><body><p>Foo!</p>");
	}

	@Override
	public void init(ServletConfig servletConfig) {
        ServletContext ctx = servletConfig.getServletContext();
        ServletRackConfig config = new ServletRackConfig(ctx);
        final RackApplicationFactory fac = new SharedRackApplicationFactory(new DefaultRackApplicationFactory());
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
