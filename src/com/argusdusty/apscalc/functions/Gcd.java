package com.argusdusty.apscalc.functions;

import java.math.BigInteger;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;

public class Gcd extends BivarFunction
{
	public Gcd(Expression arg1, Expression arg2)
	{
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.name = "gcd";
	}
	
	public Expression simplify()
	{
		if (!(arg1 instanceof Num) || !(arg2 instanceof Num)) return copy();
		if (arg1 instanceof Float) return new Float((Num) new Gcd(((Float) arg1).exactVal(), arg2).simplify());
		if (arg2 instanceof Float) return new Float((Num) new Gcd(arg1, ((Float) arg2).exactVal()).simplify());
		if (arg1 instanceof Int && arg2 instanceof Int) return new Int(FastMath.gcd(((Int) arg1).value, ((Int) arg2).value));
		if (arg1 instanceof Int)
		{
			BigInteger a = ((Int) arg1).value.multiply(((Rational) arg2).denom);
			BigInteger b = ((Rational) arg2).num;
			new Rational(FastMath.gcd(a, b), ((Rational) arg2).denom).simplify();
		}
		if (arg2 instanceof Int)
		{
			BigInteger a = ((Rational) arg1).num;
			BigInteger b = ((Int) arg2).value.multiply(((Rational) arg1).denom);
			return new Rational(FastMath.gcd(a, b), ((Rational) arg1).denom).simplify();
		}
		BigInteger a = ((Rational) arg1).num.multiply(((Rational) arg2).denom);
		BigInteger b = ((Rational) arg2).num.multiply(((Rational) arg1).denom);
		return new Rational(FastMath.gcd(a, b), ((Rational) arg2).denom.multiply(((Rational) arg1).denom)).simplify();
	}

	public Expression derivative(Variable x) {throw new Error("Function 'gcd' is non-continuous");}
	public Expression substitute(Variable x, Expression e)
	{
		return new Gcd(arg1.substitute(x, e), arg2.substitute(x, e)).simplify();
	}
	public Gcd copy() {return new Gcd(arg1, arg2);}
}
