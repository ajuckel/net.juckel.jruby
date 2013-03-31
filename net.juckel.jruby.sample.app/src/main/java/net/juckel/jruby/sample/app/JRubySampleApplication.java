package net.juckel.jruby.sample.app;

import java.io.InputStreamReader;

import javax.script.ScriptEngine;

import net.juckel.osgi.scripting.IScriptEngineFactoryService;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

public class JRubySampleApplication implements IApplication {

    private ServiceTracker tracker;

    @Override
    public Object start(IApplicationContext context) throws Exception {
        BundleContext bc = FrameworkUtil.getBundle(JRubySampleApplication.class).getBundleContext();
        tracker = new ServiceTracker(bc, IScriptEngineFactoryService.class.getName(), null);
        tracker.open();
        IScriptEngineFactoryService service = (IScriptEngineFactoryService) tracker.waitForService(1000);
        if (null == service) {
            System.err.println("Couldn't find runtime service");
            return Integer.valueOf(100);
        }

        ScriptEngine engine = service.getEngineByExtension("rb", bc.getBundle());
        if (null == engine) {
            System.err.println("Couldn't find appropriate engine");
            return Integer.valueOf(100);
        }
        engine.eval(new InputStreamReader(bc.getBundle().getEntry("/script.rb").openStream()));
        Object obj = engine.eval("require 'rubygems'; Gem.path()");
        System.out.println("Ruby result: " + obj);
        return Integer.valueOf(0);
    }

    @Override
    public void stop() {
        tracker.close();
        tracker = null;
    }
}
