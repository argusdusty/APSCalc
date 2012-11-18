package com.argusdusty.apscalc.types.arrays;

import java.util.ArrayList;

import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Variable;

public class List extends Expression
{
	public ArrayList<Expression> args = new ArrayList<Expression>();
	
	public List(ArrayList<Expression> args) {this.args = args;}
	public Expression simplify() {return copy();}
	
	public String toString()
	{
		if (args.size() == 0) return "[]";
		String result = "[" + args.get(0).toString();
		for (int i = 1; i < args.size(); i++) result += ", " + args.get(i).toString();;
		return result + "]";
	}
	
	public boolean equals(Expression e)
	{
		if (!(e instanceof List)) return false;
		ArrayList<Expression> v = new ArrayList<Expression>(((List) e).args);
		if (v.size() != args.size()) return false;
		for (int i = 0; i < v.size(); i++)
			if (!(v.get(i).equals(args.get(i)))) return false;
		return true;
	}
	
	public Expression substitute(Variable x, Expression e)
	{
		ArrayList<Expression> a = new ArrayList<Expression>();
		for (int i = 0; i < args.size(); i ++) a.add(args.get(i).substitute(x, e));
		return new List(a);
	}
	
	public List copy() {return new List(args);}
}
