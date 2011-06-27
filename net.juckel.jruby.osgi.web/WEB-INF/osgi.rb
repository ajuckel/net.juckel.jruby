require 'java'
import 'org.osgi.util.tracker.ServiceTracker'

class ServiceWrapper
  def initialize(tracker)
    @tracker = tracker
    tracker.open
  end

  def method_missing(sym, *args, &block)
    s = @tracker.service
    return nil if s.nil?
    
    if s.respond_to?(sym)
      s.send(sym, *args)
    else 
      super
    end
  end
  
  def respond_to?()
    s = @tracker.service
    return nil if s.nil?
    s.respond_to?(sym)
  end
end

class Module
  def osgi_service(name, bundle_context, interface)
    wrapper = ServiceWrapper.new(ServiceTracker.new(bundle_context, interface, nil))
    define_method(name) do
      wrapper
    end
  end
end