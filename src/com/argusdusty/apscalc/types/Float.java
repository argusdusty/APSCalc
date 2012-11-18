package com.argusdusty.apscalc.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.Settings;
import com.argusdusty.apscalc.types.mathconsts.Inf;

public class Float extends Num
{
	public static Float ZERO = new Float(BigDecimal.ZERO);
	public static Float ONE = new Float(BigDecimal.ONE);
	public static Float TWO = new Float(BigDecimal.ONE.add(BigDecimal.ONE));
	public static Float HALF = new Float(Rational.HALF);
	public static Float TEN = new Float(BigDecimal.TEN);
	
	public BigDecimal value;
	
	public Float(String s) {this(new BigDecimal(s));}
	public Float(Int i) {this(new BigDecimal(i.value));}
	public Float(Rational r) {this(new BigDecimal(r.num).divide(new BigDecimal(r.denom), Settings.MC));}
	public Float(Num n)
	{
		if (n instanceof Int) this.value = new BigDecimal(((Int) n).value).stripTrailingZeros();
		else if (n instanceof Float) this.value = ((Float) n).value.stripTrailingZeros();
		else
		{
			Rational r = (Rational) n;
			this.value = new BigDecimal(r.num).divide(new BigDecimal(r.denom), Settings.MC).stripTrailingZeros();
		}
	}
	
	public Float(BigDecimal value) {this.value = value.stripTrailingZeros();}
	public String toString() {return value.round(Settings.print).toString();}
	
	public Float simplify()
	{
		if (equals(ZERO)) return ZERO;
		return new Float(value);
	}
	
	public Float add(Int i) {return new Float(value.add(new BigDecimal(i.value), Settings.MC));}
	public Float add(Float f) {return new Float(value.add(f.value, Settings.MC));}
	public Float add(Rational r)
	{
		return new Float(value.add(new BigDecimal(r.num).divide(new BigDecimal(r.denom), Settings.MC), Settings.MC));
	}
	
	public Float mul(Int i) {return new Float(value.multiply(new BigDecimal(i.value), Settings.MC));}
	public Float mul(Float f) {return new Float(value.multiply(f.value, Settings.MC));}
	public Float mul(Rational r)
	{
		return new Float(value.multiply(new BigDecimal(r.num).divide(new BigDecimal(r.denom), Settings.MC)));
	}
	
	public Num pow(Int i)
	{
		BigDecimal f = value; BigInteger i2 = i.value;
		int sign1 = f.signum(); int sign2 = i2.signum();
		if (sign1 == 0 && sign2 == 0) throw new Error();
		if (sign1 == 0) return Int.ZERO;
		if (sign2 == 0) return Int.ONE;
		if (sign2 == -1) i2 = i2.negate();
		if (sign1 == -1) f = f.negate();
		BigDecimal b, result; BigInteger e;
		b = f; e = i2.subtract(BigInteger.ONE); result = b;
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
				if (i2.getLowestSetBit() == 0) return new Float(BigDecimal.ONE.divide(result.negate(), Settings.MC));
				return new Float(BigDecimal.ONE.divide(result, Settings.MC));
			}
			if (i2.getLowestSetBit() == 0) return new Float(result.negate());
			return new Float(result);
		}
		if (sign2 == -1) return new Float(BigDecimal.ONE.divide(result, Settings.MC));
		return new Float(result.round(Settings.MC));
	}
	
	public Expression pow(Float f)
	{
		BigDecimal f1 = value; BigDecimal f2 = f.value;
		int sign1 = f1.signum(); int sign2 = f2.signum();
		if (sign1 == 0 && sign2 == 0) throw new Error(); //0^0
		if (sign1 == 0) return new Float(BigDecimal.ZERO); //TODO: 0^-1 = Inf
		if (sign2 == 0) return new Float(BigDecimal.ONE);
		if (sign2 == -1) f2 = f2.negate();
		if (sign1 == -1) return negate().pow(f).mul(Int.ONE.negate().pow(f));
		BigDecimal result = FastMath.exp(FastMath.ln(value).multiply(f2));
		if (sign2 == -1) return new Float(BigDecimal.ONE.divide(result, Settings.MC));
		return new Float(result).round(Settings.MC);
	}
	
	public Float negate() {return new Float(value.negate());}
	
	public Constant inverse()
	{
		if (equals(ZERO)) return new Inf();
		try {return new Float(BigDecimal.ONE.divide(value));}
		catch (ArithmeticException e) {return new Float(BigDecimal.ONE.divide(value, Settings.MC));}
	}
	
	public boolean equals(Expression e)
	{
		if (e instanceof Float)
		{
			if (value.unscaledValue().compareTo(BigInteger.ZERO) == 0 
					&& ((Float) e).value.unscaledValue().compareTo(BigInteger.ZERO) == 0) return true;
			return ((Float) e).value.equals(value);
		}
		return false;
	}
	
	public Expression pow(Rational r) {return this.pow(new Float(r));}
	public int signum() {return value.signum();}
	
	public Num exactVal()
	{
		if (value.scale() == 0) return new Int(value.unscaledValue());
		if (value.scale() > 0) return new Rational(value.unscaledValue(), BigInteger.TEN.pow(value.scale()));
		return new Int(value.unscaledValue().multiply(BigInteger.TEN.pow(-value.scale())));
	}
	
	public Num reduce()
	{
		if (value.scale() == 0) return new Int(value.unscaledValue());
		else if (value.scale() < 0) return new Int(value.unscaledValue().multiply(BigInteger.TEN.pow(-value.scale())));
		return new Float(value);
	}
	
	public Float round(MathContext MC) {return new Float(value.round(MC));}
	public Float copy() {return new Float(value);}
}