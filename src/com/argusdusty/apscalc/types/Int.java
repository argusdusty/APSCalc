package com.argusdusty.apscalc.types;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.Settings;
import com.argusdusty.apscalc.functions.trig.Cos;
import com.argusdusty.apscalc.functions.trig.Sin;
import com.argusdusty.apscalc.types.mathconsts.Indeterminate;
import com.argusdusty.apscalc.types.mathconsts.Inf;
import com.argusdusty.apscalc.types.mathconsts.Pi;
import com.argusdusty.apscalc.utils.OrderedMap;

public class Int extends Num
{
	public static Int ZERO = new Int(BigInteger.ZERO);
	public static Int ONE = new Int(BigInteger.ONE);
	public static Int TWO = new Int(BigInteger.ONE.add(BigInteger.ONE));
	public static Int NEGONE = new Int(BigInteger.ONE.negate());
	public BigInteger value;
	
	public Int(String s) {this.value = new BigInteger(s);}
	public Int(String s, int b) {this.value = new BigInteger(s, b);}
	public Int(BigInteger value) {this.value = value;}
	public Int(int i) {this.value = BigInteger.valueOf(i);}
	public String toString() {return value.toString();}
	public Int simplify() {return new Int(value);}
	
	public Int add(Int i) {return new Int(value.add(i.value));}
	public Float add(Float f) {return new Float(f.value.add(new BigDecimal(value)));}
	public Num add(Rational r) {return new Rational(r.num.add(value.multiply(r.denom)), r.denom).simplify();}
	
	public Int mul(Int i) {return new Int(value.multiply(i.value));}
	public Num mul(Float f) {return new Float(f.value.multiply(new BigDecimal(value), Settings.MC));}
	public Num mul(Rational r) {return new Rational(r.num.multiply(value), r.denom).simplify();}
	
	public Expression pow(Int i)
	{
		BigInteger i1 = value, i2 = i.value;
		int sign1 = i1.signum(); int sign2 = i2.signum();
		if (sign1 == 0 && sign2 == 0) return new Indeterminate();
		if (sign1 == 0 && sign2 == 1) return Int.ZERO;
		if (sign1 == 0) return new Inf();
		if (sign2 == 0) return Int.ONE;
		if (sign2 == -1) i2 = i2.negate();
		if (sign1 == -1) i1 = i1.negate();
		BigInteger b, e, result;
		b = i1; e = i2.subtract(BigInteger.ONE); result = b;
		while (e.compareTo(BigInteger.ZERO) != 0)
		{
			if (e.and(BigInteger.ONE).compareTo(BigInteger.ONE) == 0) result = result.multiply(b);
			e = e.shiftRight(1);
			b = b.multiply(b);
		}
		if (sign1 == -1)
		{
			if (sign2 == -1)
			{
				if (i2.getLowestSetBit() == 0) return new Rational(BigInteger.ONE, result.negate());
				return new Rational(BigInteger.ONE, result);
			}
			if (i2.getLowestSetBit() == 0) return new Int(result.negate());
			return new Int(result);
		}
		if (sign2 == -1) return new Rational(BigInteger.ONE, result);
		return new Int(result);
	}
	
	public Expression pow(Float f)
	{
		if (equals(NEGONE))
		{
			Expression m = new Pi().mul(f);
			return new Complex(new Cos(m).simplify(), new Sin(m).simplify());
		}
		BigInteger i = value; BigDecimal d = f.value;
		if (new BigDecimal(d.toBigInteger()).compareTo(d) == 0)
		{
			Expression c = this.pow(new Int(d.toBigInteger()));
			if (c instanceof Num) return new Float((Num) c).round(Settings.MC);
			return c;
		}
		int sign1 = i.signum(); int sign2 = d.signum();
		if (sign1 == 0 && sign2 == 0) throw new Error();
		if (sign1 == 0) return new Float(BigDecimal.ZERO);
		if (sign2 == 0) return new Float(BigDecimal.ONE);
		if (sign2 == -1) d = d.negate();
		if (sign1 == -1) return negate().pow(f).mul(Int.NEGONE.pow(f));
		BigDecimal result = FastMath.exp(FastMath.ln(new BigDecimal(this.value)).multiply(d));
		if (sign2 == -1) return new Float(BigDecimal.ONE.divide(result, Settings.MC));
		return new Float(result).round(Settings.MC);
	}
	
	public Expression pow(Rational r)
	{
		if (equals(NEGONE))
		{
			Expression m = new Pi().mul(r);
			return new Complex(new Cos(m).simplify(), new Sin(m).simplify());
		}
		if (equals(ZERO) || equals(ONE)) return this;
		if (r.signum() == -1) return pow(r.negate()).inverse();
		OrderedMap<Num, Num> a = new OrderedMap<Num, Num>(factor(new BigInteger("100000")));
		Int n = ONE; Rational gcd = r;
		for (int i = 0; i < a.size(); i++)
		{
			Num t = r.mul(a.getVal(i));
			if (t instanceof Int)
			{
				n = n.mul((Int) a.getKey(i).pow(t));
				a.remove(i); i--;
			}
			else
			{
				a.setVal(i, t);
				Rational rc = (Rational) t;
				gcd = new Rational(FastMath.gcd(gcd.num.multiply(rc.denom), gcd.denom.multiply(rc.num)),
						gcd.denom.multiply(rc.denom));
			}
		}
		Int m = ONE;
		for (int i = 0; i < a.size(); i++)
		{
			Rational rc = (Rational) a.getVal(i);
			Int p = new Int(rc.num.multiply(gcd.denom).divide(rc.denom.multiply(gcd.num)));
			m = m.mul((Int) a.getKey(i).pow(p));
		}
		if (gcd.num.equals(gcd.denom)) return n.mul(m);
		if (m.equals(Int.ONE)) return n;
		return n.mul(new Prod(m, gcd.simplify()));
	}
	
	public OrderedMap<Int, Int> factor(BigInteger max)
	{
		OrderedMap<Int, Int> a = new OrderedMap<Int, Int>();
		if (value.signum() == 0) {a.add(ZERO, ONE); return a;}
		BigInteger x = value; int pow = 0;
		if (x.signum() == -1) {a.add(ONE.negate(), ONE); x = x.negate();}
		pow = x.getLowestSetBit();
		if (pow != 0)
		{
			x = x.shiftRight(pow);
			a.add(TWO, new Int(pow));
		}
		BigInteger Two = TWO.value;
		BigInteger n = Two.add(BigInteger.ONE);
		if (max.signum() == 0) max = x;
		while (n.multiply(n).compareTo(max) != 1)
		{
			pow = 0;
			while (x.mod(n).compareTo(BigInteger.ZERO) == 0)
			{
				x = x.divide(n);
				pow++;
			}
			if (pow != 0) a.add(new Int(n), new Int(pow));
			n = n.add(Two);
		}
		if (x.equals(BigInteger.ONE) && a.size() > 0) return a;
		a.add(new Int(x), ONE); return a;
	}
	
	public boolean isPrime()
	{
		if (signum() != 1) return false;
		return FastMath.isPrime(value);
	}
	
	public int signum() {return value.signum();}
	public Int negate() {return new Int(value.negate());}
	public Constant inverse() {if (equals(ZERO)) return new Inf(); return new Rational(ONE, new Int(value)).simplify();}
	public boolean equals(Expression e) {return e instanceof Int && ((Int) e).value.equals(value);}
	public Int copy() {return new Int(value);}
}