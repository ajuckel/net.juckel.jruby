package net.juckel.jruby.osgi.slowcalc;

import java.util.concurrent.atomic.AtomicInteger;

import net.juckel.jruby.osgi.ICalculator;

public class SlowCalculator implements ICalculator {
	private static AtomicInteger requests = new AtomicInteger(0);
	@Override
	public int add(int x, int y) {
		requests.incrementAndGet();
		return x + y;
	}

	@Override
	public int factorial(int x) {
		if( x == 0 || x == 1 ) {
			return 1;
		} else {
			return x * factorial(x - 1);
		}
	}
	
	public int fibonacci(int x) {
		requests.incrementAndGet();
		return doFib(x);
	}
	
	public int doFib(int x) {
		if( x < 0 ) {
			throw new IllegalArgumentException("Argument must be greater than or equal to zero.");
		}
		if( x == 0 ) {
			return 0;
		} else if( x == 1 ) {
			return 1;
		} else {
			return doFib(x - 1) + doFib(x - 2);
		}
	}

	public int requests() {
		return requests.get();
	}
}
