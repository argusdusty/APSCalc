package com.argusdusty.apscalc.functions;

import java.math.BigInteger;

import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.mathconsts.E;
import com.argusdusty.apscalc.types.mathconsts.Indeterminate;
import com.argusdusty.apscalc.types.mathconsts.Pi;
import com.argusdusty.apscalc.types.mathconsts.Unique;
import com.argusdusty.apscalc.utils.EvalfUtils;

public class ToInt extends UnivarFunction 
{
	public ToInt(Expression arg1) {this.name = "int"; this.arg1 = arg1;}
	public Expression derivative(Variable x) {return Int.ZERO;}
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
	
	public Expression simplify()
	{
		if (arg1 instanceof Float) return new Int(((Float) arg1).value.toBigInteger());
		if (arg1 instanceof Rational)
		{
			BigInteger n = ((Rational) arg1).num, d = ((Rational) arg1).denom;
			return new Int(n.divide(d));
		}
		if (arg1 instanceof Pi) return new Int("3");
		if (arg1 instanceof E) return new Int("2");
		if (arg1 instanceof Int) return arg1.copy();
		if (arg1 instanceof Unique) return new Indeterminate();
		try
		{
			Expression e = EvalfUtils.evalf(arg1);
			if (e instanceof Float) return new Int(((Float) e).value.toBigInteger());
		}
		catch (Exception e) {}
		return copy();
	}

	public Expression substitute(Variable x, Expression e) {return new ToInt(arg1.substitute(x, e)).simplify();}
	public Expression copy() {return new ToInt(arg1);}

}
