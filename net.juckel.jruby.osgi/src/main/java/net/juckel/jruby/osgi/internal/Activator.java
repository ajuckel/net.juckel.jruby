package net.juckel.jruby.osgi.internal;

import net.juckel.jruby.osgi.extender.JRubyGemExtender;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
    private JRubyGemExtender extender;
    private ServiceTracker<LogService, LogService> logServiceTracker;
    private static Activator plugin;

    public static Activator getDefault() {
        return plugin;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        this.logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
        this.logServiceTracker.open();
        this.extender = new JRubyGemExtender();
        context.addBundleListener(this.extender);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        context.removeBundleListener(this.extender);
        this.logServiceTracker.close();
        this.logServiceTracker = null;
        this.extender = null;
    }

    public void log(int logLevel, String message) {
        LogService service = this.logServiceTracker.getService();
        if (service != null) {
            service.log(logLevel, message);
        }
    }
}
