# -*- encoding: utf-8 -*-

Gem::Specification.new do |s|
  s.name = %q{jruby-rack}
  s.version = "1.0.9"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Nick Sieger"]
  s.date = %q{2011-06-24}
  s.description = %q{JRuby-Rack is a combined Java and Ruby library that adapts the Java Servlet API to Rack. For JRuby only.}
  s.email = ["nick@nicksieger.com"]
  s.files = ["History.txt", "LICENSE.txt", "README.md", "lib/jruby-rack-1.0.9.jar", "lib/jruby-rack.rb", "lib/jruby/rack/version.rb"]
  s.homepage = %q{http://jruby.org}
  s.require_paths = ["lib"]
  s.rubyforge_project = %q{jruby-extras}
  s.rubygems_version = %q{1.5.1}
  s.summary = %q{Rack adapter for JRuby and Servlet Containers}

  if s.respond_to? :specification_version then
    s.specification_version = 3

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
    else
    end
  else
  end
end
