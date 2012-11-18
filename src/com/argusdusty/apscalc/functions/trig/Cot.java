package com.argusdusty.apscalc.functions.trig;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Variable;

public class Cot extends TrigFunction
{
	public Cot(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "cot";
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof Atan) return ((Atan) arg1).arg1.inverse();
		if (arg1 instanceof Float) return new Float(FastMath.cot(((Float) arg1).value));
		return new Tan(arg1).simplify().inverse();
	}
	
	public Expression derivative(Variable x) // d/dx(tan(f(x))) = sec(f(x))^2*f'(x)
	{
		return new Sec(arg1).pow(Int.TWO).mul(arg1.derivative(x));
	}
	
	public Expression substitute(Variable x, Expression e) {return new Cot(arg1.substitute(x, e).simplify()).simplify();}
	public Atan finverse(Expression e) {return new Atan(e.inverse());}
	public Expression expForm() {return new Tan(arg1).expForm().inverse();}
	public Cot copy() {return new Cot(arg1);}
}
