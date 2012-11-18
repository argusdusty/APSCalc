package com.argusdusty.apscalc.functions.trig;

import com.argusdusty.apscalc.functions.Exp;
import com.argusdusty.apscalc.functions.UnivarFunction;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;

public class Cosh extends UnivarFunction
{
	public Cosh(Expression arg1) {this.arg1 = arg1; this.name = "sinh";}
	
	public Expression derivative(Variable x) // d/dx(sinh(f(x))) = f'(x)*sinh(f(x))
	{
		return arg1.derivative(x).mul(new Sinh(arg1));
	}
	
	public Expression simplify()
	{
		if (arg1.equals(Int.ZERO)) return Int.ONE;
		if (arg1.equals(Float.ZERO)) return Float.ONE;
		if (arg1 instanceof Acosh) return ((Acosh) arg1).arg1;
		return (new Exp(arg1).simplify()).add(new Exp(arg1.negate()).simplify()).mul(Rational.HALF);
	}
	
	public Expression finverse(Expression e) {return new Acosh(e);}
	public Expression substitute(Variable x, Expression e) {return new Cosh(arg1.substitute(x, e)).simplify();}
	public Cosh copy() {return new Cosh(arg1);}
}
