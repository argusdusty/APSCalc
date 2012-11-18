package com.argusdusty.apscalc.types.arrays;

import java.util.ArrayList;

import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.utils.SortUtils;

public class Expseq extends Expression
{
	public ArrayList<Expression> args;
	
	public Expseq(ArrayList<Expression> a)
	{
		this.args = new ArrayList<Expression>();
		Expression e; boolean set = false, placed = false;
		for (int i = 0; i < a.size(); i++)
		{
			set = false;
			e = a.get(i);
			for (int j = 0; j < args.size(); j++) {if (args.get(j).equals(e)) {set = true; break;}}
			if (!set)
			{
				placed = false;
				for (int j = 0; j < args.size(); j++)
				{
					if (SortUtils.isFirstSum(e, args.get(j)) == 1) {args.add(j, e); placed = true; break;}
				}
				if (!placed) {args.add(e);}
			}
		}
	}
	
	public String toString()
	{
		if (args.size() == 0) return "{}";
		String result = "{";
		for (int i = 0; i < args.size(); i++) result += args.get(i).toString() + ", ";
		return result.substring(0, result.length() - 2) + "}";
	}
	
	public Expression simplify()
	{
		if (args.size() == 1) return args.get(0);
		ArrayList<Expression> v = new ArrayList<Expression>(args);
		for (int i = 0; i < v.size(); i++) v.set(i, v.get(i).simplify());
		return new Expseq(v);
	}

	public boolean equals(Expression e)
	{
		if (!(e instanceof Expseq)) return false;
		ArrayList<Expression> v = new ArrayList<Expression>(((Expseq) e).args);
		if (v.size() != args.size()) return false;
		for (int i = 0; i < v.size(); i++)
		{
			if (!(v.get(i).equals(args.get(i)))) return false;
		}
		return true;
	}

	public Expression substitute(Variable x, Expression e)
	{
		ArrayList<Expression> a = new ArrayList<Expression>();
		for (int i = 0; i < args.size(); i ++)
			a.add(args.get(i).substitute(x, e));
		return new Expseq(a);
	}
	
	public Expseq copy() {return new Expseq(args);}
}