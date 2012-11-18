package com.argusdusty.apscalc.types;

public class Fraction extends Expression
{
	public Expression num;
	public Expression denom;
	
	public Fraction(Expression num, Expression denom)
	{
		this.num = num;
		this.denom = denom;
	}
	
	public Expression simplify()
	{
		return new Fraction(num.simplify(), denom.simplify());
	}
	
	public String toString()
	{
		return "(" + num.toString() + ")/(" + denom.toString() + ")";
	}
	
	public boolean equals(Expression e)
	{
		return (e instanceof Fraction) && ((Fraction) e).num.equals(num) && ((Fraction) e).denom.equals(denom);
	}
	
	public Expression substitute(Variable x, Expression e)
	{
		return new Fraction(num.substitute(x, e), denom.substitute(x, e));
	}
	
	public Expression copy()
	{
		return new Fraction(num.copy(), denom.copy());
	}

}
