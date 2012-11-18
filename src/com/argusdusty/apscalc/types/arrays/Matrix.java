package com.argusdusty.apscalc.types.arrays;

import java.math.BigInteger;
import java.util.ArrayList;

import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.functions.list.Determinant;
import com.argusdusty.apscalc.functions.list.REchelon;
import com.argusdusty.apscalc.functions.list.Transpose;
import com.argusdusty.apscalc.types.EmptyExpression;
import com.argusdusty.apscalc.types.Equation;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.utils.Lambda;
import com.argusdusty.apscalc.utils.SolverUtils;

public class Matrix extends Expression
{
	public ArrayList<ArrayList<Expression>> args = new ArrayList<ArrayList<Expression>>();
	public int m, n;
	
	public Matrix(List lst)
	{
		ArrayList<Expression> a = lst.args;
		if (a.size() == 0) throw new Error("Invalid Matrix");
		if (!(a.get(0) instanceof List)) throw new Error("Invalid Matrix");
		int d = ((List) a.get(0)).args.size();
		if (d == 0) throw new Error("Invalid Matrix");
		this.args.add(((List) a.get(0)).args);
		for (int i = 1; i < a.size(); i++)
		{
			if (!(a.get(i) instanceof List)) throw new Error("Invalid Matrix");
			ArrayList<Expression> b = ((List) a.get(i)).args;
			if (d != b.size()) throw new Error("Invalid Matrix");
			this.args.add(b);
			for (int j = 0; j < b.size(); j++)
			{
				Expression e = b.get(j);
				if (e instanceof List || e instanceof Matrix) throw new Error("Invalid Matrix");
				if (e instanceof Expseq) throw new Error("Invalid Matrix");
			}
		}
		this.m = a.size(); // Number of Rows
		this.n = d; // Number of Columns
	}
	
	public Matrix(ArrayList<ArrayList<Expression>> args)
	{
		if (args.size() == 0) throw new Error("Invalid Matrix");
		int d = args.get(0).size();
		if (d == 0) throw new Error("Invalid Matrix");
		for (int i = 0; i < args.size(); i++)
		{
			ArrayList<Expression> v = new ArrayList<Expression>();
			if (args.get(i).size() != d) throw new Error("Invalid Matrix");
			for (int j = 0; j < args.get(i).size(); j++)
			{
				Expression e = args.get(i).get(j);
				if (e instanceof List) throw new Error("Invalid Matrix");
				if (e instanceof Matrix) throw new Error("Invalid Matrix");
				if (e instanceof Expseq) throw new Error("Invalid Matrix");
				v.add(e.copy());
			}
			this.args.add(v);
		}
		this.m = args.size();
		this.n = d;
	}
	
	public Matrix(int m, int n)
	{
		if (m != n) throw new UnexpectedError("mmm");
		this.m = m;
		this.n = n;
		for (int i = 0; i < m; i++)
		{
			ArrayList<Expression> a = new ArrayList<Expression>();
			for (int j = 0; j < n; j++)
			{
				if (i == j) a.add(Int.ONE);
				else a.add(Int.ZERO);
			}
			args.add(a);
		}
	}
	
	public Expression simplify() {return copy();}
	public String toString() {return "matrix(" + toList().toString() + ")";}
	
	public List toList()
	{
		ArrayList<Expression> a = new ArrayList<Expression>();
		for (int i = 0; i < args.size(); i++) a.add(new List(args.get(i)));
		return new List(a);
	}
	
	public boolean equals(Expression e)
	{
		if (!(e instanceof Matrix)) return false;
		if (((Matrix) e).m != m || ((Matrix) e).n != n);
		ArrayList<ArrayList<Expression>> v = ((Matrix) e).args;
		if (v.size() != args.size()) return false;
		for (int i = 0; i < v.size(); i++)
			for (int j = 0; j < v.get(i).size(); j++)
				if (!v.get(i).get(j).equals(args.get(i).get(j))) return false;
		return true;
	}
	
	public Expression substitute(Variable x, Expression e)
	{
		ArrayList<ArrayList<Expression>> a = new ArrayList<ArrayList<Expression>>();
		ArrayList<Expression> v;
		for (int i = 0; i < args.size(); i ++)
		{
			v = new ArrayList<Expression>();
			for (int j = 0; j < args.get(i).size(); j++) v.add(args.get(i).get(j).substitute(x, e));
			a.add(v);
		}
		return new Matrix(a);
	}
	
	public Matrix copy() {return new Matrix(args);}
	
	public Matrix add(Matrix ma)
	{
		ArrayList<ArrayList<Expression>> result = new ArrayList<ArrayList<Expression>>();
		if (ma.m == m && ma.n == n)
		{
			for (int i = 0; i < args.size(); i++)
			{
				ArrayList<Expression> v = new ArrayList<Expression>();
				for (int j = 0; j < args.get(i).size(); j++) v.add(args.get(i).get(j).add(ma.args.get(i).get(j)));
				result.add(v);
			}
			return new Matrix(result);
		}
		throw new Error("Incompatible dimensions for matrix addition");
	}
	
	public Matrix mul(Matrix ma)
	{
		if (ma.m == n)
		{
			ArrayList<ArrayList<Expression>> r = new ArrayList<ArrayList<Expression>>();
			for (int i = 0; i < m; i++)
			{
				ArrayList<Expression> v = new ArrayList<Expression>();
				for (int j = 0; j < ma.n; j++)
				{
					Expression e = new EmptyExpression();
					for (int a = 0; a < n; a++) e = e.add(args.get(i).get(a).mul(ma.args.get(a).get(j)));
					v.add(e);
				}
				r.add(v);
			}
			return new Matrix(r);
		}
		throw new Error("Incompatible dimensions for matrix multiplication");
	}
	
	public Matrix invert()
	{
		if (m != n) throw new Error("Matrix must be square in order to be inverted");
		ArrayList<ArrayList<Expression>> a = copy().args;
		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < m; j++)
			{
				if (i == j) a.get(i).add(Int.ONE);
				else a.get(i).add(Int.ZERO);
			}
		}
		Matrix r = (Matrix) new REchelon(new Matrix(a)).simplify();
		a = new ArrayList<ArrayList<Expression>>();
		for (int i = 0; i < m; i++)
		{
			ArrayList<Expression> v = new ArrayList<Expression>();
			boolean b = true;
			for (int j = m; j < 2*m; j++)
			{
				if (b && (!r.get(i, j-m).equals(Int.ZERO))) {b = false;}
				v.add(r.get(i, j));
			}
			if (b)
			{
				for (int j = 0; j < m; j++) {if (!r.get(i, j).equals(Int.ZERO)) {b = false; break;}}
				if (b) throw new Error("Matrix is singular");
			}
			a.add(v);
		}
		return new Matrix(a);
	}
	
	private int in(ArrayList<Integer> a, int k)
	{for (int i = 0; i < a.size(); i++) {if (a.get(i) == k) return i;} return -1;}
	
	public ArrayList<Matrix> diagonalize()
	{
		if (m != n) throw new Error("Matrix must be square in order to be diagonalized");
		Expression poly = new Determinant(copy().sub((new Matrix(m, m)).mul(new Lambda()))).simplify();
		Expression Eigenvalues;
		try {Eigenvalues = SolverUtils.solve(new Equation(poly, Int.ZERO), new Lambda());}
		catch (Exception e) {throw new Error("Matrix could not be diagonalized");}
		ArrayList<Expression> eigvals = new ArrayList<Expression>();
		if (Eigenvalues instanceof Expseq) {eigvals = ((Expseq) Eigenvalues).args;}
		else {eigvals.add(Eigenvalues);}
		ArrayList<ArrayList<Expression>> eigvecs = new ArrayList<ArrayList<Expression>>();
		ArrayList<Integer> multiplicities = new ArrayList<Integer>();
		for (int i = 0; i < eigvals.size(); i++)
		{
			Matrix r = add((Matrix) (new Matrix(m, m)).mul(eigvals.get(i).negate()));
			Matrix n = (Matrix) new REchelon(r).simplify();
			int j = 0, k = 0;
			ArrayList<Integer> free = new ArrayList<Integer>();
			ArrayList<Integer> pivots = new ArrayList<Integer>();
			while (j < m)
			{
				if (n.get(k, j).equals(Int.ZERO)) {free.add(j);}
				else {pivots.add(j); k++;}
				j++;
			}
			for (j = 0; j < free.size(); j++)
			{
				int index = free.get(j);
				ArrayList<Expression> v = new ArrayList<Expression>();
				for (k = 0; k < m; k++)
				{
					if (index == k) v.add(Int.ONE);
					else if (in(free, k) != -1) v.add(Int.ZERO);
					else v.add(n.get(in(pivots, k), index).negate());
				}
				eigvecs.add(v);
			}
			multiplicities.add(free.size());
		}
		Matrix U;
		try {U = (Matrix) new Transpose(new Matrix(eigvecs)).simplify();}
		catch (Error e) {throw new UnexpectedError("mdt");}
		Matrix D = new Matrix(m, m);
		int a = 0;
		for (int i = 0; i < eigvals.size(); i++)
		{
			int c = multiplicities.get(i);
			for (int j = 0; j < c; j++) {D.set(a, a, eigvals.get(i)); a++;}
		}
		
		ArrayList<Matrix> r = new ArrayList<Matrix>();
		r.add(U);
		r.add(D);
		try {r.add(U.invert());}
		catch (Error e) {throw new Error("Matrix could not be diagonalized");}
		return r;
	}
	
	public Expression pow(Expression p)
	{
		if (m != n) throw new Error("Matrix must be square in order to be exponentiated");
		if (p instanceof Int)
		{
			if (p.equals(Int.ZERO)) {return new Matrix(m, m);}
			if (((Int) p).signum() < 0) {return invert().pow(p.negate());}
			if (p.equals(Int.ONE)) {return copy();}
			Matrix r = copy();
			Matrix b = copy();
			BigInteger e = ((Int) p).value.subtract(BigInteger.ONE);
			while (e.compareTo(BigInteger.ZERO) != 0)
			{
				if (e.and(BigInteger.ONE).compareTo(BigInteger.ONE) == 0) r = r.mul(b);
				e = e.shiftRight(1);
				b = b.mul(b);
			}
			return r;
		}
		if (p instanceof Variable && ((Variable) p).name.equals("T")) return new Transpose(copy()).simplify();
		ArrayList<Matrix> diag = diagonalize();
		Matrix D = diag.get(1);
		for (int i = 0; i < m; i++) D.set(i, i, D.get(i, i).pow(p));
		return diag.get(0).mul(D).mul(diag.get(2));
	}
	
	public Expression get(int i, int j) {return args.get(i).get(j).copy();}
	public ArrayList<Expression> get(int i)
	{
		ArrayList<Expression> a = args.get(i), b = new ArrayList<Expression>();
		for (int j = 0; j < a.size(); j++) b.add(a.get(j).copy());
		return b;
	}
	
	public void set(int i, int j, Expression x) {ArrayList<Expression> a = args.get(i); a.set(j, x);}
	public void set(int i, ArrayList<Expression> x) {args.set(i, x);}
}
