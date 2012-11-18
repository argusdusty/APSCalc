package com.argusdusty.apscalc.types;

import com.argusdusty.apscalc.functions.AbstractFunction;
import com.argusdusty.apscalc.types.mathconsts.Unique;

public class Power extends Expression
{
	public Expression base;
	public Expression exp;
	
	public Power(Expression base, Expression exp)
	{
		this.base = base;
		this.exp = exp;
	}
	
	public String toString()
	{
		String result;
		boolean paraa = !(base instanceof Variable || base instanceof AbstractFunction || base instanceof Constant);
		if (base instanceof Rational || base.toString().startsWith("-")) paraa = true;
		boolean parab = !(exp instanceof Variable || exp instanceof AbstractFunction || exp instanceof Constant);
		if (exp instanceof Rational || exp.toString().startsWith("-")) parab = true;
		if (paraa) result = "(" + base.toString() + ")^";
		else result = base.toString() + "^";
		if (parab) return result + "(" + exp.toString() + ")";
		else return result + exp.toString();
	}
	
	public Expression simplify()
	{
		base = base.simplify(); exp = exp.simplify();
		if (exp instanceof Unique || base instanceof Unique) return base.pow(exp);
		if (base instanceof Constant && exp instanceof Num) return base.pow(exp);
		else if (exp instanceof Num) return base.pow(exp);
		else if (base.equals(Int.ZERO) || base.equals(Float.ZERO)) return base;
		else if (exp instanceof Num) return new Prod(base, (Num) exp);
		return new Power(base, exp);
	}
	
	public Expression negate() {return this.mul(Int.ONE.negate());}
	public Expression inverse() {return this.pow(Int.ONE.negate());}
	
	public boolean equals(Expression e)
	{
		return (e instanceof Power) && (((Power) e).base.equals(base) && ((Power) e).exp.equals(exp));
	}
	
	public Expression substitute(Variable x, Expression e)
	{
		return new Power(base.substitute(x, e), exp.substitute(x, e)).simplify();
	}
	
	public Power copy() {return new Power(base, exp);}
}