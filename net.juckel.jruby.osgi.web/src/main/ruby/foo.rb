require 'rubygems'
puts "GEM_HOME=#{ENV['GEM_HOME']}"
begin
  require 'rubygems'
  require '/WEB-INF/gems/gems/jruby-rack-1.0.9/lib/jruby-rack.rb'
  puts "WHA?!"
  require 'rack/handler/servlet'
  puts "FER REALZ?!"
  gem 'jruby-rack'
  puts "shizzle"
rescue LoadError => e
  puts e
end