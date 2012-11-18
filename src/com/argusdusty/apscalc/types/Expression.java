package com.argusdusty.apscalc.types;

import com.argusdusty.apscalc.utils.CalculusUtils;
import com.argusdusty.apscalc.utils.MathUtils;

public abstract class Expression
{
	public abstract Expression simplify();
	public Expression derivative(Variable x) {return CalculusUtils.derivative(this, x);}
	public Expression derivative(Variable x, int c)
	{
		if (c < 0) throw new Error("Argument 3 of derivative must be positive.");
		if (c == 0) return copy();
		Expression r = derivative(x);
		for(int i = 1; i < c; i++) {r = r.derivative(x);}
		return r;
	}
	public abstract String toString();
	public Expression add(Expression e) {return MathUtils.add(this, e);}
	public Expression sub(Expression e) {return MathUtils.add(this, e.negate());}
	public Expression mul(Expression e) {return MathUtils.mul(this, e);}
	public Expression div(Expression e) {return MathUtils.mul(this, e.inverse());}
	public Expression pow(Expression e) {return MathUtils.pow(this, e);}
	public abstract boolean equals(Expression e);
	public abstract Expression substitute(Variable x, Expression e);
	public Expression integrate(Variable x) {return CalculusUtils.integrate(this, x);}
	// public abstract Expression integral(Variable x, Constant a, Constant b);
	public Expression negate() {return this.mul(Int.NEGONE);}
	public Expression inverse() {return this.pow(Int.NEGONE);}
	public abstract Expression copy();
}
