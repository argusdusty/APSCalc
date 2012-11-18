package com.argusdusty.apscalc.functions.trig;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.mathconsts.Pi;

public class Acos extends TrigFunction
{
	public Acos(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "acos";
	}
	
	public Expression simplify()
	{
		if (arg1.equals(Int.ZERO)) return new Pi().mul(Rational.HALF);
		if (arg1.equals(Float.ZERO)) return new Pi().mul(Float.HALF);
		if (arg1 instanceof Cos) return ((Cos) arg1).arg1;
		if (arg1 instanceof Float) return new Float(FastMath.acos(((Float) arg1).value));
		return new Acos(arg1);
	}
	
	public Expression derivative(Variable x) // d/dx(asin(f(x))) = -f'(x)*(1-f(x)^2)^(-1/2)
	{
		Expression r = arg1.derivative(x);
		r = r.mul(Int.ONE.add(arg1.pow(Int.TWO).negate()).pow(new Rational(Int.ONE,Int.TWO).negate()));
		return r.negate();
	}
	
	public Expression substitute(Variable x, Expression e) {return new Acos(arg1.substitute(x, e).simplify()).simplify();}
	public Cos finverse(Expression e) {return new Cos(e);}
	
	public Expression expForm() // acos(x)=1/2*(Pi-2*asin(x))
	{
		return Rational.HALF.mul(new Pi().add(Int.TWO.mul(new Asin(arg1).expForm()).negate()));
	}
	
	public Acos copy() {return new Acos(arg1);}
}
