package com.argusdusty.apscalc.types;

import com.argusdusty.apscalc.functions.AbstractFunction;
import com.argusdusty.apscalc.functions.Ln;
import com.argusdusty.apscalc.types.arrays.Expseq;
import com.argusdusty.apscalc.types.arrays.List;
import com.argusdusty.apscalc.types.arrays.Matrix;
import com.argusdusty.apscalc.types.arrays.Text;
import com.argusdusty.apscalc.utils.OrderedMap;
import com.argusdusty.apscalc.utils.SortUtils;

public class Prod extends Expression
{
	public OrderedMap<Expression, Num> args;
	
	public Prod(OrderedMap<Expression, Num> args) {this.args = args;}
	public Prod(Expression e)
	{
		this.args = new OrderedMap<Expression, Num>();
		args.put(e.copy(), Int.ONE);
	}
	public Prod(Expression a, Num b)
	{
		this.args = new OrderedMap<Expression, Num>();
		args.put(a.copy(), b.copy());
	}
	
	private int in(OrderedMap<Expression, Num> a, Expression e)
	{for (int i = 0; i < a.size(); i++) {if (a.getKey(i).equals(e)) return i;} return -1;}
	
	public Expression append(Expression a)
	{
		OrderedMap<Expression, Num> temp = new OrderedMap<Expression, Num>(args);
		if (a instanceof Prod)
		{
			Prod p = (Prod) a;
			for (int i = 0; i < p.args.size(); i++)
				temp = appendElement(p.args.getKey(i), p.args.getVal(i), temp);
			if (temp.size() == 0) return Int.ONE;
			if (temp.size() == 1 && (temp.getKey(0) instanceof Num) && temp.getVal(0).equals(Int.ONE)) return temp.getKey(0);
			return new Prod(temp);
		}
		else temp = appendElement(a, Int.ONE, temp);
		if (temp.size() == 0) return Int.ONE;
		if (temp.size() == 1 && (temp.getKey(0) instanceof Num))
		{
			if (temp.getKey(0) instanceof Num) return temp.getKey(0).pow(temp.getVal(0));
			if (temp.getVal(0).equals(Int.ONE)) return temp.getKey(0);
		}
		return new Prod(temp);
	}
	
	public OrderedMap<Expression, Num> appendElement(Expression a, Num b, OrderedMap<Expression, Num> temp)
	{
		int x;
		if (a instanceof Num) // reduce numerals by combining them together
		{
			for (int j = 0; j < args.size(); j++)
			{
				if (temp.getKey(j) instanceof Num && temp.getVal(j).equals(b))
				{
					Num r = ((Num) a).mul((Num) temp.getKey(j));
					if (r.equals(Int.ONE) || r.equals(Float.ONE)) temp.remove(j);
					else temp.setKey(j, r);
					return temp;
				}
			}
		}
		if (a instanceof Power)
		{
			Expression base = ((Power) a).base;
			for (int i = 0; i < temp.size(); i++)
			{
				Expression t = temp.getKey(i);
				if (t.equals(base))
				{
					temp.setPair(i, new Power(base, ((Power) a).exp.add(temp.getVal(i))).simplify(), Int.ONE);
					return temp;
				}
				else if (t instanceof Power)
				{
					Expression exp = ((Power) a).exp; Power p = (Power) t;
					if (p.base.equals(base))
					{
						temp.setPair(i, new Power(base, exp.add(p.exp)).simplify(), Int.ONE);
						return temp;
					}
					Expression r = new Ln(p.base).simplify().mul(new Ln(base).simplify().inverse());
					if (r instanceof Constant)
					{
						temp.setPair(i, new Power(base, exp.add(r.mul(p.exp))).simplify(), Int.ONE);
						return temp;
					}
				}
			}
		}
		x = in(temp, a);
		if (x != -1) // if a already exists in the product, combine their exponents
		{
			temp.setVal(x, args.getVal(x).add(b));
			if (temp.getVal(x).equals(Int.ZERO) || temp.getVal(x).equals(Float.ZERO)) temp.remove(x);
			else if (temp.getKey(x) instanceof Num)
				if (temp.getVal(x) instanceof Int || temp.getVal(x) instanceof Float)
					temp.setPair(x, temp.getKey(x).pow(temp.getVal(x)), Int.ONE);
		}
		else if (b.signum() < 0) // if the exponent is negative, sort it with the divisors (negative exponents)
		{
			int j = args.size() - 1;
			while (j >= 0 && args.getVal(j).signum() < 0 && SortUtils.isFirstProd(a, args.getKey(j)) > 0) j--;
			if (j == 0 && SortUtils.isFirstProd(a,  args.getKey(0)) > 0 && args.getVal(j).signum() < 0) temp.add(0, a, b);
			else temp.add(j + 1, a, b);
		}
		else // go ahead and sort it in
		{
			int j = args.size() - 1;
			//while (j >= 0 && args.getVal(j).signum() < 0) j--;
			while (j >= 0 && (SortUtils.isFirstProd(a, args.getKey(j)) > 0 || args.getVal(j).signum() < 0)) j--;
			if (j == 0 && (SortUtils.isFirstProd(a, args.getKey(0)) > 0 || args.getVal(0).signum() < 0)) temp.add(0, a, b);
			else temp.add(j + 1, a, b);
		}
		return temp;
	}
	
	public String toString()
	{
		Num t; Expression p;
		String result = "", a, b;
		boolean nega, parab;
		for (int i = 0; i < args.size(); i++)
		{
			t = args.getVal(i); p = args.getKey(i); a = t.toString(); b = p.toString();
			nega = a.startsWith("-");
			parab = !(p instanceof Variable || p instanceof AbstractFunction || p instanceof Constant || p instanceof Power);
			parab = !(!parab || p instanceof Matrix || p instanceof Expseq || p instanceof List || p instanceof Text);
			if (p instanceof Rational) parab = true;
			if (p instanceof Sum && ((Sum) p).args.size() == 1 && t.signum() > 0) parab = false;
			if (nega) parab = parab || (p instanceof Prod || p instanceof Rational || p instanceof Power);
			if (nega) {a = a.substring(1); if (i == 0) result += "1/"; else result += "/";}
			else if (i > 0) result += "*";
			if (parab) b = "(" + b + ")";
			if (t instanceof Rational) a = "(" + a + ")";
			if (a.equals("1")) result += b;
			else if (b.equals("1")) result += "";
			else result += b + "^" + a;
		}
		if (result.length() == 0) return "1";
		return result;
	}
	
	public Prod simplify() {return copy();}
	
	public boolean equals(Expression e)
	{
		if (!(e instanceof Prod)) return false;
		Prod p = (Prod) e;
		if (args.size() != p.args.size()) return false;
		for (int i = 0; i < args.size(); i++)
		{
			if (!args.getKey(i).equals(p.args.getKey(i))) return false;
			if (!args.getVal(i).equals(p.args.getVal(i))) return false;
		}
		return true;
	}

	public Expression substitute(Variable x, Expression e)
	{
		Expression t = Int.ONE;
		for (int i = 0; i < args.size(); i++)
			t = t.mul(args.getKey(i).substitute(x, e)).pow(args.getVal(i).substitute(x, e));
		return t;
	}
	
	public Prod copy() {return new Prod(args);}
}