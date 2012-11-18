package com.argusdusty.apscalc.functions;

import com.argusdusty.apscalc.errors.DerivativeError;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Variable;

public class Mod extends BivarFunction
{
	public Mod(Expression arg1, Expression arg2) {this.arg1 = arg1; this.arg2 = arg2; this.name = "mod";}
	public Expression simplify() {return arg1.sub((new ToInt(arg1.div(arg2)).simplify()).mul(arg2));}
	public Expression derivative(Variable x) {throw new DerivativeError(name);}
	public Expression substitute(Variable x, Expression e)
	{
		return new Mod(arg1.substitute(x, e), arg2.substitute(x, e)).simplify();
	}
	public Mod copy() {return new Mod(arg1, arg2);}
}
