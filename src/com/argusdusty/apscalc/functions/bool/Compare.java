package com.argusdusty.apscalc.functions.bool;

import com.argusdusty.apscalc.errors.DerivativeError;
import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.functions.AbstractFunction;
import com.argusdusty.apscalc.types.Bool;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Variable;

public class Compare extends AbstractFunction
{
	public Expression x;
	public Expression y;
	public int op;
	
	public Compare(Expression x, Expression y, int op)
	{
		this.x = x;
		this.y = y;
		if (op > 3 || op < -2) throw new UnexpectedError("cio");
		this.op = op;
	}
	
	public Expression derivative(Variable x) {throw new DerivativeError();}
	public Expression finverse(Expression e) {throw new FuncInvError();}
	
	public String toString()
	{
		if (op == 0) return x.toString() + "==" + y.toString();
		else if (op == -1) return x.toString() + "<=" + y.toString();
		else if (op == -2) return x.toString() + "<" + y.toString();
		else if (op == 1) return x.toString() + ">=" + y.toString();
		else if (op == 2) return x.toString() + ">" + y.toString();
		return x.toString() + "<>" + y.toString();
	}
	
	public boolean equals(Expression e)
	{
		if (e instanceof Compare && ((Compare) e).op == op)
			if (((Compare) e).x.equals(x) && ((Compare) e).y.equals(y))
				return true;
		return false;
	}
	
	public Expression simplify()
	{
		Expression t = x.sub(y);
		if (!(t instanceof Num)) return copy();
		Num n = (Num) t;
		if (op == 0) return new Bool(n.signum() == 0);
		else if (op == -1) return new Bool(n.signum() <= 0);
		else if (op == -2) return new Bool(n.signum() < 0);
		else if (op == 1) return new Bool(n.signum() >= 0);
		else if (op == 2) return new Bool(n.signum() > 0);
		return new Bool(n.signum() != 0);
	}
	
	public Expression substitute(Variable x, Expression e)
	{
		return new Compare(x.substitute(x, e), y.substitute(x, e), op).simplify();
	}
	
	public Expression copy() {return new Compare(x.copy(), y.copy(), op);}
}
