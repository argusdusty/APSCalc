package com.argusdusty.apscalc.utils;

import java.util.ArrayList;

import com.argusdusty.apscalc.errors.IntegrateError;
import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.functions.AbstractFunction;
import com.argusdusty.apscalc.functions.Ln;
import com.argusdusty.apscalc.types.Complex;
import com.argusdusty.apscalc.types.Constant;
import com.argusdusty.apscalc.types.EmptyExpression;
import com.argusdusty.apscalc.types.Equation;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Power;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Sum;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.Expseq;
import com.argusdusty.apscalc.types.arrays.List;
import com.argusdusty.apscalc.types.arrays.Text;

public class CalculusUtils
{
	public static Expression derivative(Expression e, Variable x)
	{
		if (e instanceof Text) return Int.ZERO;
		if (e instanceof Constant) return Int.ZERO;
		if (e instanceof Variable) {if (e.equals(x)) return Int.ONE; return Int.ZERO;}
		if (e instanceof EmptyExpression) return new EmptyExpression();
		if (e instanceof AbstractFunction) return ((AbstractFunction) e).derivative(x);
		if (e instanceof Complex) // d/dx(f(x)+g(x)*i) = f'(x)+g'(x)*i
		{
			Expression real = ((Complex) e).real, imag = ((Complex) e).imag;
			return new Complex(derivative(real, x), derivative(imag, x));
		}
		if (e instanceof Equation) // d/dx(f(x)=g(x)) -> f'(x)=g'(x)
		{
			Expression left = ((Equation) e).left, right = ((Equation) e).right;
			return new Equation(derivative(left, x), derivative(right, x));
		}
		if (e instanceof Expseq) // d/dx({f(x), g(x)}) -> {f'(x), g'(x)}
		{
			Expseq ex = (Expseq) e;
			ArrayList<Expression> v = ex.args;
			for (int i = 0; i < v.size(); i++) {v.set(i, derivative(v.get(i), x));}
			return new Expseq(v);
		}
		if (e instanceof List) // d/dx([f(x), g(x)]) -> [f'(x), g'(x)]
		{
			List ex = (List) e;
			ArrayList<Expression> v = ex.args;
			for (int i = 0; i < v.size(); i++) {v.set(i, derivative(v.get(i), x));}
			return new List(v);
		}
		if (e instanceof Power) // d/dx(f(x)^g(x)) = f(x)^(g(x)-1)*(g(x)*f'(x)+f(x)*ln(f(x))*g'(x))
		{
			Expression base = ((Power) e).base, exp = ((Power) e).exp;
			Expression a = base.pow(exp.add(Int.NEGONE));
			Expression b = exp.mul(derivative(base, x));
			Expression c = base.mul(new Ln(base).simplify()).mul(derivative(exp, x));
			return a.mul(b.add(c));
		}
		if (e instanceof Prod) // d/dx(f(x)^a*g(x)^b) = f(x)^(a-1)*g(x)^(b-1)*(a*f'(x)*g(x)+b*f(x)*g'(x))
		{
			OrderedMap<Expression, Num> args = new OrderedMap<Expression, Num>(((Prod) e).args);
			Expression r1 = Int.ZERO, r2 = Int.ONE;
			for (int i = 0; i < args.size(); i++)
			{
				r2 = args.getVal(i);
				for (int j = 0; j < args.size(); j++)
				{
					if (j == i) r2 = r2.mul(derivative(args.getKey(j), x));
					else r2 = r2.mul(args.getKey(j));
				}
				r1 = r1.add(r2);
			}
			r2 = Int.ONE;
			for (int i = 0; i < args.size(); i += 2) {r2 = r2.mul(args.getKey(i).pow(args.getVal(i).add(Int.NEGONE)));}
			r2 = r2.mul(r1);
			return r2;
		}
		if (e instanceof Sum) // d/dx(f(x)*a+g(x)*b) = f'(x)*a+g'(x)*b
		{
			OrderedMap<Expression, Num> args = new OrderedMap<Expression, Num>(((Sum) e).args);
			Expression r = Int.ZERO;
			for (int i = 0; i < args.size(); i++) {r = r.add(derivative(args.getKey(i), x).mul(args.getVal(i)));}
			return r;
		}
		throw new UnexpectedError("cud");
	}
	
	@SuppressWarnings("unused")
	private static boolean diffConv(Expression e, Variable x) //returns true if the function goes to some constant multiple of itself (including 0)
	{
		if (!SolverUtils.contains(e, x)) return true;
		if (e instanceof Constant) return true;
		if (e instanceof Variable) return true;
		if (e instanceof EmptyExpression) return true;
		if (e instanceof Equation) return diffConv(((Equation) e).left, x) || diffConv(((Equation) e).right, x);
		if (e instanceof Expseq)
		{
			ArrayList<Expression> v = ((Expseq) e).args;
			for (int i = 0; i < v.size(); i += 2) {if (!diffConv(v.get(i), x)) return false;}
			return true;
		}
		if (e instanceof List)
		{
			ArrayList<Expression> v = ((List) e).args;
			for (int i = 0; i < v.size(); i += 2) {if (!diffConv(v.get(i), x)) return false;}
			return true;
		}
		if (e instanceof Sum)
		{
			OrderedMap<Expression, Num> v = ((Sum) e).args;
			for (int i = 0; i < v.size(); i++)
			{
				if (!diffConv(v.getKey(i), x)) return false;
			}
			if (v.size() == 1) return true;
		}
		if (e instanceof Prod)
		{
			OrderedMap<Expression, Num> v = ((Prod) e).args;
			for (int i = 0; i < v.size(); i++)
			{
				if (!diffConv(v.getKey(i), x)) return false;
				if (!(v.getVal(i+1) instanceof Int)) return false;
				if (((Int) v.getVal(i+1)).signum() != 1) return false;
			}
			if (v.size() == 1) return true;
		}
		if (e instanceof Power)
		{
			Expression f = ((Power) e).base;
			Expression g = ((Power) e).exp;
			if (f instanceof Constant)
			{
				if ((g instanceof Variable) || (g instanceof Constant) || (!SolverUtils.contains(g, x))) return true;
			}
			if ((!SolverUtils.contains(f, x)) && (!SolverUtils.contains(g, x))) return true;
		}
		Expression a = derivative(e, x);
		if ((a instanceof Constant) || (!SolverUtils.contains(a.div(e), x))) return true;
		return false;
	}
	
	public static Expression integrate(Expression e, Variable x) //TODO: Needs work.
	{
		if (!SolverUtils.contains(e, x)) return e.mul(x);
		if (e instanceof Constant) return e.mul(x);
		else if (e instanceof Text) throw new IntegrateError();
		else if (e instanceof Variable) {if (e.equals(x)) return Rational.HALF.mul(x.pow(Int.TWO)); return e.mul(x);}
		else if (e instanceof EmptyExpression) return new EmptyExpression();
		else if (e instanceof Complex) // ∫(f(x)+g(x)*i)dx = F(x)+G(x)*i
		{
			Expression real = ((Complex) e).real, imag = ((Complex) e).imag;
			return new Complex(integrate(real, x), integrate(imag, x));
		}
		else if (e instanceof Equation) // ∫(f(x)=g(x))dx -> F(x)=G(x)
		{
			Expression left = ((Equation) e).left, right = ((Equation) e).right;
			return new Equation(integrate(left, x), integrate(right, x));
		}
		else if (e instanceof Expseq) // ∫({f(x), g(x)})dx -> {F(x), G(x)}
		{
			Expseq ex = (Expseq) e;
			ArrayList<Expression> v = ex.args;
			for (int i = 0; i < v.size(); i++) {v.set(i, integrate(v.get(i), x));}
			return new Expseq(v);
		}
		else if (e instanceof List) // ∫([f(x), g(x)])dx -> [F(x), G(x)]
		{
			List ex = (List) e;
			ArrayList<Expression> v = ex.args;
			for (int i = 0; i < v.size(); i++) {v.set(i, integrate(v.get(i), x));}
			return new List(v);
		}
		else if (e instanceof Power)
		{
			Expression base = ((Power) e).base, exp = ((Power) e).exp;
			if (!SolverUtils.contains(base, x))
			{
				if (exp.equals(x)) return e.div(new Ln(base).simplify()); // ∫(a^x)dx = a^x/ln(a)
				else if (exp instanceof Sum && ((Sum) exp).args.size() == 1) // ∫(a^(b*x))dx = a^(b*x)/(b*ln(a))
				{
					Expression b = ((Sum) exp).args.getVal(0);
					return e.div(b.mul(new Ln(base).simplify()));
				}
				else if (exp instanceof Sum) // ∫(a^(x+b))dx = a^(x+b)/ln(a) - TODO
				{
					
				}
			}
			else if (!SolverUtils.contains(exp, x)) // ∫(x^a)dx = x^(a+1)/(a+1)
			{
				if (base.equals(x)) return e.mul(x).div(exp.add(Int.ONE));
				//TODO: ∫((a*x+b)^c)dx = (a*x+b)^(c+1)/(a*c+a)
			}
			else throw new IntegrateError();
		}
		else if (e instanceof Prod && ((Prod) e).args.size() == 2) // ∫(x^n)dx = x^(n+1)/(n+1)
		{
			Expression a = ((Prod) e).args.getKey(0);
			if (a.equals(x)) return e.mul(x).div(((Prod) e).args.getVal(0).add(Int.ONE));
		}
		else if (e instanceof Prod) throw new IntegrateError(); //TODO: ∫(f(x)*y^b)dx = y^b*F(x)
		else if (e instanceof Sum) // ∫(f(x)*a+g(x)*b)dx = F(x)*a+G(x)*b
		{
			OrderedMap<Expression, Num> args = new OrderedMap<Expression, Num>(((Sum) e).args);
			Expression r = Int.ZERO;
			for (int i = 0; i < args.size(); i++) {r = r.add(integrate(args.getKey(i), x).mul(args.getVal(i)));}
			return r;
		}
		throw new UnexpectedError("cui");
	}
}
