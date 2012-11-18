package com.argusdusty.apscalc.utils;

import java.math.BigInteger;
import java.util.ArrayList;

import com.argusdusty.apscalc.APSCalc;
import com.argusdusty.apscalc.functions.Ln;
import com.argusdusty.apscalc.functions.list.Transpose;
import com.argusdusty.apscalc.types.Bool;
import com.argusdusty.apscalc.types.Complex;
import com.argusdusty.apscalc.types.Constant;
import com.argusdusty.apscalc.types.EmptyExpression;
import com.argusdusty.apscalc.types.Equation;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Power;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Sum;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.Expseq;
import com.argusdusty.apscalc.types.arrays.List;
import com.argusdusty.apscalc.types.arrays.Matrix;
import com.argusdusty.apscalc.types.arrays.Text;
import com.argusdusty.apscalc.types.mathconsts.Indeterminate;
import com.argusdusty.apscalc.types.mathconsts.Inf;
import com.argusdusty.apscalc.types.mathconsts.NegInf;
import com.argusdusty.apscalc.types.mathconsts.Real;
import com.argusdusty.apscalc.types.mathconsts.Unique;

public class MathUtils
{
	public static Expression add(Expression a, Expression b)
	{
		if (APSCalc.debug) System.out.println("Add: " + a + " " + b);
		if (a instanceof EmptyExpression) return b;
		if (b instanceof EmptyExpression) return a;
		if (a instanceof Expseq)
		{
			ArrayList<Expression> args = new ArrayList<Expression>(((Expseq) a).args);
			ArrayList<Expression> r = new ArrayList<Expression>();
			if (b instanceof Expseq)
			{
				ArrayList<Expression> v = new ArrayList<Expression>(((Expseq) b).args);
				for (int i = 0; i < args.size(); i++)
					for (int j = 0; j < v.size(); j++) r.add(args.get(i).add(v.get(j)));
				return new Expseq(r).simplify();
			}
			for (int i = 0; i < args.size(); i++) r.add(args.get(i).add(b));
			return new Expseq(r).simplify();
		}
		if (b instanceof Expseq)
		{
			ArrayList<Expression> args = new ArrayList<Expression>(((Expseq) b).args);
			ArrayList<Expression> r = new ArrayList<Expression>();
			for (int i = 0; i < args.size(); i++) r.add(b.add(args.get(i)));
			return new Expseq(r).simplify();
		}
		if (a instanceof List)
		{
			ArrayList<Expression> r = new ArrayList<Expression>();
			ArrayList<Expression> args = ((List) a).args;
			if (b instanceof List)
			{
				args.addAll(((List) b).args);
				return new List(args);
			}
			for (int i = 0; i < args.size(); i++) r.add(args.get(i).add(b));
			return new List(r);
		}
		if (b instanceof List)
		{
			ArrayList<Expression> r = new ArrayList<Expression>();
			ArrayList<Expression> args = ((List) b).args;
			for (int i = 0; i < args.size(); i++) r.add(a.add(args.get(i)));
			return new List(r);
		}
		if (a instanceof Matrix)
		{
			if (b instanceof Matrix) return ((Matrix) a).add((Matrix) b);
			ArrayList<ArrayList<Expression>> r = new ArrayList<ArrayList<Expression>>();
			ArrayList<ArrayList<Expression>> ma = ((Matrix) a).args;
			for (int i = 0; i < ma.size(); i++)
			{
				ArrayList<Expression> v = new ArrayList<Expression>();
				for (int j = 0; j < ma.get(i).size(); j++) v.add(ma.get(i).get(j).add(b));
				r.add(v);
			}
			return new Matrix(r);
		}
		if (b instanceof Matrix)
		{
			ArrayList<ArrayList<Expression>> r = new ArrayList<ArrayList<Expression>>();
			ArrayList<ArrayList<Expression>> mb = ((Matrix) b).args;
			for (int i = 0; i < mb.size(); i++)
			{
				ArrayList<Expression> v = new ArrayList<Expression>();
				for (int j = 0; j < mb.get(i).size(); j++) v.add(a.add(mb.get(i).get(j)));
				r.add(v);
			}
			return new Matrix(r);
		}
		if (a.equals(b)) return a.mul(Int.TWO);
		if (a instanceof Text)
		{
			if (b instanceof Text) return new Text(((Text) a).text + ((Text) b).text);
			return new Text(((Text) a).text + b.toString());
		}
		if (b instanceof Text) return new Text(a.toString() + ((Text) b).text);
		if (a instanceof Bool) return add(((Bool) a).toInt(), b);
		if (b instanceof Bool) return add(b, ((Bool) a).toInt());
		if (a instanceof Unique)
		{
			if (a instanceof Indeterminate || b instanceof Indeterminate) return new Indeterminate();
			if (b instanceof Unique && !a.equals(b)) return new Indeterminate();
			return a.copy();
		}
		if (b instanceof Unique) return add(b, a);
		if (b.equals(Int.ZERO) || b.equals(Float.ZERO)) return a.copy();
		if (a.equals(Int.ZERO) || a.equals(Float.ZERO)) return b.copy();
		if (a instanceof Num && b instanceof Num) return ((Num) a).add((Num) b);
		if (a instanceof Complex)
		{
			Expression real = ((Complex) a).real, imag = ((Complex) a).imag;
			if (b instanceof Complex)
				return new Complex(add(real, (((Complex) b).real)), add(imag, (((Complex) b).imag)));
			return new Complex(add(real, b), imag);
		}
		if (b instanceof Complex) return add(b, a);
		if (a instanceof EmptyExpression) return b.copy();
		if (b instanceof EmptyExpression) return a.copy();
		if (a instanceof Equation || b instanceof Equation) throw new Error("Cannot add to an equation");
		if (a instanceof Sum) return ((Sum) a).append(b);
		if (b instanceof Sum) return add(b, a);
		return new Sum(a).append(b);
	}
	
	public static Expression mul(Expression a, Expression b)
	{
		if (APSCalc.debug) System.out.println("Mul: " + a + " " + b);
		if (a instanceof EmptyExpression) return b.copy();
		if (b instanceof EmptyExpression) return a.copy();
		if (a instanceof Expseq)
		{
			ArrayList<Expression> args = new ArrayList<Expression>(((Expseq) a).args);
			ArrayList<Expression> r = new ArrayList<Expression>();
			if (b instanceof Expseq)
			{
				ArrayList<Expression> v = new ArrayList<Expression>(((Expseq) b).args);
				for (int i = 0; i < args.size(); i++)
					for (int j = 0; j < v.size(); j++) r.add(args.get(i).mul(v.get(j)));
				return new Expseq(r).simplify();
			}
			for (int i = 0; i < args.size(); i++) r.add(args.get(i).mul(b));
			return new Expseq(r).simplify();
		}
		if (b instanceof Expseq)
		{
			ArrayList<Expression> args = new ArrayList<Expression>(((Expseq) b).args);
			ArrayList<Expression> r = new ArrayList<Expression>();
			for (int i = 0; i < args.size(); i++) r.add(b.mul(args.get(i)));
			return new Expseq(r).simplify();
		}
		if (a instanceof List)
		{
			ArrayList<Expression> r = new ArrayList<Expression>();
			ArrayList<Expression> args = ((List) a).args;
			for (int i = 0; i < args.size(); i++) r.add(args.get(i).mul(b));
			return new List(r);
		}
		if (b instanceof List)
		{
			ArrayList<Expression> r = new ArrayList<Expression>();
			ArrayList<Expression> args = ((List) b).args;
			for (int i = 0; i < args.size(); i++) r.add(a.mul(args.get(i)));
			return new List(r);
		}
		if (a instanceof Matrix)
		{
			if (b instanceof Matrix) return ((Matrix) a).mul((Matrix) b);
			ArrayList<ArrayList<Expression>> r = new ArrayList<ArrayList<Expression>>();
			ArrayList<ArrayList<Expression>> ma = ((Matrix) a).args;
			for (int i = 0; i < ma.size(); i++)
			{
				ArrayList<Expression> v = new ArrayList<Expression>();
				for (int j = 0; j < ma.get(i).size(); j++) v.add(ma.get(i).get(j).mul(b));
				r.add(v);
			}
			return new Matrix(r);
		}
		if (b instanceof Matrix)
		{
			ArrayList<ArrayList<Expression>> r = new ArrayList<ArrayList<Expression>>();
			ArrayList<ArrayList<Expression>> mb = ((Matrix) b).args;
			for (int i = 0; i < mb.size(); i++)
			{
				ArrayList<Expression> v = new ArrayList<Expression>();
				for (int j = 0; j < mb.get(i).size(); j++) v.add(a.mul(mb.get(i).get(j)));
				r.add(v);
			}
			return new Matrix(r);
		}
		if (a instanceof Bool) return mul(((Bool) a).toInt(), b);
		if (b instanceof Bool) return mul(a, ((Bool) b).toInt());
		if (a instanceof Text)
		{
			if (b instanceof Int && ((Int) b).signum() != -1)
			{
				if (((Int) b).signum() == 0) return new Text("");
				String s = ((Text) a).text;
				int e = ((Int) b).value.intValue() - 1;
				String t = s;
				while (e != 0)
				{
					if ((e & 1) == 1) s += t;
					e = e >> 1;
					t += t;
				}
				return new Text(s);
			}
			else throw new Error("Multiplicand of a string must be a positive integer");
		}
		if (b instanceof Text) return mul(b, a);
		if (a instanceof Unique)
		{
			if (a instanceof Indeterminate) return a.copy();
			if (b instanceof Constant)
			{
				int sign = ((Constant) a).signum()*((Constant) b).signum();
				if (sign == 0) return new Indeterminate();
				if (sign == 1) return new Inf();
				return new NegInf();
			}
			return a.copy();
		}
		if (b instanceof Unique) return mul(b, a);
		if (b.equals(Int.ONE) || b.equals(Float.ONE)) return a.copy();
		if (a.equals(Int.ONE) || a.equals(Float.ONE)) return b.copy();
		if (b.equals(Int.ZERO) || b.equals(Float.ZERO)) return b;
		if (a.equals(Int.ZERO) || a.equals(Float.ZERO)) return a;
		if (a instanceof Num && b instanceof Num) return ((Num) a).mul((Num) b);
		if (a instanceof Float && b instanceof Real) return ((Float) a).mul(((Real) b).decForm());
		if (a instanceof Real && b instanceof Float) return ((Real) a).decForm().mul((Float) b);
		if (a instanceof Num) return mul(b, a);
		if (a instanceof Complex)
		{
			if (b instanceof Complex) return ((Complex) a).mul((Complex) b);
			Expression real = ((Complex) a).real, imag = ((Complex) a).imag;
			return new Complex(mul(real, b), mul(imag, b));
		}
		if (b instanceof Complex) return mul(b, a);
		if (a instanceof Equation || b instanceof Equation) throw new Error("Cannot add to an equation");
		if (a instanceof Sum)
		{
			OrderedMap<Expression, Num> v = new OrderedMap<Expression, Num>(((Sum) a).args);
			if (b instanceof Num)
			{
				if (v.size() == 0) return b.copy();
				if (v.size() == 1) return new Sum(v.getKey(0), v.getVal(0).mul((Num) b));
				Expression r = new Sum(v.getKey(0), v.getVal(0).mul((Num) b));
				for (int i = 1; i < v.size(); i++)
				{
					r = r.add(v.getKey(i).mul(v.getVal(i).mul((Num) b)));
				}
				return r;
			}
			if (((Sum) a).args.size() == 1)
			{
				Sum sa = (Sum) a;
				Expression r = mul(sa.args.getKey(0), b);
				return r.mul(sa.args.getVal(0));
			}
			if (b instanceof Sum)
			{
				OrderedMap<Expression, Num> sa = ((Sum) a).args, sb = ((Sum) b).args;
				Expression result = new EmptyExpression();
				for (int i = 0; i < sa.size(); i++)
				{
					for (int j = 0; j < sb.size(); j++)
					{
						result = result.add(sa.getKey(i).mul(sb.getKey(j)).mul(sa.getVal(i).mul(sb.getVal(j))));
					}
				}
				return result;
			}
			else
			{
				Expression result = new EmptyExpression();
				for (int i = 0; i < v.size(); i++)
					result = result.add(v.getKey(i).mul(b).mul(v.getVal(i)));
				return result;
			}
		}
		else if (b instanceof Sum) {return mul(b, a);}
		if (b instanceof Num) return new Sum(a.copy(), (Num) b.copy());
		if (a instanceof Prod) return ((Prod) a).append(b);
		if (b instanceof Prod) return ((Prod) b).append(a);
		if (a instanceof Power)
		{
			if (b instanceof Power) // a^b*c^d = a^(b+ln(b)/ln(a)*d)
			{
				Expression base = ((Power) a).base, exp = ((Power) a).exp; Power p = (Power) b;
				if (p.base.equals(base)) return new Power(base, add(exp, p.exp)).simplify();
				Expression r = mul(new Ln(p.base).simplify(), new Ln(base).simplify().inverse());
				if (r instanceof Constant) return new Power(base, add(exp, mul(r, p.exp)));
			}
			if (b.equals(((Power) a).base)) // a*a^b=a^(b+1)
			{
				return new Power(((Power) a).base, ((Power) a).exp.add(Int.ONE));
			}
		}
		else if (b instanceof Power) return mul(b, a);
		if (a.equals(b)) return a.pow(Int.TWO);
		return new Prod(a).append(b);
	}
	
	public static Expression pow(Expression a, Expression b)
	{
		if (APSCalc.debug) System.out.println("Pow: " + a + " " + b);
		if (a instanceof EmptyExpression) return b;
		if (b instanceof EmptyExpression) return a;
		if (a instanceof Expseq)
		{
			ArrayList<Expression> args = new ArrayList<Expression>(((Expseq) a).args);
			ArrayList<Expression> r = new ArrayList<Expression>();
			if (b instanceof Expseq)
			{
				ArrayList<Expression> v = new ArrayList<Expression>(((Expseq) b).args);
				for (int i = 0; i < args.size(); i++)
					for (int j = 0; j < v.size(); j++) r.add(args.get(i).pow(v.get(j)));
				return new Expseq(r).simplify();
			}
			for (int i = 0; i < args.size(); i++) r.add(args.get(i).pow(b));
			return new Expseq(r).simplify();
		}
		if (b instanceof Expseq)
		{
			ArrayList<Expression> args = new ArrayList<Expression>(((Expseq) b).args);
			ArrayList<Expression> r = new ArrayList<Expression>();
			for (int i = 0; i < args.size(); i++) r.add(b.pow(args.get(i)));
			return new Expseq(r).simplify();
		}
		if ((a instanceof List) || (b instanceof List)) throw new Error("Cannot take power of a list");
		if ((a instanceof Text) || (b instanceof Text)) throw new Error("Cannot take power of a string");
		if (a instanceof Matrix && b.equals(new Variable("T"))) return new Transpose(a).simplify();
		if (a instanceof Matrix) return ((Matrix) a).pow(b);
		if (a instanceof Bool) return pow(((Bool) a).toInt(), b);
		if (b instanceof Bool) return pow(a, ((Bool) b).toInt());
		if ((a.equals(Int.ONE) || a.equals(Float.ONE)))
		{
			if (b instanceof Unique) return new Indeterminate();
			return a.copy();
		}
		if (a instanceof Text || b instanceof Text) throw new Error("String cannot be used in exponentiation.");
		if ((a.equals(Int.ZERO) || a.equals(Float.ZERO)) && b instanceof Constant)
		{
			if (((Constant) b).signum() == 0) return new Indeterminate();
			if (((Constant) b).signum() == 1) return a.copy();
			return new Inf();
		}
		if (b instanceof Unique)
		{
			if (b instanceof Inf || b instanceof Indeterminate) return b;
			if (a instanceof Inf || a instanceof Indeterminate) return new Indeterminate();
			return Int.ZERO;
		}
		if (a instanceof Indeterminate) return a.copy();
		if (a instanceof Complex && b instanceof Int)
		{
			int sign = ((Int) b).signum();
			if (sign == 0) return Int.ONE;
			if (sign == -1) {a = a.inverse(); b = b.negate();}
			BigInteger e = ((Int) b).value.subtract(BigInteger.ONE);
			Complex c = (Complex) a, r = c;
			while (e.compareTo(BigInteger.ZERO) != 0)
			{
				if (e.and(BigInteger.ONE).compareTo(BigInteger.ONE) == 0) r = r.mul(c);
				e = e.shiftRight(1); c = c.mul(c);
			}
			return r;
		}
		if (a instanceof Num && b instanceof Num) return ((Num) a).pow((Num) b);
		if (b instanceof Num)
		{
			if (b.equals(Int.ONE) || b.equals(Float.ONE)) return a.copy();
			if (b.equals(Int.ZERO)) return Int.ONE;
			if (b.equals(Float.ZERO)) return Float.ONE;
			if (a instanceof Sum && ((Sum) a).args.size() == 1)
			{
				return mul(pow(((Sum) a).args.getVal(0), b), pow(((Sum) a).args.getKey(0), b));
			}
			if (a instanceof Prod)
			{
				OrderedMap<Expression, Num> v = new OrderedMap<Expression, Num>(((Prod) a).args);
				for (int i = 0; i < v.size(); i++) {v.setVal(i, ((Num) b).mul(v.getVal(i)));}
				return new Prod(v).simplify();
			}
			// Causes loops in some cases
			/*
			if ((b instanceof Int) && ((Int) b).value.compareTo(BigInteger.ONE) > 0)
			{
				BigInteger e = ((Int) b).value.subtract(BigInteger.ONE);
				Expression c = a.copy(), r = c;
				while (e.compareTo(BigInteger.ZERO) != 0)
				{
					if (e.and(BigInteger.ONE).compareTo(BigInteger.ONE) == 0) r = r.mul(c);
					e = e.shiftRight(1); c = c.mul(c);
				}
				return r;
			}
			*/
			return new Prod(a.copy(), (Num) b.copy());
		}
		if (a instanceof Num && b instanceof Sum && ((Sum) b).args.size() == 1)
		{
			return pow(pow(a, ((Sum) b).args.getVal(0)), ((Sum) b).args.getKey(0));
		}
		if (a instanceof Power) return new Power(((Power) a).base, ((Power) a).exp.mul(b));
		if (a instanceof Prod && ((Prod) a).args.size() == 2)
		{
			return new Power(((Prod) a).args.getKey(0), ((Prod) a).args.getVal(0).mul(b));
		}
		return new Power(a.copy(), b.copy()).simplify();
	}
	
	public static boolean isZero(Expression x)
	{
		if (x instanceof Int)
		{
			return ((Int) x).value.equals(BigInteger.ZERO);
		}
		else if (x instanceof Float)
		{
			return ((Float) x).value.unscaledValue().equals(BigInteger.ZERO);
		}
		else if (x instanceof Rational)
		{
			return ((Rational) x).num.equals(BigInteger.ZERO);
		}
		return false;
	}
}
