package com.argusdusty.apscalc.functions.trig;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.functions.Ln;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.mathconsts.Pi;

public class Atan extends TrigFunction
{
	public Atan(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "atan";
	}
	
	public Expression simplify()
	{
		if (arg1.equals(Int.ZERO)) return Int.ZERO;
		if (arg1.equals(Int.ONE)) return new Pi().div(Int.TWO.add(Int.TWO));
		if (arg1 instanceof Tan) return ((Tan) arg1).arg1;
		if (arg1 instanceof Float) return new Float(FastMath.atan(((Float) arg1).value));
		return new Atan(arg1);
	}
	
	public Expression derivative(Variable x) // d/dx(atan(f(x))) = -f'(x)*(f(x)^2+1)^(-1)
	{
		Expression r = arg1.derivative(x);
		r = r.mul(Int.ONE.add(arg1.pow(Int.TWO)).pow(Int.ONE.negate()));
		return r.negate();
	}
	
	public Expression substitute(Variable x, Expression e) {return new Atan(arg1.substitute(x, e).simplify()).simplify();}
	public Tan finverse(Expression e) {return new Tan(e);}
	
	public Expression expForm() // atan(x)=i/2*(ln(1-i*x)-ln(1+i*x))
	{
		Expression i = Int.ONE.negate().pow(Rational.HALF);
		Expression x = new Ln(Int.ONE.add(i.mul(arg1).negate())).simplify();
		x = x.add(new Ln(Int.ONE.add(i.mul(arg1))).simplify().negate());
		return i.mul(Rational.HALF).mul(x);
	}
	
	public Atan copy() {return new Atan(arg1);}
}
