package com.argusdusty.apscalc.types;

import com.argusdusty.apscalc.errors.UnexpectedError;

public class EmptyExpression extends Expression
{
	public Expression pow(Expression e) {throw new UnexpectedError("eep");}
	public Expression simplify() {return new EmptyExpression();}
	public Expression derivative(Variable x) {return new EmptyExpression();}
	public String toString() {return "";}
	public boolean equals(Expression e) {return (e instanceof EmptyExpression);}
	public Expression substitute(Variable x, Expression e) {return new EmptyExpression();}
	public Int negate() {return Int.ONE.negate();}
	public EmptyExpression inverse() {throw new UnexpectedError("eei");}
	public EmptyExpression copy() {return new EmptyExpression();}
}
