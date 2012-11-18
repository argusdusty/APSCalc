package com.argusdusty.apscalc.types;


public abstract class Constant extends Expression
{
	boolean CommutativeAdd = true;
	boolean CommutativeMul = true;
	public abstract int signum();
	public Constant substitute(Variable x, Expression e) {return copy();}
	public abstract Constant copy();
}
