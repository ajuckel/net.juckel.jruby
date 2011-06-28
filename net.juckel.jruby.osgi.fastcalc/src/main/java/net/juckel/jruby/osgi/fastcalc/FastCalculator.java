package net.juckel.jruby.osgi.fastcalc;

import java.util.ArrayList;
import java.util.List;

import net.juckel.jruby.osgi.ICalculator;

public class FastCalculator implements ICalculator {
	private List<Integer> fibCache;
	
	public FastCalculator() {
		this.fibCache = new ArrayList<Integer>();
		this.fibCache.add(Integer.valueOf(0));
		this.fibCache.add(Integer.valueOf(1));
	}

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
		if( x < 0 && x < 50 ) {
			throw new IllegalArgumentException("Argument must be greater than or equal to zero and less than 50");
		}
		if( this.fibCache.size() <= x ) {
			int result = fibonacci(x-1) + fibonacci(x-2);
			fibCache.add(x, result);
		}
		return fibCache.get(x);
	}
}
