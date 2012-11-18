package com.argusdusty.apscalc.types.solve;

import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.utils.PolyUtils;

public class PolyFrac extends Solveable
{
	public Poly num;
	public Poly denom;
	
	public PolyFrac(Poly p1, Poly p2)
	{
		if (p1.var.equals(p2.var))
		{
			this.num = p1; this.denom = p2; this.var = p1.var;
		}
		this.num = p1; this.denom = p2; this.var = p1.var; //TODO?
	}
	
	public PolyFrac(Solveable s1, Solveable s2)
	{
		Poly p1, p2;
		if (s1 instanceof PolyFrac)
		{
			if (s2 instanceof PolyFrac)
			{
				try {p1 = (Poly) ((PolyFrac) s1).num.mul(((PolyFrac) s2).denom);}
				catch (ClassCastException cce) {throw new UnexpectedError("pf1");}
				try {p2 = (Poly) ((PolyFrac) s2).num.mul(((PolyFrac) s1).denom);}
				catch (ClassCastException cce) {throw new UnexpectedError("pf2");}
			}
			p1 = ((PolyFrac) s1).num;
			try {p2 = (Poly) ((Poly) s2).mul(((PolyFrac) s1).denom);}
			catch (ClassCastException cce) {throw new UnexpectedError("pf3");}
		}
		else {p1 = (Poly) s1; p2 = (Poly) s2;}
		if (p1.var.equals(p2.var))
		{
			this.num = p1; this.denom = p2; this.var = p1.var;
		}
		this.num = p1; this.denom = p2; this.var = p1.var; //TODO?
	}
	
	public Expression simplify()
	{
		return new PolyFrac(num, denom);
	}
	
	public Expression derivative(Variable x)
	{
		return PolyUtils.toSum(num).mul(PolyUtils.toSum(denom).inverse()).derivative(x);
	}
	
	public String toString()
	{
		return PolyUtils.toSum(num).mul(PolyUtils.toSum(denom).inverse()).toString();
	}
	
	public Solveable add(Expression e)
	{
		if (e instanceof Poly)
		{
			Poly p = (Poly) e;
			return new PolyFrac(num.add(p.mul(denom)), denom);
		}
		else if (e instanceof PolyFrac) // a/b+c/d = (a*d+b*c)/(b*d)
		{
			PolyFrac pf = (PolyFrac) e;
			return new PolyFrac(num.mul(pf.denom).add(denom.mul(pf.num)), denom.mul(pf.denom));
		}
		else return add(PolyUtils.toPoly(var, e));
	}
	
	public Solveable mul(Expression e)
	{
		return new PolyFrac(num.mul(e), denom);
	}
	
	public Solveable pow(Expression e)
	{
		return num.pow(e).mul(denom.pow(e).inverse());
	}
	
	public boolean equals(Expression e)
	{
		boolean b = e instanceof PolyFrac;
		return b && ((PolyFrac) e).num.equals(num) && ((PolyFrac) e).denom.equals(denom);
	}
	
	public Expression substitute(Variable x, Expression e)
	{
		return num.substitute(x, e).mul(denom.substitute(x, e).inverse());
	}
	
	public Expression negate() {return new PolyFrac(num.negate(), denom);}
	public Solveable inverse() {return new PolyFrac(denom, num);}
	public PolyFrac copy() {return new PolyFrac(num, denom);}
}
