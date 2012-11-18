package com.argusdusty.apscalc.functions.list;

import java.util.ArrayList;

import com.argusdusty.apscalc.errors.DerivativeError;
import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.functions.UnivarFunction;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.Matrix;

public class QRDec extends UnivarFunction
{
	public QRDec(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "QRDec";
	}
	
	private Expression inProd(ArrayList<Expression> a, ArrayList<Expression> e)
	{
		if (a.size() != e.size()) throw new UnexpectedError("qrp");
		if (a.size() == 0) return Int.ZERO;
		Expression result = a.get(0).mul(e.get(0));
		for (int i = 1; i < a.size(); i++)
			result = result.add(a.get(i).mul(e.get(i)));
		return result;
	}
	
	private ArrayList<Expression> proj(ArrayList<Expression> a, ArrayList<Expression> e, boolean neg)
	{
		if (neg) return mulV(e, inProd(a, e).div(inProd(e, e)).negate());
		return mulV(e, inProd(a, e).div(inProd(e, e)));
	}
	
	private ArrayList<Expression> normal(ArrayList<Expression> v)
	{
		return mulV(v, inProd(v, v).pow(Rational.HALF.negate()));
	}
	
	private ArrayList<Expression> mulV(ArrayList<Expression> v, Expression e)
	{
		ArrayList<Expression> result = new ArrayList<Expression>(v);
		for (int i = 0; i < result.size(); i++) result.set(i, result.get(i).mul(e));
		return result;
	}
	
	private ArrayList<Expression> addV(ArrayList<Expression> a, ArrayList<Expression> b)
	{
		ArrayList<Expression> result = new ArrayList<Expression>(a);
		for (int i = 0; i < result.size(); i++) result.set(i, result.get(i).add(b.get(i)));
		return result;
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof Matrix)
		{
			Matrix m = (Matrix) arg1;
			if (m.m == 0 || m.n == 0) throw new UnexpectedError("qrs");
			if (m.m != m.n) throw new Error("Invalid matrix size in function '" + name + "'");
			Matrix A = (Matrix) new Transpose(m).simplify();
			ArrayList<ArrayList<Expression>> E = new ArrayList<ArrayList<Expression>>();
			E.add(normal(A.get(0)));
			ArrayList<Expression> e, a;
			for (int i = 1; i < A.m; i++)
			{
				a = new ArrayList<Expression>(A.get(i));
				e = new ArrayList<Expression>(A.get(i));
				for (int j = 0; j < E.size(); j++)
					e = addV(e, proj(a, E.get(j), true));
				E.add(normal(e));
			}
			Matrix Q = (Matrix) new Transpose(new Matrix(E)).simplify();
			ArrayList<ArrayList<Expression>> AR = new ArrayList<ArrayList<Expression>>();
			for (int i = 0; i < E.size(); i++)
			{
				ArrayList<Expression> ea = new ArrayList<Expression>();
				if (A.get(i).size() <= i)
					for (int j = 0; j < A.get(i).size(); j++) ea.add(Int.ZERO);
				else
				{
					for (int j = 0; j < i; j++) ea.add(Int.ZERO);
					for (int j = i; j < A.get(i).size(); j++) ea.add(inProd(E.get(i), A.get(j)));
				}
				AR.add(ea);
			}
			Matrix R = new Matrix(AR);
			return new Prod(Q).append(R);
		}
		return copy();
	}
	
	public Expression derivative(Variable x) {throw new DerivativeError(name);}
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
	public Expression substitute(Variable x, Expression e) {return new QRDec(arg1.substitute(x, e)).simplify();}
	public Expression copy() {return new QRDec(arg1);}
}
