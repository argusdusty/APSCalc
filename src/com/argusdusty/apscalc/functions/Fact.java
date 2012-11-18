package com.argusdusty.apscalc.functions;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.types.Constant;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;

public class Fact extends UnivarFunction
{
	public Fact(Expression arg1) {this.arg1 = arg1; this.name = "fact";}
	public String toString()
	{
		boolean parab = !(arg1 instanceof Variable || arg1 instanceof AbstractFunction || arg1 instanceof Constant);
		if (arg1 instanceof Rational) parab = true;
		if (parab) return "(" + arg1.toString() + ")!";
		return arg1.toString() + "!";
	}
	public Expression simplify()
	{
		if (arg1 instanceof Int) return new Int(FastMath.fact(((Int) arg1).value));
		return copy();
	}
	public Expression derivative(Variable x) {throw new Error("Function 'fact' is non-continuous. Try gamma.");}
	public Fact substitute(Variable x, Expression e) {return new Fact(arg1.substitute(x, e));}
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
	public Fact copy() {return new Fact(arg1);}
}
