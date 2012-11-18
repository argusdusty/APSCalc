package com.argusdusty.apscalc.functions.list;

import java.util.ArrayList;

import com.argusdusty.apscalc.errors.DerivativeError;
import com.argusdusty.apscalc.functions.UnivarFunction;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.Matrix;

public class Transpose extends UnivarFunction
{
	public Transpose(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "transpose";
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof Matrix)
		{
			Matrix m = ((Matrix) arg1);
			ArrayList<ArrayList<Expression>> r = new ArrayList<ArrayList<Expression>>();
			for (int i = 0; i < m.n; i++)
			{
				ArrayList<Expression> v = new ArrayList<Expression>();
				for (int j = 0; j < m.m; j++) v.add(m.get(j, i));
				r.add(v);
			}
			return new Matrix(r);
		}
		throw new Error("Argument of function 'transpose' must be a matrix");
		//return copy();
	}
	
	public Expression derivative(Variable x) {throw new DerivativeError(name);}
	public Expression finverse(Expression e) {return new Transpose(e);}
	public Expression substitute(Variable x, Expression e) {return new Transpose(arg1.substitute(x, e)).simplify();}
	public Expression copy() {return new Transpose(arg1);}
}
