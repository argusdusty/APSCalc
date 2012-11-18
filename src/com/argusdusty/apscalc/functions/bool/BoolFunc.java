package com.argusdusty.apscalc.functions.bool;

import java.math.BigInteger;

import com.argusdusty.apscalc.errors.DerivativeError;
import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.functions.AbstractFunction;
import com.argusdusty.apscalc.types.Bool;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Variable;

public class BoolFunc extends AbstractFunction
{
	Expression x;
	Expression y;
	int op;
	/*
	 * 1 -> AND
	 * 2 -> OR
	 * 3 -> XOR
	 * -1 -> NOT
	 */
	
	public BoolFunc(Expression x, int op) // NOT
	{
		this.x = x;
		this.y = x.copy(); // Not used
		if (op != -1) throw new UnexpectedError("bin"); // 1-arg BoolFunc must be a NOT operator
		this.op = -1;
	}

	public BoolFunc(Expression x, Expression y, int op)
	{
		this.x = x;
		this.y = y;
		if (op == 0 || op > 3 || op < -3) throw new UnexpectedError("bio");
		this.op = op;
	}

	public Expression derivative(Variable x) {throw new DerivativeError();}
	public Expression finverse(Expression e) {throw new FuncInvError();}
	
	public Expression simplify()
	{
		if (!((x instanceof Int || x instanceof Bool) && (y instanceof Int || y instanceof Bool))) return copy();
		if (x instanceof Bool && y instanceof Int) x = ((Bool) x).toInt();
		if (y instanceof Bool && x instanceof Int) y = ((Bool) y).toInt();
		if (x instanceof Bool && y instanceof Bool)
		{
			boolean a = ((Bool) x).val, b = ((Bool) y).val;
			if (op == 1) return new Bool(a && b);
			if (op == 2) return new Bool(a || b);
			if (op == -1) return new Bool(!a);
			return new Bool(!(a ^ b));
		}
		if (!(x instanceof Int && y instanceof Int)) throw new UnexpectedError("bsi");
		BigInteger a = ((Int) x).value, b = ((Int) y).value;
		if (op == 1) return new Int(a.and(b));
		if (op == 2) return new Int(a.or(b));
		if (op == -1) return new Int(a.not());
		return new Int(a.xor(b));
	}
	
	public String toString()
	{
		if (op == 1) return x.toString() + "&" + y.toString();
		if (op == 2) return x.toString() + "|" + y.toString();
		if (op == -1) return "~" + x.toString();
		return "xor(" + x.toString() + ", " + y.toString() + ")";
	}

	@Override
	public boolean equals(Expression e)
	{
		if (e instanceof BoolFunc && ((BoolFunc) e).op == op)
			if (((BoolFunc) e).x.equals(x) && ((BoolFunc) e).y.equals(y))
				return true;
		return false;
	}

	@Override
	public Expression substitute(Variable x, Expression e)
	{
		return new BoolFunc(x.substitute(x, e), y.substitute(x, e), op).simplify();
	}

	@Override
	public Expression copy() {return new BoolFunc(x.copy(), y.copy(), op);}

}
