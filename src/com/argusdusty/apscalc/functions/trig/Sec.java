package com.argusdusty.apscalc.functions.trig;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Variable;

public class Sec extends TrigFunction
{
	public Sec(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "sec";
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof Acos) return ((Acos) arg1).arg1.inverse();
		if (arg1 instanceof Float) return new Float(FastMath.sec(((Float) arg1).value));
		return new Cos(arg1).simplify().inverse();
	}
	
	public Expression derivative(Variable x) // d/dx(sec(f(x))) = tan(f(x))*sec(f(x))*f'(x)
	{
		return new Tan(arg1).mul(new Sec(arg1)).mul(arg1.derivative(x));
	}
	
	public Expression substitute(Variable x, Expression e) {return new Sec(arg1.substitute(x, e).simplify()).simplify();}
	public Acos finverse(Expression e) {return new Acos(e.inverse());}
	public Expression expForm() {return new Cos(arg1).expForm().inverse();}
	public Sec copy() {return new Sec(arg1);}
}
