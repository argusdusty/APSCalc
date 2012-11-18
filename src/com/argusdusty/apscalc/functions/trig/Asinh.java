package com.argusdusty.apscalc.functions.trig;

import com.argusdusty.apscalc.functions.Ln;
import com.argusdusty.apscalc.functions.UnivarFunction;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;

public class Asinh extends UnivarFunction
{
	public Asinh(Expression arg1) {this.arg1 = arg1; this.name = "asinh";}
	public Expression derivative(Variable x) {return arg1.pow(Int.TWO).add(Int.ONE).pow(Rational.HALF.negate());}
	public Expression finverse(Expression e) {return new Sinh(e);}
	public Expression simplify() {return new Ln(arg1.add(arg1.pow(Int.TWO).add(Int.ONE).pow(Rational.HALF))).simplify();}
	public Expression substitute(Variable x, Expression e) {return new Asinh(arg1.substitute(x, e)).simplify();}
	public Expression copy() {return new Asinh(arg1);}
}
