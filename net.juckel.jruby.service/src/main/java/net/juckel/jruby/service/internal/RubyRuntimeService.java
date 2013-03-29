package net.juckel.jruby.service.internal;

import java.util.HashMap;
import java.util.Map;

import net.juckel.jruby.service.IRubyRuntimeService;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.osgi.OSGiScriptingContainer;
import org.osgi.framework.FrameworkUtil;

public class RubyRuntimeService implements IRubyRuntimeService {
	private Map<String, ScriptingContainer> runtimes;
	public RubyRuntimeService() {
		runtimes = new HashMap<String, ScriptingContainer>();
	}

	@Override
	public ScriptingContainer createRuby(String engineName, Class<?> clazz) {
		ScriptingContainer ruby = null;
		synchronized(runtimes) {
			ruby = runtimes.get(engineName);
			if (null == ruby) {
                OSGiScriptingContainer container = new OSGiScriptingContainer(
                        FrameworkUtil.getBundle(clazz), LocalContextScope.CONCURRENT,
                        LocalVariableBehavior.TRANSIENT);
				runtimes.put(engineName, container);
				ruby = container;
			}
		}
		return ruby;
	}
}
