package com.argusdusty.apscalc.utils;

import java.math.BigInteger;
import java.util.ArrayList;

import com.argusdusty.apscalc.errors.PolyError;
import com.argusdusty.apscalc.functions.AbstractFunction;
import com.argusdusty.apscalc.functions.Gcd;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Power;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Sum;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.solve.Poly;
import com.argusdusty.apscalc.types.solve.PolyFrac;
import com.argusdusty.apscalc.types.solve.Solveable;

public class PolyUtils
{	
	public static Solveable toPoly(Expression x, Expression e)
	{
		ArrayList<Expression> v = new ArrayList<Expression>();
		if (!(SolverUtils.contains(e, x)))
		{
			v.add(e);
			return new Poly(x, v);
		}
		else if (e.equals(x))
		{
			v.add(Int.ZERO); v.add(Int.ONE);
			return new Poly(x, v);
		}
		else if (e instanceof Variable)
		{
			v.add(Int.ONE);
			return new Poly(x, v);
		}
		else if (e instanceof Poly)
		{
			Poly p = (Poly) e;
			if (p.var.equals(x)) return new Poly(x, p.consts);
			else return toPoly(x, toSum(p));
		}
		else if (e instanceof Sum)
		{
			OrderedMap<Expression, Num> a = new OrderedMap<Expression, Num>(((Sum) e).args);
			Solveable p = toPoly(x, a.getKey(0)).mul(a.getVal(0));
			for (int i = 1; i < a.size(); i++) p = p.add(toPoly(x, a.getKey(i)).mul(a.getVal(i)));
			return p;
		}
		else if (e instanceof Prod)
		{
			OrderedMap<Expression, Num> a = new OrderedMap<Expression, Num>(((Prod) e).args);
			Solveable p = toPoly(x, a.getKey(0)).pow(a.getVal(0));
			for (int i = 1; i < a.size(); i++) {p = p.mul(toPoly(x, a.getKey(i)).pow(a.getVal(i)));}
			return p;
		}
		else if (e instanceof AbstractFunction) return toPoly(e, e);
		else if (e instanceof Power)
		{
			Power p = (Power) e;
			if (factorNum(p.exp).signum() == -1) return toPoly(p.inverse(), p.inverse()).inverse();
			if (((Power) e).exp instanceof Sum) // a^(b*x+c) = (a^c)*a^(b*x)
			{
				Sum s = (Sum) p.exp;
				Expression s1 = s; Expression s2 = s;
				for (int i = 0; i < s.args.size(); i++)
				{
					if (SolverUtils.contains(s.args.getKey(i), x)) s2 = s2.sub(s.args.getKey(i).mul(s.args.getVal(i)));
					else s1 = s1.sub(s.args.getKey(i).mul(s.args.getVal(i)));
				}
				Solveable sp = toPoly(p.base.pow(s1), p.base.pow(s1));
				sp = sp.mul(p.base.pow(s2));
				return sp;
			}
			return toPoly(e, e);
		}
		else if (e instanceof PolyFrac)
		{
			PolyFrac p = (PolyFrac) e;
			if (p.var.equals(x)) return p;
			else return toPoly(x, toSum(p.num).mul(toSum(p.denom).inverse()));
		}
		throw new PolyError();
	}

	public static Expression toSum(Poly p)
	{
		Expression x = p.var;
		ArrayList<Expression> v = new ArrayList<Expression>(p.consts);
		if (v.size() == 0) return new Sum(new OrderedMap<Expression, Num>());
		Expression r = v.get(0);
		for (int i = 1; i < v.size(); i++)
			r = r.add(v.get(i).mul(x.pow(new Int(BigInteger.valueOf(i)))));
		return r;
	}
	
	public static int binomial(Poly p) //Returns the larger of the two
	{
		int s = -1;
		int q = -1;
		for (int i = 0; i < p.consts.size(); i++)
		{
			if (!p.consts.get(i).equals(Int.ZERO) && !p.consts.get(i).equals(Float.ZERO))
			{
				if (s != -1) return -1;
				if (q != -1) s = i;
				q = i;
			}
		}
		return s;
	}
	
	public static int uninomial(Poly p)
	{
		int s = -1;
		for (int i = 0; i < p.consts.size(); i++)
		{
			if (!p.consts.get(i).equals(Int.ZERO) && !p.consts.get(i).equals(Float.ZERO))
			{
				if (s != -1) return -1;
				s = i;
			}
		}
		return s;
	}
	
	public static Num factorNum(Expression e)
	{
		if (e instanceof Num) return (Num) e;
		if (e instanceof Sum)
		{
			Sum s = (Sum) e;
			Num n = factorNum(s.args.getKey(0)).mul(factorNum(s.args.getVal(0)));
			for (int i = 1; i < s.args.size(); i++)
			{
				Expression temp = new Gcd(n, (Num) factorNum(s.args.getKey(i)).mul(factorNum(s.args.getVal(i)))).simplify();
				if (!(temp instanceof Num)) throw new PolyError();
				n = (Num) temp;
			}
			return n;
		}
		return Int.ONE;
	}
	
	public static ArrayList<Integer> exps(Poly p)
	{
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i = 0; i < p.consts.size(); i++)
			if (!p.consts.get(i).equals(Int.ZERO) && !p.consts.get(i).equals(Float.ZERO))
				a.add(i);
		return a;
	}
}