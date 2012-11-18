package com.argusdusty.apscalc.types;

import java.math.BigDecimal;
import java.math.BigInteger;


public class Rational extends Num
{
	public static Rational HALF = new Rational(Int.ONE, Int.TWO);
	public static Rational THIRD = new Rational(Int.ONE, Int.TWO.add(Int.ONE));
	public BigInteger num;
	public BigInteger denom;
	
	public Rational(Int num, Int denom)
	{
		if (denom.equals(Int.ZERO)) throw new Error("Error: Division by zero");
		this.num = num.value;
		this.denom = denom.value;
	}
	
	public Rational(BigInteger num, BigInteger denom)
	{
		if (denom.equals(BigInteger.ZERO)) throw new Error("Error: Division by zero");
		this.num = num;
		this.denom = denom;
	}

	public String toString() {return num.toString() + "/" + denom.toString();}
	
	public Num simplify()
	{
		BigInteger n = num; BigInteger d = denom; BigInteger gcd = n.gcd(d);
		n = n.divide(gcd); d = d.divide(gcd);
		if (n.signum() == 0) return Int.ZERO;
		if (d.signum() == -1) {n = n.negate(); d = d.negate();}
		if (d.compareTo(BigInteger.ONE) == 0) return new Int(n);
		return new Rational(n, d);
	}
	
	public Num add(Int i) {return i.add(this);}
	public Float add(Float f) {return new Float(f.value.add(new BigDecimal(num).divide(new BigDecimal(denom))));}
	
	public Num add(Rational r)
	{
		return new Rational(r.num.multiply(denom).add(r.denom.multiply(num)), r.denom.multiply(denom)).simplify();
	}
	
	public Num mul(Int i) {return i.mul(this);}
	public Float mul(Float f) {return new Float(f.mul(new Float(this)));}
	public Num mul(Rational r) {return new Rational(num.multiply(r.num), denom.multiply(r.denom)).simplify();}
	
	public Rational pow(Int i)
	{
		if (i.value.signum() == -1)
		{
			i = new Int(i.value.negate());
			return new Rational((Int) new Int(denom).pow(i), (Int) new Int(num).pow(i));
		}
		return new Rational((Int) new Int(num).pow(i), (Int) new Int(denom).pow(i));
	}
	
	public Expression pow(Float f) {return new Float(this).pow(f);}
	public Num negate() {return new Rational(num.negate(), denom);}
	public Num inverse() {return new Rational(denom, num).simplify();}
	
	public boolean equals(Expression e)
	{
		if (e instanceof Rational && ((Rational) e).num.equals(num) && ((Rational) e).denom.equals(denom)) return true;
		return false;
	}
	
	public Expression pow(Rational r) {return new Int(num).pow(r).mul(new Int(denom).pow(r).inverse());}
	public int signum() {return (num.signum() * denom.signum());}
	public Rational copy() {return new Rational(num, denom);}
}