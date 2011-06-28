Embedding JRuby in OSGi Sample Project
======================================

This is a relatively simple project just meant as a technology demonstration
of a JRuby Rack app published to an HttpService running within an OSGi framework
where the Rack handler then uses the OSGi service registry to obtain an
ICalculator service to use for handling certain requests.

Currently requires org.jruby.rack.OSGiRackApplicationFactory available from:
https://github.com/ajuckel/jruby-rack/tree/osgi.

The ICalculator interface is provided by the net.juckel.jruby.osgi.web bundle,
and an implementation is available both via the net.juckel.jruby.osgi.slowcalc
and net.juckel.jruby.osgi.fastcalc bundles.  These implementations are
published via OSGi's Declarative Services mechanism, so simply starting the
bundles in a runtime with Declarative Services running should publish the
service implementations.

When running the app