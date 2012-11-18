package com.argusdusty.apscalc.types;


public abstract class Num extends Constant
{
	public abstract Num add(Int i);
	public abstract Num add(Float f);
	public abstract Num add(Rational r);
	public abstract Num mul(Int i);
	public abstract Num mul(Float f);
	public abstract Num mul(Rational r);
	public abstract Expression pow(Int i);
	public abstract Expression pow(Float f);
	public abstract Expression pow(Rational r);
	
	public Num add(Num c)
	{
		if (c instanceof Int) return this.add((Int) c);
		if (c instanceof Float) return this.add((Float) c);
		if (c instanceof Rational) return this.add((Rational) c);
		throw new Error("Unknown Exception");
	}
	
	public Num mul(Num c)
	{
		if (c instanceof Int) return this.mul((Int) c);
		if (c instanceof Float) return this.mul((Float) c);
		if (c instanceof Rational) return this.mul((Rational) c);
		throw new Error("Unknown Exception");
	}
	
	public Expression pow(Num e)
	{
		if (e instanceof Int) return this.pow((Int) e);
		else if (e instanceof Float) return this.pow((Float) e);
		return this.pow((Rational) e);
	}
	
	public abstract Num negate();
	public abstract Num simplify();
	
	public abstract Num copy();
}
