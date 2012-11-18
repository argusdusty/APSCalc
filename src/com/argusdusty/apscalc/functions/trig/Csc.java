package com.argusdusty.apscalc.functions.trig;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Variable;

public class Csc extends TrigFunction
{
	public Csc(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "csc";
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof Asin) return ((Asin) arg1).arg1.inverse();
		if (arg1 instanceof Float) return new Float(FastMath.csc(((Float) arg1).value));
		return new Sin(arg1).simplify().inverse();
	}
	
	public Expression derivative(Variable x) // d/dx(csc(f(x))) = -cot(f(x))*csc(f(x))*f'(x)
	{
		return new Cot(arg1).mul(new Csc(arg1)).mul(arg1.derivative(x)).negate();
	}
	
	public Expression substitute(Variable x, Expression e) {return new Csc(arg1.substitute(x, e).simplify()).simplify();}
	public Asin finverse(Expression e) {return new Asin(e.inverse());}
	public Expression expForm() {return new Sin(arg1).expForm().inverse();}
	public Csc copy() {return new Csc(arg1);}
}
