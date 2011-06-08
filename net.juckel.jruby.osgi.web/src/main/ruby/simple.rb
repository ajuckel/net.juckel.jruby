require 'java'
require 'irb'
require 'irb/completion'

import 'net.juckel.jruby.osgi.StupidClass'

#StupidClass = JavaUtilities.create_proxy_class(
#  'StupidClass',
#  JavaUtilities.get_java_class('net.juckel.jruby.osgi.StupidClass', $foo),
#  JavaUtilities.get_proxy_or_package_under_package('net.juckel.jruby.osgi', $foo))
#puts "What is StupidClass? #{StupidClass.class}, #{StupidClass}"
#c = StupidClass.new
#puts "Value #{c.stuff}"

IRB.start
1
