package com.argusdusty.apscalc.functions;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.functions.trig.Cos;
import com.argusdusty.apscalc.functions.trig.Sin;
import com.argusdusty.apscalc.types.Complex;
import com.argusdusty.apscalc.types.EmptyExpression;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Sum;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.mathconsts.E;
import com.argusdusty.apscalc.types.mathconsts.Indeterminate;
import com.argusdusty.apscalc.types.mathconsts.Inf;
import com.argusdusty.apscalc.utils.OrderedMap;

public class Exp extends UnivarFunction
{
	public Exp(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "exp";
	}

	public Expression simplify()
	{
		if (arg1 instanceof Inf || arg1 instanceof Indeterminate) return arg1;
		if (arg1 instanceof Float) return new Float(FastMath.exp(((Float) arg1).value));
		if (arg1 instanceof Complex)
		{
			Expression a = ((Complex) arg1).real, b = ((Complex) arg1).imag;
			Expression r = new Exp(a).simplify().mul(new Cos(b).simplify());
			Expression i = new Exp(a).simplify().mul(new Sin(b).simplify());
			return new Complex(r, i);
		}
		if (arg1 instanceof Ln) return ((Ln) arg1).arg1;
		if (arg1 instanceof Sum) //exp(f(x)*a+g(x)*b) = exp(f(x)*a)*exp(g(x)*b)
		{
			OrderedMap<Expression, Num> v = new OrderedMap<Expression, Num>(((Sum) arg1).args);
			if (v.size() == 1) return (new E()).pow(arg1);
			Expression r = new EmptyExpression();
			for (int i = 0; i < v.size(); i++)
			{
				r = r.mul(new Exp(v.getKey(i)).simplify());
			}
			return r;
		}
		return (new E()).pow(arg1);
	}

	public Expression derivative(Variable x) // d/dx(exp(f(x))) = exp(f(x))*f'(x)
	{
		return new Exp(arg1).mul(arg1.derivative(x));
	}
	public Expression substitute(Variable x, Expression e) {return new Exp(arg1.substitute(x, e)).simplify();}
	public Ln finverse(Expression e) {return new Ln(e);}
	public Exp copy() {return new Exp(arg1);}
}
