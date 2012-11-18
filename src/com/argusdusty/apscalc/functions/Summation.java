package com.argusdusty.apscalc.functions;

import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.Sum;
import com.argusdusty.apscalc.utils.OrderedMap;
import com.argusdusty.apscalc.utils.SolverUtils;

public class Summation extends AbstractFunction
{
	public Expression arg1;
	public Variable arg2;
	public Expression arg3;
	public Expression arg4;

	public Summation(Expression arg1, Expression arg2, Expression arg3, Expression arg4)
	{
		this.name = "sum";
		this.arg1 = arg1;
		if (!(arg2 instanceof Variable)) throw new Error("Arg 2 of Sum must be a variable");
		this.arg2 = (Variable) arg2;
		//if (!(arg3 instanceof Int)) throw new Error("Arg 3 of Sum must be an integer");
		this.arg3 = arg3;
		//if (!(arg4 instanceof Int)) throw new Error("Arg 4 of Sum must be an integer");
		this.arg4 = arg4;
	}
	
	public Expression simplify()
	{
		if (!SolverUtils.contains(arg1, arg2))
		{
			return arg1.mul(arg4.sub(arg3).add(Int.ONE));
		}
		if (!arg3.equals(Int.ZERO))
		{
			Expression a = new Summation(arg1.copy(), arg2.copy(), Int.ZERO, arg4.copy()).simplify();
			Expression b = new Summation(arg1.copy(), arg2.copy(), Int.ZERO, arg3.add(Int.NEGONE)).simplify();
			return a.sub(b);
		}
		if (arg1 instanceof Sum)
		{
			Expression r = Int.ZERO;
			OrderedMap<Expression, Num> v = ((Sum) arg1).args;
			for (int i = 0; i < v.size(); i++)
			{
				r = r.add(v.getVal(i).mul(new Summation(v.getKey(i), arg2.copy(), arg3.copy(), arg4.copy())));
			}
			return r;
		}
		if (arg1.equals(arg2))
		{
			return arg4.mul(arg4.add(Int.ONE)).mul(Rational.HALF);
		}
		if (arg1 instanceof Prod)
		{
			if (((Prod) arg1).args.size() == 1)
			{
				Expression a = ((Prod) arg1).args.getKey(0);
				Expression b = ((Prod) arg1).args.getVal(0);
				if (a.equals(arg2) && b instanceof Int)
				{
					Int n = (Int) b;
					if (n.equals(Int.TWO))
					{
						return arg4.mul(arg4.add(Int.ONE)).mul(arg4.mul(Int.TWO).add(Int.ONE)).div(new Int(6));
					}
					if (n.equals(new Int(3)))
					{
						return arg4.pow(Int.TWO).mul(arg4.add(Int.ONE).pow(Int.TWO)).div(new Int(4));
					}
					if (n.equals(new Int(4)))
					{
						Expression r = arg4.mul(arg4.add(Int.ONE)).mul(arg4.mul(Int.TWO).add(Int.ONE));
						r = r.mul((new Int(3)).mul(arg4.pow(Int.TWO)).add((new Int(3)).mul(arg4)).add(Int.NEGONE));
						return r.div(new Int(30));
					}
				}
			}
			else
			{
				Expression r = Int.ONE;
				Expression c = Int.ONE;
				OrderedMap<Expression, Num> v = ((Prod) arg1).args;
				for (int i = 0; i < v.size(); i++)
				{
					if (SolverUtils.contains(v.getKey(i), arg2)) r = r.mul(v.getKey(i).pow(v.getVal(i)));
					else c = c.mul(v.getKey(i).pow(v.getVal(i)));
				}
				return c.mul(new Summation(r, arg2.copy(), arg3.copy(), arg4.copy()).simplify());
			}
		}
		if (!(arg4 instanceof Int)) return copy();
		
		Int n = Int.ZERO;
		Expression r = arg1.substitute(arg2, n).simplify();
		while (n.value.compareTo(((Int) arg4).value) == -1)
		{
			n = n.add(Int.ONE);
			r = r.add(arg1.substitute(arg2, n).simplify());
		}
		return r;
	}

	public Expression derivative(Variable x) {throw new UnexpectedError("sfd");}
	
	public String toString()
	{
		return "sum(" + arg1.toString() + ", " + arg2.toString() + ", " + arg3.toString() + ", " + arg4.toString() + ")";
	}
	
	public boolean equals(Expression e)
	{
		if (e instanceof Summation)
		{
			if (!((Summation) e).arg1.equals(arg1)) return false;
			if (!((Summation) e).arg2.equals(arg1)) return false;
			if (!((Summation) e).arg3.equals(arg1)) return false;
			if (!((Summation) e).arg4.equals(arg1)) return false;
			return true;
		}
		return false;
	}
	
	public Expression substitute(Variable x, Expression e)
	{
		return new Summation(arg1.substitute(x, e), arg2.substitute(x, e), 
				arg3.substitute(x, e), arg4.substitute(x, e)).simplify();
	}
	
	public Expression finverse(Expression e) {throw new UnexpectedError("sfi");}
	public Summation copy() {return new Summation(arg1, arg2, arg3, arg4);}
}
