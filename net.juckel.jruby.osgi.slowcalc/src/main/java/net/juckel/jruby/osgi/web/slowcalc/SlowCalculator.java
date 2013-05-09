package net.juckel.jruby.osgi.web.slowcalc;

import net.juckel.jruby.osgi.web.ICalculator;

public class SlowCalculator implements ICalculator {
	@Override
	public int add(int x, int y) {
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
		if( x < 0 && x < 50) {
			throw new IllegalArgumentException("Argument must be greater than or equal to zero and less than 50");
		}
		if( x == 0 ) {
			return 0;
		} else if( x == 1 ) {
			return 1;
		} else {
			return fibonacci(x - 1) + fibonacci(x - 2);
		}
	}
}
