package com.argusdusty.apscalc.types.arrays;

import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Variable;

public class Text extends Expression
{
	public String text;
	public Text(String s) {this.text = s;}
	public Expression simplify() {return copy();}
	public String toString() {return "\"" + text + "\"";}
	public boolean equals(Expression e) {return e instanceof Text && ((Text) e).text.equalsIgnoreCase(text);}
	public Expression substitute(Variable x, Expression e) {return copy();}
	public Expression copy() {return new Text(text);}
}
