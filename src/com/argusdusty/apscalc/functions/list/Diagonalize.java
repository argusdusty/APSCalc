package com.argusdusty.apscalc.functions.list;

import java.util.ArrayList;

import com.argusdusty.apscalc.errors.DerivativeError;
import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.functions.UnivarFunction;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.Matrix;
import com.argusdusty.apscalc.utils.OrderedMap;

public class Diagonalize extends UnivarFunction
{
	public Diagonalize(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "diagonalize";
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof Matrix)
		{
			ArrayList<Matrix> r = ((Matrix) arg1).diagonalize();
			OrderedMap<Expression, Num> v = new OrderedMap<Expression, Num>();
			for (int i = 0; i < r.size(); i++) {v.add(r.get(i), Int.ONE);}
			return new Prod(v);
		}
		return copy();
	}
	
	public Expression derivative(Variable x) {throw new DerivativeError(name);}
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
	public Expression substitute(Variable x, Expression e) {return new Diagonalize(arg1.substitute(x, e)).simplify();}
	public Expression copy() {return new Diagonalize(arg1);}
}
