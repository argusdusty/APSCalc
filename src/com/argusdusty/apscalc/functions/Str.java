package com.argusdusty.apscalc.functions;

import com.argusdusty.apscalc.errors.DerivativeError;
import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.Text;

public class Str extends UnivarFunction
{
	public Str(Expression arg1) {this.arg1 = arg1; this.name = "str";}
	public Expression derivative(Variable x) {throw new DerivativeError(name);}
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
	public Expression simplify() {return new Text(arg1.toString());}
	public Expression substitute(Variable x, Expression e) {return new Str(arg1.substitute(x, e));}
	public Expression copy() {return new Str(arg1);}
}
