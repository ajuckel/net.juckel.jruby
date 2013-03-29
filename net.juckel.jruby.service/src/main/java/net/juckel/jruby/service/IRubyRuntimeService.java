package net.juckel.jruby.service;

import org.jruby.embed.ScriptingContainer;

public interface IRubyRuntimeService {
	ScriptingContainer createRuby(String engineName, Class<?> clazz);
}
