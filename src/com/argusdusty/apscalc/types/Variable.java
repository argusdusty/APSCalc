package com.argusdusty.apscalc.types;

public class Variable extends Expression
{
	public String name;
	public Variable(String name) {this.name = name;}
	public String toString() {return name;}
	public Variable simplify() {return copy();}
	public boolean equals(Expression e) {return (e instanceof Variable) && ((Variable) e).name.equals(name);}
	public Expression substitute(Variable x, Expression e) {return equals(x)?e:copy();}
	public Variable copy() {return new Variable(name);}
}