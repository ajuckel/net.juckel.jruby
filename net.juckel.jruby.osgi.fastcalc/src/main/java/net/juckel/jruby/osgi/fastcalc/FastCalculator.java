package net.juckel.jruby.osgi.fastcalc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.juckel.jruby.osgi.ICalculator;

public class FastCalculator implements ICalculator {
	private static AtomicInteger requests = new AtomicInteger(0);
	private List<Integer> fibCache;
	
	public FastCalculator() {
		this.fibCache = new ArrayList<Integer>();
		this.fibCache.add(Integer.valueOf(0));
		this.fibCache.add(Integer.valueOf(1));
	}

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
		if( this.fibCache.size() <= x ) {
			int result = doFib(x-1) + doFib(x-2);
			fibCache.add(x, result);
		}
		return fibCache.get(x);
	}

	public int requests() {
		return requests.get();
	}
}
