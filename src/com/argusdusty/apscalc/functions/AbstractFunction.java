package com.argusdusty.apscalc.functions;

import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Sum;
import com.argusdusty.apscalc.types.Variable;

public abstract class AbstractFunction extends Expression
{
	public String name;
	public Expression negate() {return new Sum(this, Int.NEGONE);}
	public Expression inverse() {return new Prod(this, Int.NEGONE);}
	public abstract Expression derivative(Variable x);
	public abstract Expression finverse(Expression e);
}
