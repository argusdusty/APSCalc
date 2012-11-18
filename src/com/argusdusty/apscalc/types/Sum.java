package com.argusdusty.apscalc.types;

import com.argusdusty.apscalc.utils.OrderedMap;
import com.argusdusty.apscalc.utils.SortUtils;

public class Sum extends Expression
{
	public OrderedMap<Expression, Num> args;
	
	public Sum(OrderedMap<Expression, Num> args) {this.args = args;}
	public Sum(Expression e)
	{
		this.args = new OrderedMap<Expression, Num>();
		args.put(e.copy(), Int.ONE);
	}
	public Sum(Expression a, Num b)
	{
		this.args = new OrderedMap<Expression, Num>();
		if (a instanceof Num) args.put(((Num) a).copy().mul(b.copy()), Int.ONE);
		else args.put(a.copy(), b.copy());
	}
	
	private int in(OrderedMap<Expression, Num> a, Expression e)
	{for (int i = 0; i < a.size(); i++) {if (a.getKey(i).equals(e)) return i;} return -1;}
	
	public Expression append(Expression a)
	{
		OrderedMap<Expression, Num> temp = new OrderedMap<Expression, Num>(args);
		if (a instanceof Sum)
		{
			Sum p = (Sum) a;
			for (int i = 0; i < p.args.size(); i++) temp = appendElement(p.args.getKey(i), p.args.getVal(i), temp);
			if (temp.size() == 0) return Int.ZERO;
			if (temp.size() == 1 && (temp.getKey(0) instanceof Num)) return temp.getKey(0).mul(temp.getVal(0));
			return new Sum(temp);
		}
		else temp = appendElement(a, Int.ONE, temp);
		if (temp.size() == 0) return Int.ZERO;
		if (temp.size() == 1)
		{
			if (temp.getKey(0) instanceof Num) return temp.getKey(0).mul(temp.getVal(0));
			if (temp.getVal(0).equals(Int.ONE)) return temp.getKey(0);
		}
		return new Sum(temp);
	}
	
	public OrderedMap<Expression, Num> appendElement(Expression a, Num b, OrderedMap<Expression, Num> temp)
	{
		int x;
		if (a instanceof Num) // reduce numerals by combining them together
		{
			for (int j = 0; j < temp.size(); j++)
			{
				if (temp.getKey(j) instanceof Num && temp.getVal(j).equals(b))
				{
					Num r = ((Num) a).add((Num) temp.getKey(j));
					if (r.equals(Int.ZERO) || r.equals(Float.ZERO)) temp.remove(j);
					else temp.setKey(j, r);
					return temp;
				}
			}
		}
		x = in(temp, a);
		if (x != -1) // if a already exists in the product, combine their exponents
		{
			temp.setVal(x, temp.getVal(x).add(b));
			if (temp.getVal(x).equals(Int.ZERO) || temp.getVal(x).equals(Float.ZERO)) temp.remove(x);
			else if (temp.getKey(x) instanceof Num)
				if (temp.getVal(x) instanceof Int || temp.getVal(x) instanceof Float)
					temp.setPair(x, temp.getKey(x).pow(temp.getVal(x)), Int.ONE);
		}
		else if (b.signum() < 0) // if the exponent is negative, sort it with the divisors (negative exponents)
		{
			int j = temp.size() - 1;
			while (j >= 0 && temp.getVal(j).signum() < 0 && SortUtils.isFirstSum(a, temp.getKey(j)) > 0) j--;
			if (j == 0 && SortUtils.isFirstSum(a, temp.getKey(0)) > 0 && temp.getVal(j).signum() < 0) temp.add(0, a, b);
			else temp.add(j + 1, a, b);
		}
		else // go ahead and sort it in
		{
			int j = temp.size() - 1;
			//while (j >= 0 && temp.getVal(j).signum() < 0) j--;
			while (j >= 0 && (SortUtils.isFirstSum(a, temp.getKey(j)) > 0 || temp.getVal(j).signum() < 0)) j--;
			if (j == 0 && (SortUtils.isFirstSum(a, temp.getKey(0)) > 0 || temp.getVal(0).signum() < 0)) temp.add(0, a, b);
			else temp.add(j + 1, a, b);
		}
		return temp;
	}
	
	/*public Expression append(Expression a)
	{
		OrderedMap<Expression, Num> temp = new OrderedMap<Expression, Num>(args);
		int x;
		if (a instanceof Sum)
		{
			Sum p = (Sum) a;
			for (int i = 0; i < p.args.size(); i++)
			{
				Expression k = p.args.getKey(i);
				boolean set = false;
				if (k instanceof Num)
				{
					for (int j = 0; j < args.size(); j++)
					{
						if (temp.getKey(j) instanceof Num)
						{
							Num r = ((Num) k).add((Num) temp.getKey(j));
							if (r.equals(Int.ZERO) || r.equals(Float.ZERO)) temp.remove(j);
							else temp.setKey(j, r);
							set = true;
						}
					}
				}
				if (set) continue;
				x = in(temp, k);
				if (x != -1)
				{
					temp.setVal(x, args.getVal(x).add(p.args.values.get(0)));
					if (temp.getVal(x).equals(Int.ZERO) || temp.getVal(x).equals(Float.ZERO)) temp.remove(x);
				}
				else if (p.args.getVal(0).signum() < 0)
				{
					int j = args.size() - 1;
					while (j >= 0 && args.getVal(j).signum() < 0 && SortUtils.isFirstSum(k, args.getKey(j)) > 0) j--;
					if (j == 0 && SortUtils.isFirstSum(k,  args.getKey(0)) > 0 && args.getVal(j).signum() < 0)
						temp.add(0, k, p.args.getVal(0));
					else temp.add(j + 1, k, p.args.getVal(0));
				}
				else
				{
					int j = args.size() - 1;
					while (j >= 0 && (SortUtils.isFirstSum(k, args.getKey(j)) > 0 || args.getVal(j).signum() < 0)) j--;
					if (j == 0 && (SortUtils.isFirstSum(k,  args.getKey(0)) > 0 || args.getVal(0).signum() < 0))
						temp.add(0, k, p.args.getVal(0));
					else temp.add(j + 1, k, p.args.getVal(0));
				}
			}
			if (temp.size() == 0) return Int.ZERO;
			if (temp.size() == 1 && (temp.getKey(0) instanceof Num)) return temp.getKey(0).mul(temp.getVal(0));
			return new Sum(temp);
		}
		if (a instanceof Num)
		{
			for (int i = 0; i < args.size(); i++)
			{
				if (temp.getKey(i) instanceof Num)
				{
					Num r = ((Num) a).add((Num) temp.getKey(i));
					if (r.equals(Int.ZERO) || r.equals(Float.ZERO)) temp.remove(i);
					else temp.setKey(i, r);
					if (temp.size() == 0) return Int.ZERO;
					if (temp.size() == 1 && (temp.getKey(0) instanceof Num)) return temp.getKey(0).mul(temp.getVal(0));
					return new Sum(temp);
				}
			}
		}
		x = in(temp, a);
		if (x != -1)
		{
			temp.setVal(x, args.getVal(x).add(Int.ONE));
			if (temp.getVal(x).equals(Int.ZERO)) temp.remove(x);
		}
		else
		{
			int i = args.size() - 1;
			while (i >= 0 && (SortUtils.isFirstSum(a, args.getKey(i)) > 0 || args.getVal(i).signum() < 0)) i--;
			if (i == 0 && SortUtils.isFirstSum(a, args.getKey(0)) > 0 || args.getVal(0).signum() < 0)
				temp.add(0, a, Int.ONE);
			else temp.add(i + 1, a, Int.ONE);
		}
		if (temp.size() == 0) return Int.ZERO;
		if (temp.size() == 1 && (temp.getKey(0) instanceof Num)) return temp.getKey(0).mul(temp.getVal(0));
		return new Sum(temp);
	}*/
	
	public String toString()
	{
		String result = "", a, b; boolean nega, negb;
		for (int i = 0; i < args.size(); i++)
		{
			a = args.getVal(i).toString(); b = args.getKey(i).toString();
			nega = a.startsWith("-"); negb = b.startsWith("-");
			if (nega) a = a.substring(1);
			if (negb) b = b.substring(1);
			if (nega ^ negb) result += "-";
			else if (i >= 1) result += "+";
			if (a.equals("1")) result += b;
			else if (b.equals("1")) result += a;
			else if (args.getVal(i) instanceof Rational)
			{
				Rational p = (Rational) args.getVal(i);
				if (p.num.toString().equals("1") || p.num.toString().equals("-1"))
					result += b + "/" + p.denom.toString();
				else if (nega) result += p.num.toString().substring(1) + "*" + b + "/" + p.denom.toString();
				else result += p.num.toString() + "*" + b + "/" + p.denom.toString();
			}
			else result += a + "*" + b;
		}
		return result;
	}
	
	public Sum simplify() {return copy();}
		
	public Expression inverse()
	{
		if (args.size() == 1) return args.getKey(0).inverse().mul(args.getVal(0).inverse());
		return this.pow(Int.ONE.negate());
	}

	public boolean equals(Expression e)
	{
		if (!(e instanceof Sum)) return false;
		Sum s = (Sum) e;
		if (args.size() != s.args.size()) return false;
		for (int i = 0; i < args.size(); i++)
		{
			if (!args.getKey(i).equals(s.args.getKey(i))) return false;
			if (!args.getVal(i).equals(s.args.getVal(i))) return false;
		}
		return true;
	}

	public Expression substitute(Variable x, Expression e)
	{
		Expression t = Int.ZERO;
		for (int i = 0; i < args.size(); i++)
			t = t.add(args.getKey(i).substitute(x, e)).mul(args.getVal(i).substitute(x, e));
		return t;
	}
	
	public Sum copy() {return new Sum(args);}
}