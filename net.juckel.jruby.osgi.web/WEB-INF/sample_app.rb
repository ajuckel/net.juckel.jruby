require 'rubygems'
require 'rack'
require 'erb'
require 'java'
require 'osgi'

import 'java.util.Scanner'
import 'java.util.concurrent.atomic.AtomicInteger'

class SampleApp
  attr_reader :path
  osgi_service :calculator, $bundle.bundle_context, "net.juckel.jruby.osgi.web.ICalculator"
  def initialize(path)
    @path = path
    @bundle_template = ERB.new slurp("/WEB-INF/views/bundle.rhtml")
    @result_template = ERB.new slurp("/WEB-INF/views/app.rhtml")
  end
  
  def call(env)
    case env["REQUEST_URI"]
    when /\/#{path}\/start\/(.*)$/ then show_template @bundle_template, start_bundle($1)
    when /\/#{path}\/stop\/(.*)$/ then show_template @bundle_template, stop_bundle($1)
    when /\/#{path}\/fact\/(\d+)$/ then show_template @result_template, get_factorial($1.to_i)
    when /\/#{path}\/ruby\/fib\/(\d+)$/ then show_template @result_template, fib($1.to_i)
    when /\/#{path}\/fib\/(\d+)$/ then show_template @result_template, get_fibonacci($1.to_i)
    else
      show_template @result_template, get_add(3, 5)
    end
  end

  def show_template(page, value)
    [value.nil? ? 204 : 200, {"Content-type" => "text/html"}, page.result(binding) ]
  end

  def get_factorial(num)
    calculator.factorial(num)
  end

  def get_fibonacci(num)
    calculator.fibonacci(num)
  end

  def fib(num)
    return nil if num < 0
    case num
    when 0 then 0
    when 1 then 1
    else fib(num - 2) + fib(num - 1) 
    end  
  end

  def get_add(x, y)
    calculator.add(x, y)
  end
  
  def start_bundle(bundle_symbolic_name)
    $bundle.bundle_context.bundles.each do |b|
      if b.symbolic_name == bundle_symbolic_name
        b.start
        return "Started #{bundle_symbolic_name}"
      end
    end
    return "Couldn't find bundle #{bundle_symbolic_name} to start"
  end

  def stop_bundle(bundle_symbolic_name)
    $bundle.bundle_context.bundles.each do |b|
      if b.symbolic_name == bundle_symbolic_name
        b.stop
        return "Stopped #{bundle_symbolic_name}"
      end
    end 
    return "Couldn't find bundle #{bundle_symbolic_name} to stop"
  end

  private
  def slurp(path)
    stream = $classloader.getResourceAsStream(path)
    Scanner.new(stream).useDelimiter("\\A").next
  end
end

