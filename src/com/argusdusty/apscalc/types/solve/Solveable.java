package com.argusdusty.apscalc.types.solve;

import com.argusdusty.apscalc.types.Expression;

public abstract class Solveable extends Expression
{
	public Expression var;
	
	public abstract Solveable inverse();
	public abstract Solveable add(Expression e);
	public abstract Solveable mul(Expression e);
	public abstract Solveable pow(Expression e);
}
