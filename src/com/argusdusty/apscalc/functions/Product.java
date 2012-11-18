package com.argusdusty.apscalc.functions;

import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Power;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.utils.OrderedMap;
import com.argusdusty.apscalc.utils.SolverUtils;

public class Product extends AbstractFunction
{
	public Expression arg1;
	public Variable arg2;
	public Expression arg3;
	public Expression arg4;

	public Product(Expression arg1, Expression arg2, Expression arg3, Expression arg4)
	{
		this.name = "prod";
		this.arg1 = arg1;
		if (!(arg2 instanceof Variable)) throw new Error("Arg 2 of Prod must be a variable");
		this.arg2 = (Variable) arg2;
		//if (!(arg3 instanceof Int)) throw new Error("Arg 3 of Prod must be an integer");
		this.arg3 = arg3;
		//if (!(arg4 instanceof Int)) throw new Error("Arg 4 of Prod must be an integer");
		this.arg4 = arg4;
	}
	
	public Expression simplify()
	{
		if (!SolverUtils.contains(arg1, arg2))
		{
			return arg1.pow(arg4.sub(arg3).add(Int.ONE));
		}
		if (arg1 instanceof Prod) // Product(f(x)^a*g(x)^b, x, c, d) = Product(f(x), x, c, d)^a*Product(g(x), x, c, d)^b
		{
			Expression r = Int.ZERO;
			OrderedMap<Expression, Num> v = ((Prod) arg1).args;
			for (int i = 0; i < v.size(); i++)
			{
				r = r.mul(new Product(v.getKey(i), arg2.copy(), arg3.copy(), arg4.copy()).simplify().pow(v.getVal(i)));
			}
			return r;
		}
		if (arg1.equals(arg2))
		{
			if (arg3 instanceof Int && ((Int) arg3).signum() != 1) return Int.ZERO;
			return new Fact(arg4).simplify().div(new Fact(arg3.sub(Int.ONE)).simplify());
		}
		if (arg1 instanceof Power)
		{
			Expression base = ((Power) arg1).base;
			Expression exp = ((Power) arg1).exp;
			if (!SolverUtils.contains(base, arg2)) // Product(c^f(x), x, a, b) = c^Summation(f(x), x, a, b)
			{
				return base.pow(new Summation(exp, arg2, arg3, arg4).simplify());
			}
			if (!SolverUtils.contains(base, arg2)) // Product(f(x)^c, x, a, b) = Product(f(x), x, a, b)^c
			{
				return new Product(exp, arg2, arg3, arg4).simplify().pow(base);
			}
		}
		if (!(arg4 instanceof Int)) return copy();
		
		Int n = Int.ZERO;
		Expression r = arg1.substitute(arg2, n).simplify();
		while (n.value.compareTo(((Int) arg4).value) == -1)
		{
			n = n.add(Int.ONE);
			r = r.mul(arg1.substitute(arg2, n).simplify());
		}
		return r;
	}
	
	public Expression derivative(Variable x) {throw new UnexpectedError("pfd");}
	
	public String toString()
	{
		return "prod(" + arg1.toString() + ", " + arg2.toString() + ", " + arg3.toString() + ", " + arg4.toString() + ")";
	}
	
	public boolean equals(Expression e)
	{
		if (e instanceof Product)
		{
			if (!((Product) e).arg1.equals(arg1)) return false;
			if (!((Product) e).arg2.equals(arg1)) return false;
			if (!((Product) e).arg3.equals(arg1)) return false;
			if (!((Product) e).arg4.equals(arg1)) return false;
			return true;
		}
		return false;
	}
	
	public Expression substitute(Variable x, Expression e)
	{
		return new Product(arg1.substitute(x, e), arg2.substitute(x, e), 
				arg3.substitute(x, e), arg4.substitute(x, e)).simplify();
	}
	
	public Expression finverse(Expression e) {throw new UnexpectedError("pfi");}
	
	public Product copy() {return new Product(arg1, arg2, arg3, arg4);}
}
