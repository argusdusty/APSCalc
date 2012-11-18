package com.argusdusty.apscalc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;

public class FastMath // TODO: Insert Settings.inc_prcec and Settings.dec_prec
{
	private static final BigDecimal Two = BigDecimal.ONE.add(BigDecimal.ONE);
	private static final BigDecimal Three = Two.add(BigDecimal.ONE);
	private static final BigDecimal Five = Two.add(Three);
	private static final BigDecimal Half = BigDecimal.ONE.divide(Two);
	private static final int precinc = Settings.precinc;
	
	public static BigDecimal PHI() {return BigDecimal.ONE.add(sqrt(Five)).divide(Two);}
	public static BigDecimal E()
	{
		if (Settings.MC.getPrecision() < 50)
		{return new BigDecimal("2.718281828459045235360287471352662497757247093699959575").round(Settings.MC);}
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal result = Two, prevr = BigDecimal.ONE, term = prevr.divide(Two), temp = Two;
		while (prevr.compareTo(result) != 0)
		{
			prevr = result; result = result.add(term, Settings.MC); 
			temp = temp.add(BigDecimal.ONE); term = term.divide(temp, Settings.MC);
		}
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return result.round(Settings.MC);
	}
	public static BigDecimal PI()
	{
		if (Settings.MC.getPrecision() < 50)
		{return new BigDecimal("3.141592653589793238462643383279502884197169399375105821").round(Settings.MC);}
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal a = BigDecimal.ONE, b = a.divide(SQRT2(), Settings.MC), t = a.divide(Two.add(Two)), p = a, temp, temp2;
		while (a.compareTo(b) != 0)
		{
			temp = a;
			a = a.add(b).divide(Two, Settings.MC);
			b = sqrt(temp.multiply(b));
			temp2 = temp.subtract(a, Settings.MC);
			t = t.add(p.multiply(temp2.multiply(temp2, Settings.MC), Settings.MC).negate(), Settings.MC);
			p = p.multiply(Two);
		}
		a = a.multiply(a).divide(t, Settings.MC);
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return a.round(Settings.MC);
	}
	public static BigDecimal SQRT2()
	{
		BigDecimal result = new BigDecimal("1.4142135623730950488016887242096980785696718753769480732"), prevr = result;
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		result = result.divide(Two).add(BigDecimal.ONE.divide(result, Settings.MC));
		while (result.compareTo(prevr) != 0)
		{
			prevr = result; result = result.divide(Two).add(BigDecimal.ONE.divide(result, Settings.MC), Settings.MC);
		}
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return result.round(Settings.MC);
	}
	public static BigDecimal LN2()
	{
		if (Settings.MC.getPrecision() < 50)
		{return new BigDecimal("0.69314718055994530941723212145817656807550013436026").round(Settings.MC);}
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal result = BigDecimal.ZERO, prevr = BigDecimal.ONE, temp = Two, i = BigDecimal.ONE;
		while (prevr.compareTo(result) != 0)
		{
			prevr = result; result = result.add(BigDecimal.ONE.divide(temp.multiply(i), Settings.MC), Settings.MC);
			temp = temp.multiply(Two); i = i.add(BigDecimal.ONE);
		}
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return result.round(Settings.MC);
	}
	
	public static BigDecimal sqrt(BigDecimal d)
	{
		int sign = d.signum(); BigDecimal Two = BigDecimal.ONE.add(BigDecimal.ONE);
		if (sign == -1) throw new Error("Cannot take the sqrt of negative number");
		if (sign == 0) return BigDecimal.ZERO;
		if (d.compareTo(BigDecimal.ONE) == 0) return BigDecimal.ONE;
		BigDecimal result = d.divide(Two), prevr = d;
		while (prevr.compareTo(result) != 0)
		{
			prevr = result; result = result.add(d.divide(result, Settings.MC)).divide(Two, Settings.MC);
		}
		return result;
	}
	
	public static BigDecimal AGM(BigDecimal a, BigDecimal b)
	{
		if (a.compareTo(BigDecimal.ZERO) == 0 || b.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
		BigDecimal temp; BigDecimal Two = BigDecimal.ONE.add(BigDecimal.ONE);
		while (a.compareTo(b) != 0)
		{
			temp = a;
			a = a.add(b).divide(Two, Settings.MC);
			b = sqrt(temp.multiply(b));
		}
		return a;
	}
	
	public static BigDecimal ln(BigDecimal x)
	{
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		if (x.compareTo(BigDecimal.ZERO) != 1) throw new Error("Log input value must be > 0");
		if (x.compareTo(BigDecimal.ONE) == 0) return BigDecimal.ZERO;
		BigDecimal TWO = BigDecimal.ONE.add(BigDecimal.ONE);
		int count = 0;
		while (x.compareTo(BigDecimal.ONE) == 1) {x = x.divide(TWO); count++;}
		while (x.compareTo(Half) == -1) {x = x.multiply(TWO); count--;}
		BigDecimal r = PI().divide(Two);
		int m = (Settings.MC.getPrecision() << 2) + 3;
		r = r.divide(AGM(BigDecimal.ONE, BigDecimal.ONE.divide(x.multiply(Two.pow(m-2)), Settings.MC)), Settings.MC);
		r = r.add(LN2().multiply(new BigDecimal(count-m)));
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return r.round(Settings.MC);
	}
	
	public static BigDecimal log(BigDecimal x, BigInteger b)
	{
		if (b.compareTo(BigInteger.ONE) != 1) throw new Error("Log base must be > 1");
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal lnx = ln(x), lnb = ln(new BigDecimal(b));
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return lnx.divide(lnb, Settings.MC);
	}
	
	public static BigDecimal log(BigDecimal x, BigDecimal b)
	{
		if (b.compareTo(BigDecimal.ONE) != 1) throw new Error("Log base must be > 1");
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal lnx = ln(x), lnb = ln(b);
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return lnx.divide(lnb, Settings.MC);
	}
	
	public static BigDecimal exp(BigDecimal x)
	{
		if (x.signum() == 0) return BigDecimal.ONE;
		if (x.signum() == -1) return BigDecimal.ONE.divide(exp(x.negate()), Settings.MC);
		if (x.compareTo(BigDecimal.ONE) == 0) return E().round(Settings.MC);
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal prevr = BigDecimal.ZERO; BigDecimal result = BigDecimal.ONE; BigDecimal term = BigDecimal.ONE;
		int n = 0;
		while (prevr.compareTo(result) != 0)
		{
			n++; term = term.multiply(x).divide(new BigDecimal(n), Settings.MC);
			prevr = result; result = result.add(term, Settings.MC);
		}
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return result.round(Settings.MC);
	}
	
	public static BigDecimal exp(BigInteger x) {return exp(new BigDecimal(x));}
	
	public static BigDecimal sin(BigDecimal x)
	{
		BigDecimal PI = PI();
		x = x.remainder(Two.multiply(PI));
		if (x.compareTo(PI.divide(Two)) == 1) return cos(x.subtract(PI.divide(Two)));
		if (x.compareTo(PI.divide(Two.add(Two))) == 1) return cos(PI.divide(Two).subtract(x));
		int sign = x.signum();
		if (sign == 0) return BigDecimal.ZERO;
		if (sign == -1) x = x.negate();
		BigDecimal Eight = Three.add(Three).add(Two);
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal x2 = x.multiply(x); BigDecimal i = Three.add(Three); BigDecimal div = i;
		BigDecimal result = x; BigDecimal prevr = result;
		BigDecimal term = x.multiply(x2).divide(div, Settings.MC).negate();
		result = result.add(term);
		while (prevr.compareTo(result) != 0)
		{
			prevr = result;
			i = i.add(Eight); div = div.add(i); term = term.multiply(x2).divide(div, Settings.MC).negate();
			result = result.add(term, Settings.MC);
		}
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		result = result.round(Settings.MC);
		if (sign == -1) return result.negate();
		return result;
	}
	
	public static BigDecimal cos(BigDecimal x)
	{
		BigDecimal PI = PI();
		x = x.remainder(Two.multiply(PI));
		if (x.compareTo(PI.divide(Two)) == 1) return sin(x.subtract(PI.divide(Two))).negate();
		if (x.compareTo(PI.divide(Two.add(Two))) == 1) return sin(PI.divide(Two).subtract(x));
		int sign = x.signum();
		if (sign == 0) return BigDecimal.ONE;
		if (sign == -1) x = x.negate();
		BigDecimal Eight = Three.add(Three).add(Two);
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal x2 = x.multiply(x); BigDecimal i = Two; BigDecimal div = i;
		BigDecimal result = BigDecimal.ONE; BigDecimal prevr = result;
		BigDecimal term = x2.divide(div, Settings.MC).negate();
		result = result.add(term);
		while (prevr.compareTo(result) != 0)
		{
			prevr = result;
			i = i.add(Eight); div = div.add(i); term = term.multiply(x2).divide(div, Settings.MC).negate();
			result = result.add(term, Settings.MC);
		}
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		result = result.round(Settings.MC);
		if (sign == -1) return result.negate();
		return result;
	}
	
	public static BigDecimal tan(BigDecimal x)
	{
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal c = cos(x); BigDecimal s = sin(x);
		BigDecimal result = s.divide(c, Settings.MC);
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return result.round(Settings.MC);
	}
	
	public static BigDecimal sec(BigDecimal x)
	{
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal c = cos(x); BigDecimal result = BigDecimal.ONE.divide(c, Settings.MC);
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return result.round(Settings.MC);
	}
	
	public static BigDecimal csc(BigDecimal x)
	{
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal s = sin(x); BigDecimal result = BigDecimal.ONE.divide(s, Settings.MC);
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return result.round(Settings.MC);
	}
	
	public static BigDecimal cot(BigDecimal x)
	{
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal c = cos(x); BigDecimal s = sin(x);
		BigDecimal result = c.divide(s, Settings.MC);
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return result.round(Settings.MC);
	}
	
	public static BigDecimal asin(BigDecimal x)
	{
		if (x.compareTo(BigDecimal.ONE) == 1 || x.compareTo(BigDecimal.ONE.negate()) == -1)
		{
			throw new Error("Out of range for asin");
		}
		int sign = x.signum();
		if (sign == 0) return BigDecimal.ZERO;
		if (sign == -1) x = x.negate();
		if (x.compareTo(BigDecimal.ONE) == 1)
		{
			if (sign == -1) return PI().divide(Two).negate();
			return PI().divide(Two);
		}
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		boolean b = x.compareTo(new BigDecimal("0.5")) == 1;
		BigDecimal term, result;
		if (b)
		{
			x = BigDecimal.ONE.subtract(x);
			term = sqrt(x).multiply(SQRT2(), Settings.MC).negate();
			result = PI().divide(Two).add(term, Settings.MC);
		}
		else
		{
			term = x; result = x;
		}
		BigDecimal x2 = x.multiply(x), prevr = BigDecimal.ZERO, n = BigDecimal.ONE, n2 = n.add(n);
		while (prevr.compareTo(result) != 0)
		{
			term = term.multiply(n2.subtract(BigDecimal.ONE).pow(2).divide(n2.multiply(n2).add(n2), Settings.MC));
			if (b) term = term.multiply(x).divide(Two);
			else term = term.multiply(x2, Settings.MC);
			prevr = result; result = result.add(term, Settings.MC); n = n.add(BigDecimal.ONE); n2 = n.add(n);
		}
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		result = result.round(Settings.MC);
		if (sign == -1) return result.negate();
		return result;
	}
	
	public static BigDecimal acos(BigDecimal x)
	{
		if (x.compareTo(BigDecimal.ONE) == 1 || x.compareTo(BigDecimal.ONE.negate()) == -1)
		{
			throw new Error("Out of range for acos");
		}
		return PI().divide(Two).subtract(asin(x));
	}
	
	public static BigDecimal atan(BigDecimal x)
	{
		int sign = x.signum();
		if (sign == 0) return BigDecimal.ZERO;
		if (sign == -1) x = x.negate();
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal x2 = x.multiply(x), result = BigDecimal.ZERO, n = BigDecimal.ZERO;
		if (x.compareTo(new BigDecimal("1.2")) == 1) // 1.2 optimized for fewest number of iterations ~100
		{
			BigDecimal term = BigDecimal.ONE.divide(x, Settings.MC).negate(), prevr = BigDecimal.ZERO;
			n = BigDecimal.ONE; result = PI().divide(Two);
			while (prevr.compareTo(result) != 0)
			{
				prevr = result; result = result.add(term.divide(n, Settings.MC), Settings.MC); n = n.add(Two);
				term = term.divide(x2, Settings.MC).negate();
			}
			n = n.add(BigDecimal.ONE).divide(Two);
		}
		else
		{
			BigDecimal d = x2.divide(x2.add(BigDecimal.ONE), Settings.MC), d2 = Two.multiply(d);
			BigDecimal term = d.divide(x, Settings.MC), prevr = BigDecimal.ONE;
			while (prevr.compareTo(result) != 0)
			{
				prevr = result; result = result.add(term, Settings.MC); n = n.add(BigDecimal.ONE);
				term = term.multiply(d2.multiply(n.divide(n.multiply(Two).add(BigDecimal.ONE), Settings.MC)), Settings.MC);
			}
		}
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		result = result.round(Settings.MC);
		if (sign == -1) return result.negate();
		return result;
	}
	
	@SuppressWarnings("unused")
	private static BigInteger intsqrt(BigInteger x)
	{
		BigInteger up = x, down = BigInteger.ZERO, two = BigInteger.ONE.add(BigInteger.ONE), test = x.divide(two);
		int m = 0;
		while (up.compareTo(down) != 0)
		{
			m = test.multiply(test).compareTo(x);
			if (m == 0) return test;
			else if (m == -1)
			{
				if (down.compareTo(test) == 0) return test;
				down = test;
			}
			else up = test;
			test = down.add(up).divide(two);
		}
		return test;
	}
	
	@SuppressWarnings("unused")
	private static int intsqrt(int x)
	{
		int up = x, down = 0, test = x / 2;
		int m = 0;
		while (up > down)
		{
			m = test*test;
			if (m == x) return test;
			else if (m < x)
			{
				if (down == test) return test;
				down = test;
			}
			else up = test;
			test = (up + down) / 2;
		}
		return test;
	}
	
	public static ArrayList<Integer> primes(int x)
	{
		ArrayList<Integer> primes = new ArrayList<Integer>();
		primes.add(2); primes.add(3); primes.add(5); primes.add(7); primes.add(11);
		ArrayList<Integer> temp;
		int n = 13; int m = 121; boolean p;
		while (true)
		{
			temp = new ArrayList<Integer>(primes);
			if (m > x) m = x;
			while (n <= m)
			{
				p = true;
				for (int i = 0; i < temp.size(); i++) {if ((n % temp.get(i)) == 0) {p = false; break;}}
				if (p) primes.add(n);
				n += 2;
			}
			if (m >= x) return primes;
			m = m * m;
		}
	}
	
	public static BigInteger fact(BigInteger x)
	{
		if (x.signum() == 0) return BigInteger.ONE;
		if (x.signum() == -1) throw new Error("Factorial is not available for negative numbers");
		if (x.compareTo(BigInteger.ONE.add(BigInteger.ONE).pow(20)) == 1)
			throw new Error("Max input for factorial is 1048576");
		int v = x.intValue();
		if (v > 100)
		{
			BigInteger r = BigInteger.ONE;
			ArrayList<Integer> primelist = primes(v); int j, m, p;
			for (int i = 0; i < primelist.size(); i++)
			{
				j = primelist.get(i); m = v; p = 0;
				while (m > 0) {m /= j; p += m;}
				r = r.multiply(BigInteger.valueOf(j).pow(p));
			}
			return r;
		}
		BigInteger r = BigInteger.ONE;
		BigInteger n = BigInteger.ONE.add(BigInteger.ONE);
		for (int i = 2; i <= v; i++) {r = r.multiply(n); n = n.add(BigInteger.ONE);}
		return r;
	}
	
	public static BigInteger gcd(BigInteger x, BigInteger y) {return x.gcd(y);}
	
	public static BigInteger mod(BigInteger x, BigInteger y)
	{
		if (y.signum() == -1) return mod(x, y.negate()).negate();
		if (y.signum() == 0) throw new Error("Error: division by 0");
		return x.mod(y);
	}
	
	public static BigDecimal mod(BigDecimal x, BigDecimal y)
	{
		if (y.signum() == -1) return mod(x, y.negate()).negate();
		if (y.signum() == 0) throw new Error("Error: division by 0");
		return x.remainder(y);
	}
	
	public static boolean isPrime(BigInteger x)
	{
		if (x.compareTo(BigInteger.ONE) == -1) return false;
		if (x.and(BigInteger.ONE).compareTo(BigInteger.ZERO) == 0) return false;
		BigInteger nx = x.subtract(BigInteger.ONE), d = nx, n;
		int s = 0;
		while (d.and(BigInteger.ONE).compareTo(BigInteger.ZERO) == 0) {s++; d = d.shiftRight(1);}
		n = pow_mod(BigInteger.ONE.add(BigInteger.ONE), d, x);
		if (n.compareTo(BigInteger.ONE) != 0 && n.compareTo(x.subtract(BigInteger.ONE)) != 0)
		{
			for (int r = 1; r < s; r++)
			{
				n = n.multiply(n).mod(x);
				if (n.compareTo(BigInteger.ONE) == 0) return false;
				if (n.compareTo(nx) == 0) break;
			}
			if (n.compareTo(nx) != 0) return false;
		}
		int maxi = 3;
		if (x.compareTo(new BigInteger("1373653")) >= 0) maxi = 13;
		if (x.compareTo(new BigInteger("341550071728321")) >= 0) maxi = 19;
		if (x.compareTo(new BigInteger("3825123056546413051")) >= 0)
		{
			BigInteger THREE = BigInteger.ONE.add(BigInteger.ONE).add(BigInteger.ONE);
			int r = 0; while (x.compareTo(THREE) == 1) {x = x.divide(THREE); r++;}
			maxi = r*r*2;
		}
		for (int i = 2; i <= maxi; i++)
		{
			if (x.mod(BigInteger.valueOf(i)).compareTo(BigInteger.ZERO) == 0) return false;
			n = pow_mod(BigInteger.valueOf(i), d, x);
			if (n.compareTo(BigInteger.ONE) == 0 || n.compareTo(x.subtract(BigInteger.ONE)) == 0) continue;
			for (int r = 1; r < s; r++)
			{
				n = n.multiply(n).mod(x);
				if (n.compareTo(BigInteger.ONE) == 0) return false;
				if (n.compareTo(nx) == 0) break;
			}
			if (n.compareTo(nx) != 0) return false;
		}
		return true;
	}
	
	public static BigInteger pow_mod(BigInteger base, BigInteger exp, BigInteger mod)
	{
		BigInteger b = base, e = exp, r = b;
		e = e.subtract(BigInteger.ONE);
		while (e.compareTo(BigInteger.ZERO) != 0)
		{
			if (e.and(BigInteger.ONE).compareTo(BigInteger.ONE) == 0) r = r.multiply(b).mod(mod);
			e = e.shiftRight(1);
			b = b.multiply(b).mod(mod);
		}
		return r;
	}
	
	public static BigDecimal gamma(BigDecimal x)
	{
		try
		{
			BigInteger m = x.toBigIntegerExact();
			return new BigDecimal(fact(m.add(BigInteger.ONE)));
		}
		catch (ArithmeticException e) {;}
		x = x.subtract(BigDecimal.ONE);
		Settings.MC = new MathContext(Settings.MC.getPrecision() + precinc, Settings.MC.getRoundingMode());
		BigDecimal a = new BigDecimal(1.253*Settings.MC.getPrecision(), Settings.MC); // ln(10)/ln(2*Pi) ~= 1.25285
		BigDecimal m = exp(ln(x.add(a)).multiply(x.add(Half)).add(x.add(a).negate()));
		BigDecimal r = m.multiply(sqrt(PI().multiply(Two)), Settings.MC);
		BigDecimal c = exp(a.subtract(BigDecimal.ONE));
		c = c.multiply(exp(ln(a.subtract(BigDecimal.ONE)).multiply(Half)));
		BigDecimal k = BigDecimal.ONE;
		while (k.compareTo(a.subtract(BigDecimal.ONE)) < 0)
		{
			r = r.add(c.divide(x.add(k), Settings.MC).multiply(m, Settings.MC));
			k = k.add(BigDecimal.ONE);
			c = c.multiply(exp(ln(a.subtract(k)).multiply(k.subtract(Half))), Settings.MC).negate();
			c = c.divide(k.subtract(BigDecimal.ONE).multiply(E(), Settings.MC), Settings.MC);
			c = c.divide(exp(ln(a.add(BigDecimal.ONE).subtract(k)).multiply(k.subtract(Three.multiply(Half)))), Settings.MC);
		}
		Settings.MC = new MathContext(Settings.MC.getPrecision() - precinc, Settings.MC.getRoundingMode());
		return r;
	}
}