package net.juckel.jruby.sample.app;

import java.util.Hashtable;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.jruby.CompatVersion;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.PathType;
import org.jruby.embed.osgi.OSGiScriptingContainer;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ClassLoaderTest implements IApplication {

    @Override
    public Object start(IApplicationContext context) throws Exception {
        // Experiment: see if this bundle has access to IRuntime class
        Bundle b = FrameworkUtil.getBundle(this.getClass());
        @SuppressWarnings("unchecked") Class<IRuntime> iRuntimeClass = (Class<IRuntime>) b.loadClass("net.juckel.jruby.sample.app.IRuntime"); 
        
        // Using OSGiScriptingContainer instead of vanilla ScriptingContainer now that we are
        // running JRuby as an OSGi bundle.
        OSGiScriptingContainer scriptingContainer = new OSGiScriptingContainer(b,LocalContextScope.CONCURRENT, LocalVariableBehavior.TRANSIENT);
        scriptingContainer.setCompatVersion(CompatVersion.RUBY1_9);
        
        // Work-around JRUBY issue JRUBY-6265
        // scriptingContainer.runScriptlet("$: << '/opt/raritan/polaris/dynamic_plugin/lib';"); 

        // Experiment: Let's try to manually set hard-coded GEM_PATH
        // scriptingContainer.runScriptlet("ENV['GEM_PATH']='/opt/raritan/polaris/lib/configuration/org.eclipse.osgi/bundles/3/1/.cp/META-INF/jruby.home/lib/ruby/gems/shared'"); 
        
        // Experiment: Perform DynamicPluginManager functionality directly in Activator
        //    1. Create IRuntime object
        System.out.println("start: calling runScriptlet(bootstrap.rb)...");
        Object receiver = scriptingContainer.runScriptlet(b.getEntry("/script.rb").openStream(), "script.rb");
        System.out.println("start: mapping to IRuntime interface...");
        IRuntime runtime = scriptingContainer.getInstance(receiver, IRuntime.class);
        System.out.println("start:  IRuntime interface instance: " + runtime);
        System.out.println("3+4: " + runtime.calc(3,  4));
        return null;
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

}
