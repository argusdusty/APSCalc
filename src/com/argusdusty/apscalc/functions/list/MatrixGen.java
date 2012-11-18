package com.argusdusty.apscalc.functions.list;

import com.argusdusty.apscalc.errors.DerivativeError;
import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.functions.UnivarFunction;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.List;
import com.argusdusty.apscalc.types.arrays.Matrix;

public class MatrixGen extends UnivarFunction
{
	public MatrixGen(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "matrix";
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof List) return new Matrix(((List) arg1));
		//throw new Error("Invalid matrix");
		return copy();
	}
	
	public Expression derivative(Variable x) {throw new DerivativeError(name);}
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
	public Expression substitute(Variable x, Expression e) {return new MatrixGen(arg1.substitute(x, e)).simplify();}
	public Expression copy() {return new MatrixGen(arg1);}
}
