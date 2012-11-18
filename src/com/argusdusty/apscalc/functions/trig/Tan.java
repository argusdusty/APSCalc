package com.argusdusty.apscalc.functions.trig;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Variable;

public class Tan extends TrigFunction
{
	public Tan(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "tan";
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof Atan) return ((Atan) arg1).arg1;
		if (arg1 instanceof Float) return new Float(FastMath.tan(((Float) arg1).value));
		return new Sin(arg1).simplify().div(new Cos(arg1).simplify());
	}
	
	public Expression derivative(Variable x) // d/dx(tan(f(x))) = sec(f(x))^2*f'(x)
	{
		return new Sec(arg1).pow(Int.TWO).mul(arg1.derivative(x));
	}
	
	public Expression substitute(Variable x, Expression e) {return new Tan(arg1.substitute(x, e).simplify()).simplify();}
	public Atan finverse(Expression e) {return new Atan(e);}
	public Expression expForm() {return new Sin(arg1).expForm().mul(new Cos(arg1).expForm().inverse());}
	public Tan copy() {return new Tan(arg1);}
}
