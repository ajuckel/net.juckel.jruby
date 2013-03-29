package net.juckel.jruby.sample.app;

import net.juckel.jruby.service.IRubyRuntimeService;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.jruby.embed.ScriptingContainer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

public class JRubySampleApplication implements IApplication {

    private ServiceTracker tracker;

    @Override
    public Object start(IApplicationContext context) throws Exception {
        BundleContext bc = FrameworkUtil.getBundle(JRubySampleApplication.class).getBundleContext();
        tracker = new ServiceTracker(bc, IRubyRuntimeService.class.getName(), null);
        tracker.open();
        IRubyRuntimeService service = (IRubyRuntimeService) tracker.waitForService(1000);
        if (null == service) {
            System.err.println("Couldn't find runtime service");
            return Integer.valueOf(100);
        }

        ScriptingContainer container = service.createRuby("sample", this.getClass());
        container.runScriptlet(bc.getBundle().getEntry("/script.rb").openStream(), "script.rb");
        Object obj = container.runScriptlet("require 'rubygems'; Gem.path()");
        System.out.println("Ruby result: " + obj);
        return Integer.valueOf(0);
    }

    @Override
    public void stop() {
        tracker.close();
        tracker = null;
    }
}
