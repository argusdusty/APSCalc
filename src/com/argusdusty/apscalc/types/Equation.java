package com.argusdusty.apscalc.types;

public class Equation extends Expression
{
	public Expression left;
	public Expression right;

	public Equation(Expression left, Expression right)
	{
		if (left instanceof Equation) throw new Error("Cannot take equality of equality");
		this.left = left;
		if (right instanceof Equation) throw new Error("Cannot take equality of equality");
		this.right = right;
	}
	
	public String toString() {return left.toString() + "=" + right.toString();}
	public Expression simplify() {return new Equation(left.simplify(), right.simplify());}
	public Expression derivative(Variable x) {throw new Error("Cannot take the derivative of an equality");}
	public Expression add(Expression e) {throw new Error("Cannot add to an equation");}
	public Expression mul(Expression e) {throw new Error("Cannot multiply an equation");}
	public Expression pow(Expression e) {throw new Error("Cannot take equation to a power");}
	
	public boolean equals(Expression e)
	{
		return e instanceof Equation && ((Equation) e).left.equals(left) && ((Equation) e).right.equals(right);
	}
	
	public Expression substitute(Variable x, Expression e)
	{
		return new Equation(left.substitute(x, e), right.substitute(x, e));
	}
	
	public Equation copy() {return new Equation(left, right);}
}