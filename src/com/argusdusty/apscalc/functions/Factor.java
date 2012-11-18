package com.argusdusty.apscalc.functions;

import java.math.BigInteger;

import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.utils.OrderedMap;

public class Factor extends UnivarFunction
{
	public Factor(Expression arg1) {this.arg1 = arg1; this.name = "factor";}
	
	private int in(OrderedMap<Expression, Num> a, Expression e)
	{for (int i = 0; i < a.size(); i++) {if (a.getKey(i).equals(e)) return i;} return -1;}
	
	public Expression finverse(Expression e) {return e;}
	public Expression simplify()
	{
		if (arg1 instanceof Int)
		{
			OrderedMap<Expression, Num> a = new OrderedMap<Expression, Num>(((Int) arg1).factor(BigInteger.ZERO));
			return new Prod(a);
		}
		else if (arg1 instanceof Float) return new Factor(((Float) arg1).exactVal()).simplify();
		else if (arg1 instanceof Rational)
		{
			Rational r = (Rational) arg1;
			OrderedMap<Expression, Num> a1 = new OrderedMap<Expression, Num>(new Int(r.num).factor(BigInteger.ZERO));
			if (a1.size() == 1 && a1.getKey(0).equals(Int.ZERO)) return Int.ZERO;
			
			OrderedMap<Expression, Num> a2 = new OrderedMap<Expression, Num>(new Int(r.denom).factor(BigInteger.ZERO));
			for (int i = 0; i < a2.size(); i++)
			{
				int x = in(a1, a2.getKey(i));
				if (x == -1) a1.add(a2.getKey(i), a2.getVal(i).negate());
				else 
				{
					Num t = a1.getVal(x).add(a2.getVal(i).negate());
					if (!t.equals(Int.ZERO)) a1.setVal(x, t);
				}
			}
			return new Prod(a1);
		}
		return new Factor(arg1);
	}
	
	public Expression derivative(Variable x) {return arg1.derivative(x);}
	public Expression substitute(Variable x, Expression e) {return new Factor(arg1.substitute(x, e));}
	public Factor copy() {return new Factor(arg1);}
}
