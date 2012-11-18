package com.argusdusty.apscalc.functions;

import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Variable;

public class Lcm extends BivarFunction
{
	public Lcm(Expression arg1, Expression arg2) {this.arg1 = arg1; this.arg2 = arg2; this.name = "lcm";}
	public Expression simplify() {return arg1.mul(arg2).div(new Gcd(arg1, arg2).simplify());}
	public Expression derivative(Variable x) {throw new Error("Function 'lcm' is non-continuous");}
	public Expression substitute(Variable x, Expression e)
	{
		return new Lcm(arg1.substitute(x, e), arg2.substitute(x, e)).simplify();
	}
	public Lcm copy() {return new Lcm(arg1, arg2);}
}
