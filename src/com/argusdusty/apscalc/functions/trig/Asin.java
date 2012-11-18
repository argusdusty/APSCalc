package com.argusdusty.apscalc.functions.trig;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.functions.Ln;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.mathconsts.Pi;

public class Asin extends TrigFunction
{
	public Asin(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "asin";
	}
	
	public Expression simplify()
	{
		if (arg1.equals(Int.ZERO) || arg1.equals(Float.ZERO)) return arg1;
		else if (arg1.equals(Int.ONE)) return new Pi().mul(Rational.HALF);
		else if (arg1 instanceof Num && ((Num) arg1).signum() == -1) return new Asin(arg1.negate()).simplify().negate();
		if (arg1 instanceof Sin) return ((Sin) arg1).arg1;
		if (arg1 instanceof Float) return new Float(FastMath.asin(((Float) arg1).value));
		return new Asin(arg1);
	}
	
	public Expression derivative(Variable x)
	{
		// d/dx(asin(f(x))) = f'(x)*(1-f(x)^2)^(-1/2)
		Expression r = arg1.derivative(x);
		r = r.mul(Int.ONE.add(arg1.pow(Int.TWO).negate()).pow(new Rational(Int.ONE,Int.TWO).negate()));
		return r;
	}
	
	public Expression substitute(Variable x, Expression e) {return new Asin(arg1.substitute(x, e).simplify()).simplify();}
	public Sin finverse(Expression e) {return new Sin(e);}
	
	public Expression expForm() // asin(x)=-i*ln(i*x+(1-x^2)^(1/2))
	{
		Expression i = Int.ONE.negate().pow(Rational.HALF);
		return i.mul(new Ln(i.mul(arg1).add(Int.ONE.add(arg1.pow(Int.TWO).negate()).pow(Rational.HALF))).simplify());
	}
	
	public Asin copy() {return new Asin(arg1);}
}
