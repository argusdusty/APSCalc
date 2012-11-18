package com.argusdusty.apscalc.functions.trig;

import com.argusdusty.apscalc.functions.Ln;
import com.argusdusty.apscalc.functions.UnivarFunction;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;

public class Acosh extends UnivarFunction
{
	public Acosh(Expression arg1) {this.arg1 = arg1; this.name = "asinh";}
	public Expression derivative(Variable x) {return arg1.pow(Int.TWO).sub(Int.ONE).pow(Rational.HALF).inverse();}
	public Expression finverse(Expression e) {return new Cosh(e);}
	public Expression simplify() {return new Ln(arg1.add(arg1.pow(Int.TWO).sub(Int.ONE).pow(Rational.HALF))).simplify();}
	public Expression substitute(Variable x, Expression e) {return new Acosh(arg1.substitute(x, e)).simplify();}
	public Expression copy() {return new Acosh(arg1);}
}
