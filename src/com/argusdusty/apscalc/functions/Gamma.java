package com.argusdusty.apscalc.functions;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.errors.DerivativeError;
import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Variable;

public class Gamma extends UnivarFunction
{
	public Gamma(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "gamma";
	}

	public Expression simplify()
	{
		if (arg1 instanceof Float) return new Float(FastMath.gamma(((Float) arg1).value));
		if (arg1 instanceof Int)
		{
			/*if (((Int) arg1).signum() != -1)*/ return new Fact(arg1.sub(Int.ONE)).simplify();
			//return new Float(FastMath.gamma(new Float(arg1)));
		}
		return copy();
	}

	public Expression derivative(Variable x) {throw new DerivativeError(name);}
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
	public Expression substitute(Variable x, Expression e) {return new Gamma(arg1.substitute(x, e)).simplify();}
	public Gamma copy() {return new Gamma(arg1);}
}
