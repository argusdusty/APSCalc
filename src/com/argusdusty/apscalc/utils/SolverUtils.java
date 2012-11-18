package com.argusdusty.apscalc.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;

import com.argusdusty.apscalc.APSCalc;
import com.argusdusty.apscalc.Settings;
import com.argusdusty.apscalc.errors.ReduceError;
import com.argusdusty.apscalc.errors.SolverError;
import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.functions.BivarFunction;
import com.argusdusty.apscalc.functions.Function;
import com.argusdusty.apscalc.functions.Ln;
import com.argusdusty.apscalc.functions.UnivarFunction;
import com.argusdusty.apscalc.types.Complex;
import com.argusdusty.apscalc.types.Constant;
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
import com.argusdusty.apscalc.types.mathconsts.MathConst;
import com.argusdusty.apscalc.types.solve.Poly;
import com.argusdusty.apscalc.types.solve.PolyFrac;
import com.argusdusty.apscalc.types.solve.Solveable;

public class SolverUtils
{ 
	public static Expression solve(Equation e, Variable x)
	{
		if (APSCalc.debug) {System.out.println("Solve: " + e + " for " + x);}
		Expression t = e.left.add(e.right.negate());
		if (t instanceof Prod && ((Prod) t).args.size() > 2)
		{
			OrderedMap<Expression, Num> v = new OrderedMap<Expression, Num>(((Prod) t).args);
			ArrayList<Expression> a = new ArrayList<Expression>();
			for (int i = 0; i < v.size(); i++)
			{
				try {a.add(solve(new Equation(v.getKey(i).pow(v.getVal(i)), Int.ZERO), x));}
				catch (SolverError se) {continue;}
			}
			if (a.size() == 0) throw new SolverError();
			else if (a.size() == 1) return a.get(0);
			return new Expseq(a);
		}
		try
		{
			Solveable s = reduce(e.left.add(e.right.negate()), Int.ZERO, x);
			if (s instanceof Poly && contains(new Expseq(((Poly) s).consts), x)) throw new ReduceError();
			else if (s instanceof PolyFrac)
			{
				if (contains(new Expseq(((PolyFrac) s).num.consts), x)) throw new ReduceError();
				if (contains(new Expseq(((PolyFrac) s).denom.consts), x)) throw new ReduceError();
			}
			Expression r = root(s).simplify();
			if (s.var.equals(x))
			{
				if (r instanceof Expseq)
				{
					ArrayList<Expression> a = new ArrayList<Expression>();
					for (int i = 0; i < ((Expseq) r).args.size(); i++)
						a.add(((Expseq) r).args.get(i));
					return new Expseq(a);
				}
				return r;
			}
			if (r instanceof Expseq)
			{
				ArrayList<Expression> a = new ArrayList<Expression>();
				for (int i = 0; i < ((Expseq) r).args.size(); i++)
					a.add(solve(new Equation(s.var, ((Expseq) r).args.get(i)), x));
				return new Expseq(a);
			}
			return solve(new Equation(s.var, r), x);
		}
		catch (ReduceError re)
		{
			System.out.println("Caution: May not find all solutions");
			try {return newton(x, e.left.add(e.right.negate()));}
			catch (Throwable tr)
			{
				return bisection(x, e.left.add(e.right.negate()), Float.TWO.negate(), Float.TWO);
			}
		}
	}
	
	public static Expression root(Solveable s)
	{
		Poly p;
		if (s instanceof PolyFrac) p = ((PolyFrac) s).num;
		else p = (Poly) s;
		if (APSCalc.debug) {System.out.println("Root: " + p);}
		if (p.dim == 0)
		{
			if (p.consts.get(0).equals(Int.ZERO) || p.consts.get(0).equals(Float.ZERO)) return p.var;
			throw new SolverError();
		}
		while ((p.consts.get(1).equals(Int.ZERO) || p.consts.get(1).equals(Float.ZERO)) 
				&& (p.consts.get(0).equals(Int.ZERO) || p.consts.get(0).equals(Float.ZERO)))
		{
			p.consts.remove(0); p.dim--;
		}
		if (p.dim == 1) return p.consts.get(0).mul(p.consts.get(1).inverse()).negate();
		if ((p.consts.get(0).equals(Int.ZERO) || p.consts.get(0).equals(Float.ZERO))) 
		{
			p.consts.remove(0); p.dim--;
			try
			{
				Expression e = root(p);
				if (e instanceof Expseq) {((Expseq) e).args.add(Int.ZERO); return e;}
				ArrayList<Expression> a = new ArrayList<Expression>();
				a.add(e); a.add(Int.ZERO); return new Expseq(a);
			}
			catch (SolverError se) {return Int.ZERO;}
		}
		if (p.dim == 2) // a*x^2+b*x+c=0, x = ((b^2-4*a*c)-b)/(2*a), (-(b^2-4*a*c)-b)/(2*a)
		{
			Expression a = p.consts.get(2), b = p.consts.get(1), c = p.consts.get(0);
			Expression beta = b.pow(Int.TWO).add(Int.TWO.add(Int.TWO).mul(a).mul(c).negate());
			beta = beta.pow(new Rational(Int.ONE, Int.TWO));
			ArrayList<Expression> v = new ArrayList<Expression>();
			v.add(beta.add(b.negate()).mul(a.mul(Int.TWO).inverse()));
			v.add(beta.add(b).negate().mul(a.mul(Int.TWO).inverse()));
			return new Expseq(v);
		}
		int q = PolyUtils.binomial(p);
		if (q != -1) // a*x^n+b=0
		{
			Rational r = new Rational(BigInteger.ONE, BigInteger.valueOf(q));
			Expression e = p.consts.get(q).inverse().mul(p.consts.get(0)).pow(r);
			boolean n = false;
			ArrayList<Expression> v = new ArrayList<Expression>();
			if ((q & 1) == 0) // x = +-i^(1/n)*(b/a)^(1/n), +-i^(3/n)*(b/a)^(1/n), ..., +-i^((n-1)/n)*(b/a)^(1/n)
			{
				for (int j = 1; j < q; j += 2)
				{
					v.add(Int.ONE.negate().pow(r.mul(new Int(j))).mul(e));
					v.add(v.get(j - 1).negate());
				}
				return new Expseq(v);
			}
			v.add(e.negate());
			for (int j = 1; j < q; j++) // x = -(-b/a)^(1/n), i^(1/n)*(-b/a)^(1/n), ..., +/-i^((n-1)/n)*(-b/a)^(1/n)
			{
				if (n) v.add(Int.ONE.negate().pow(r.mul(new Int(j))).mul(e).negate());
				else v.add(Int.ONE.negate().pow(r.mul(new Int(j))).mul(e));
				n = !n;
			}
			return new Expseq(v);
		}
		int g = gcd(diffs(PolyUtils.exps(p)));
		if (g != 1)
		{
			ArrayList<Expression> nc = new ArrayList<Expression>();
			ArrayList<Expression> nc2 = new ArrayList<Expression>();
			for (int i = 0; i < p.consts.size(); i += g) {nc.add(p.consts.get(i));}
			for (int i = 0; i < g; i++) {nc2.add(Int.ZERO);} nc2.add(Int.ONE);
			Expression r = root(new Poly(p.var, nc)).simplify(), r2;
			ArrayList<Expression> a = new ArrayList<Expression>();
			if (r instanceof Expseq)
			{
				for (int i = 0; i < ((Expseq) r).args.size(); i++)
				{
					nc2.set(0, ((Expseq) r).args.get(i).negate());
					r2 = root(new Poly(p.var, nc2));
					if (r2 instanceof Expseq)
						for (int j = 0; j < ((Expseq) r2).args.size(); j++) {a.add(((Expseq) r2).args.get(j));}
					else a.add(r2);
				}
				return new Expseq(a);
			}
			nc2.set(0, r.negate()); r2 = root(new Poly(p.var, nc2));
			if (r2 instanceof Expseq)
				for (int j = 0; j < ((Expseq) r2).args.size(); j++) {a.add(((Expseq) r2).args.get(j));}
			else return r2;
			return new Expseq(a);
		}
		if (p.dim == 3)
		{
			Num ONE = Int.ONE, TWO = Int.TWO, THREE = TWO.add(ONE), FOUR = TWO.add(TWO), SIX = THREE.add(THREE);
			Num NINE = SIX.add(THREE); Expression i3 = THREE.mul(Complex.I);
			Rational THIRD = Rational.THIRD, HALF = Rational.HALF;
			Expression a = p.consts.get(3), b = p.consts.get(2), c = p.consts.get(1), d = p.consts.get(0);
			//Q = 12*a*c^3+81*a^2*d^2+12*b^3*d-54*a*b*c*d-3*b^2*c^2
			//R = 3*a*c-b^2
			//P = (36*a*b*c-108*a^2*d-8*b^3+12*a*(Q)^(1/2))^(1/3)
			Expression Q = FOUR.mul(THREE).mul(a).mul(c.pow(THREE));
			Q = Q.add(NINE.mul(NINE).mul(a.pow(TWO)).mul(d.pow(TWO)));
			Q = Q.add(FOUR.mul(THREE).mul(b.pow(THREE)).mul(d));
			Q = Q.add(NINE.mul(SIX).mul(a).mul(b).mul(c).mul(d).negate());
			Q = Q.add(THREE.mul(b.pow(TWO)).mul(c.pow(TWO)).negate());
			Expression R = THREE.mul(a).mul(c).add(b.pow(TWO).negate());
			Expression P = NINE.mul(FOUR).mul(a).mul(b).mul(c);
			P = P.add(NINE.mul(THREE).mul(FOUR).mul(a.pow(TWO)).mul(d).negate());
			P = P.add(FOUR.mul(TWO).mul(b.pow(THREE)).negate());
			P = P.add(FOUR.mul(THREE).mul(a).mul(Q.pow(HALF))).pow(THIRD);
			//x1 = 1/(6*a)*P-2/3*R/(a*P)-b/(3*a)
			//x2 = 1/(12*a)*(1-i*3^(1/2))*P+(1/3)*(i*3^(1/2)+1)*R/(a*P)-b/(3*a)
			//x3 = 1/(12*a)*(1+i*3^(1/2))*P+(1/3)*(i*3^(1/2)-1)*R/(a*P)-b/(3*a)
			Expression x1 = THREE.mul(TWO).mul(a).inverse().mul(P);
			Expression x2 = x1.mul(HALF).mul(ONE.negate().add(i3));
			Expression x3 = x1.mul(HALF).mul(ONE.negate().add(i3.negate()));
			x1 = x1.add(TWO.mul(THIRD).negate().mul(R).mul(a.mul(P).inverse()));
			x2 = x2.add(THIRD.mul(ONE.add(i3)).mul(R).mul(a.mul(P).inverse()));
			x3 = x3.add(THIRD.mul(ONE.add(i3.negate())).mul(R).mul(a.mul(P).inverse()));
			ArrayList<Expression> v = new ArrayList<Expression>();
			v.add(x1.add(b.mul(THREE.mul(a).inverse()).negate()));
			v.add(x2.add(b.mul(THREE.mul(a).inverse()).negate()));
			v.add(x3.add(b.mul(THREE.mul(a).inverse()).negate()));
			return new Expseq(v);
		}
		if (p.dim == 4)
		{
			//TODO - If ever
		}
		Poly p2 = new Poly(new Variable("x"), p.consts);
		System.out.println("Caution: May not find all solutions");
		try
		{
			return new Expseq(horner(new Variable("x"), p2)).simplify();
		}
		catch (SolverError se)
		{
			return bisection(new Variable("x"), PolyUtils.toSum(p2), Float.TWO.negate(), Float.TWO);
		}
	}
	
	public static boolean contains(Expression e, Expression x)  // return (x in e)
	{
		if (e.equals(x)) return true;
		else if (e instanceof Variable || e instanceof Constant || e instanceof Text) return false;
		else if (e instanceof Prod)
		{
			OrderedMap<Expression, Num> v = ((Prod) e).args;
			for (int i = 0; i < v.size(); i++)
				if (contains(v.getKey(i), x) || contains(v.getVal(i), x)) return true;
			return false;
		}
		else if (e instanceof Sum)
		{
			OrderedMap<Expression, Num> v = ((Sum) e).args;
			for (int i = 0; i < v.size(); i++)
				if (contains(v.getKey(i), x) || contains(v.getVal(i), x)) return true;
			return false;
		}
		else if (e instanceof Expseq)
		{
			ArrayList<Expression> v = ((Expseq) e).args;
			for (int i = 0; i < v.size(); i++)
				if (contains(v.get(i), x)) return true;
			return false;
		}
		else if (e instanceof UnivarFunction)
		{
			Expression a = ((UnivarFunction) e).arg1;
			return contains(a, x);
		}
		else if (e instanceof BivarFunction)
		{
			Expression a1 = ((BivarFunction) e).arg1;
			Expression a2 = ((BivarFunction) e).arg2;
			return contains(a1, x) || contains(a2, x);
		}
		else if (e instanceof Function)
		{
			ArrayList<Expression> v = ((Function) e).args;
			for (int i = 0; i < v.size(); i++)
				if (contains(v.get(i), x)) return true;
			return false;
		}
		else if (e instanceof Poly) return contains(PolyUtils.toSum((Poly) e), x);
		else if (e instanceof PolyFrac)
			return contains(((PolyFrac) e).num, x) || contains(((PolyFrac) e).denom, x);
		else if (e instanceof Power) return contains(((Power) e).base, x) || contains(((Power) e).exp, x);
		else if (e instanceof Equation) return contains(((Equation) e).left, x) || contains(((Equation) e).right, x);
		else if (e instanceof Complex) return contains(((Complex) e).real, x) || contains(((Complex) e).imag, x);
		else if (e instanceof List)
		{
			ArrayList<Expression> v = ((List) e).args;
			for (int i = 0; i < v.size(); i++)
				if (contains(v.get(i), x)) return true;
			return false;
		}
		else if (e instanceof Matrix)
		{
			ArrayList<ArrayList<Expression>> v = ((Matrix) e).args;
			for (int i = 0; i < v.size(); i++)
				for (int j = 0; j < v.get(i).size(); j++)
					if (contains(v.get(i).get(j), x)) return true;
			return false;
		}
		throw new UnexpectedError("suc");
	}
	
	public static Solveable reduce(Expression a, Expression b, Expression x)
	{
		if (contains(b, x)) return reduce(a.add(b.negate()), Int.ZERO, x);
		if (!contains(a, x)) return PolyUtils.toPoly(x, a.add(b.negate()));
		if (a.equals(x)) return PolyUtils.toPoly(x, a.add(b.negate()));
		else if (a instanceof Complex) // r+g*i=b, (b-r)/g=i, (b^2-2*b*r+r^2)/(g^2)=-1
		{
			Expression f = ((Complex) a).real, g = ((Complex) a).imag;
			return reduce(f.mul(f).add(b.mul(b)).sub(f.mul(b).mul(Int.TWO)).div(g.mul(g)), Int.NEGONE, x);
		}
		else if (a instanceof Sum)
		{
			boolean s = false; Expression e;
			OrderedMap<Expression, Num> v = new OrderedMap<Expression, Num>(((Sum) a).args);
			if (v.size() == 1) {return reduce(v.getKey(0), b.mul(v.getVal(0).inverse()), x);}
			for (int i = 0; i < v.size(); i++)
			{
				if (v.getKey(i) instanceof Prod)
				{
					OrderedMap<Expression, Num> v2 = new OrderedMap<Expression, Num>(((Prod) v.getKey(i)).args);
					for (int j = 0; j < v2.size(); j++)
					{
						if (v2.getVal(j) instanceof Rational && contains(v2.getKey(j), x))
						{
							v.remove(i);
							Expression f = v2.getKey(j); Rational r = (Rational) v2.getVal(j);
							v2.remove(j);
							Expression g, h;
							if (v.size() == 0) g = Int.ZERO;
							else g = new Sum(v);
							if (v2.size() == 0) h = Int.ONE;
							else h = new Prod(v2);
							Int n = new Int(r.num); Int d = new Int(r.denom);
							return reduce(f.pow(n).add(b.add(g.negate()).pow(d).mul(h.pow(d.negate()))), Int.ZERO, x);
						}
					}
				}
			}
			for (int i = 0; i < v.size(); i++)
			{
				if (SolverUtils.contains(v.getKey(i), x)) s = true;
				else
				{
					e = v.getKey(i).mul(v.getVal(i).negate());
					a = a.add(e); b = b.add(e);
				}
			}
			if (s && a instanceof Sum) return PolyUtils.toPoly(x, a.add(b.negate()));
			return reduce(a, b, x);
		}
		else if (a instanceof Prod)
		{
			Expression r = b;
			Prod p = (Prod) a;
			for (int i = 0; i < p.args.size(); i++)
			{
				if (!contains(p.args.getKey(i), x))
				{
					r = r.mul(p.args.getKey(i).pow(p.args.getVal(i).negate()));
					a = a.mul(p.args.getKey(i).pow(p.args.getVal(i).negate()));
				}
				if (((Num) p.args.getVal(i)).signum() < 0)
				{
					OrderedMap<Expression, Num> v = new OrderedMap<Expression, Num>(p.args);
					v.remove(i); v.remove(i);
					return reduce(new Prod(v).sub(b.mul(p.args.getKey(i).pow(p.args.getVal(i).negate()))), Int.ZERO, x);
				}
			}
			if (r.equals(b)) return PolyUtils.toPoly(x, a.add(b.negate()));
			return reduce(a, r, x);
		}
		else if (a instanceof Constant) return PolyUtils.toPoly(x, a.add(b.negate()));
		else if (a instanceof UnivarFunction)
		{
			return reduce(((UnivarFunction) a).arg1, ((UnivarFunction) a).finverse(b).simplify(), x);
		}
		else if (a instanceof Power)
		{
			Power p = (Power) a;
			if (!SolverUtils.contains(p.base, x))
			{
				return reduce(p.exp, new Ln(b).simplify().mul(new Ln(p.base).simplify().inverse()), x);
			}
			if (!SolverUtils.contains(p.exp, x)) return reduce(p.base, b.pow(p.exp.inverse()), x);
			return reduce(p.exp.mul(new Ln(p.base).simplify().inverse()), new Ln(b).simplify(), x);
		}
		else if (a instanceof Variable) {return PolyUtils.toPoly(x, a.add(b.negate()));}
		throw new ReduceError();
	}
	
	public static boolean wrongVar(Expression e, Variable x)
	{
		if (e instanceof Variable) return (!e.equals(x));
		else if (e instanceof Constant) return e instanceof MathConst;
		else if (e instanceof Prod)
		{
			OrderedMap<Expression, Num> v = ((Prod) e).args;
			for (int i = 0; i < v.size(); i++) {if (wrongVar(v.getKey(i), x) || wrongVar(v.getVal(i), x)) return true;}
			return false;
		}
		else if (e instanceof Sum)
		{
			OrderedMap<Expression, Num> v = ((Sum) e).args;
			for (int i = 0; i < v.size(); i++) {if (wrongVar(v.getKey(i), x) || wrongVar(v.getVal(i), x)) return true;}
			return false;
		}
		else if (e instanceof Expseq)
		{
			ArrayList<Expression> v = ((Expseq) e).args;
			for (int i = 0; i < v.size(); i++) {if (wrongVar(v.get(i), x)) return true;}
			return false;
		}
		else if (e instanceof UnivarFunction)
		{
			Expression a = ((UnivarFunction) e).arg1;
			return wrongVar(a, x);
		}
		else if (e instanceof BivarFunction)
		{
			Expression a1 = ((BivarFunction) e).arg1;
			Expression a2 = ((BivarFunction) e).arg2;
			return wrongVar(a1, x) || wrongVar(a2, x);
		}
		else if (e instanceof Function)
		{
			ArrayList<Expression> v = ((Function) e).args;
			for (int i = 0; i < v.size(); i++) {if (wrongVar(v.get(i), x)) return true;}
			return false;
		}
		else if (e instanceof Poly) {return wrongVar(PolyUtils.toSum((Poly) e), x);}
		else if (e instanceof PolyFrac) return wrongVar(((PolyFrac) e).num, x) || wrongVar(((PolyFrac) e).denom, x);
		else if (e instanceof Power) return wrongVar(((Power) e).base, x) || wrongVar(((Power) e).exp, x);
		else if (e instanceof Equation) return wrongVar(((Equation) e).left, x) || wrongVar(((Equation) e).right, x);
		throw new UnexpectedError("suw");
	}
	
	public static Num newton(Variable v, Expression e)
	{
		MathContext MC = Settings.MC; Settings.MC = new MathContext(MC.getPrecision() + 2, MC.getRoundingMode());
		ArrayList<Float> a = new ArrayList<Float>();
		a.add(Float.ZERO); a.add(Float.ONE); a.add(Float.ONE.negate()); a.add(Float.TWO); //a.add(Float.TEN);
		for (int i = 0; i < a.size(); i++)
		{
			try {Float r = newton(v, e, a.get(i), MC); Settings.MC = MC; return r.round(MC).reduce();}
			catch (SolverError se) {continue;}
		}
		Settings.MC = MC; throw new SolverError();
	}
	
	public static Float newton(Variable v, Expression e, Float x, MathContext MC)
	{
		e = EvalfUtils.evalf(e);
		int maxi = 100*Settings.MC.getPrecision();
		if (wrongVar(e, v)) throw new SolverError();
		Expression ep = EvalfUtils.evalf(e.derivative(v));
		Float prevx = x, minx = new Float(BigDecimal.TEN.pow(100, Settings.MC)).negate(), maxx = minx.negate();
		Expression t = e.substitute(v, x).simplify();
		if (t.equals(Float.ZERO) || t.equals(Int.ZERO)) return x;
		t = t.mul(ep.substitute(v, x).simplify().inverse());
		if (!(t instanceof Num)) throw new SolverError();
		x = x.add(new Float((Num) t).negate());
		for (int i = 0; i < maxi; i++)
		{
			if (x.round(MC).equals(prevx.round(MC))) {return x.round(MC);}
			prevx = x;
			try
			{
				t = e.substitute(v, x).simplify();
				if (t.equals(Float.ZERO) || t.equals(Int.ZERO)) return x;
				t = t.mul(ep.substitute(v, x).simplify().inverse());
				if (!(t instanceof Num)) throw new SolverError();
				x = x.add(new Float((Num) t).negate());
			}
			catch (Throwable tr) {throw new SolverError();}
			if (x.value.compareTo(minx.value) == -1 || x.value.compareTo(maxx.value) == 1) throw new SolverError();
		}
		throw new SolverError();
	}
	
	public static Float bisection(Variable v, Expression e, Float min, Float max)
	{
		if (min.value.compareTo(max.value) == 0)
		{
			Expression x = e.substitute(v, min);
			if (x instanceof Num && (x.equals(Float.ZERO) || x.equals(Float.ZERO))) return min;
			throw new SolverError();
		}
		if (min.value.compareTo(max.value) == 1) return bisection(v, e, max, min);
		Expression x = e.substitute(v, min), y = e.substitute(v, max);
		if (!(x instanceof Num) || !(y instanceof Num)) throw new SolverError();
		int s1 = ((Num) x).signum(), s2 = ((Num) y).signum();
		if (s1 == 0) return min;
		if (s2 == 0) return max;
		if (s1*s2 != -1) throw new SolverError();
		Float half = max.add(min).mul(Float.HALF);
		MathContext MC = Settings.MC; Settings.MC = new MathContext(MC.getPrecision() + 2, MC.getRoundingMode());
		while (true)
		{
			if (min.round(MC).equals(max.round(MC))) {Settings.MC = MC; return min;}
			x = e.substitute(v, half);
			if (!(x instanceof Num)) {Settings.MC = MC; throw new SolverError();}
			s1 = ((Num) x).signum();
			if (s1 == 0) return half;
			if (s1 == s2) max = half;
			else min = half;
			half = max.mul(Float.HALF).add(min.mul(Float.HALF));
		}
	}
	
	public static ArrayList<Expression> horner(Variable v, Poly p)
	{
		Expression s = PolyUtils.toSum(p);
		ArrayList<Expression> a = new ArrayList<Expression>();
		ArrayList<Expression> c = new ArrayList<Expression>();
		Num x = newton(v, s); a.add(x);
		c.add(0, p.consts.get(p.consts.size() - 1));
		for (int i = p.consts.size() - 2; i > 0; i--) {c.add(0, c.get(0).mul(x).add(p.consts.get(i)));}
		p = new Poly(p.var, c); c = new ArrayList<Expression>();
		s = PolyUtils.toSum(p);
		while (p.dim > 2)
		{
			s = PolyUtils.toSum(p);
			try {x = newton(v, s);}
			catch (SolverError se) {return a;}
			a.add(x); c.add(0, p.consts.get(p.consts.size() - 1));
			for (int i = p.consts.size() - 2; i > 0; i--) {c.add(0, c.get(0).mul(x).add(p.consts.get(i)));}
			p = new Poly(p.var, c); c = new ArrayList<Expression>();
		}
		try
		{
			Expression e = root(p);
			if (e instanceof Expseq) a.addAll(((Expseq) e).args);
			else a.add(e);
			return a;
		}
		catch (SolverError se) {return a;}
	}
	
	static int gcd(int a, int b) {int temp; while (a != 0) {temp = a; a = b % a; b = temp;} return b;}
	static ArrayList<Integer> diffs(ArrayList<Integer> a)
	{
		ArrayList<Integer> v = new ArrayList<Integer>();
		for (int i = 1; i < a.size(); i++) {v.add(a.get(i)-a.get(i-1));}
		return v;
	}
	static int gcd(ArrayList<Integer> a)
	{
		if (a.size() == 0) throw new UnexpectedError("sug");
		if (a.size() == 1) return a.get(0);
		int r = gcd(a.get(1), a.get(0));
		for (int i = 2; i < a.size(); i++) {r = gcd(r, a.get(i));}
		return r;
	}
	
	public static Expression msolve(Expression e, Expression v) //TODO - Different algo?
	{
		if (!(e instanceof List)) throw new Error("arg1 for msolve must be a list of equations");
		ArrayList<Expression> ae = ((List) e).args;
		for (int i = 0; i < ae.size(); i++)
			if (!(ae.get(i) instanceof Equation)) throw new Error("arg1 for msolve must be a list of equations");
		if (!(v instanceof List)) throw new Error("arg2 for msolve must be a list of variables");
		ArrayList<Expression> av = ((List) v).args;
		for (int i = 0; i < av.size(); i++)
			if (!(av.get(i) instanceof Variable)) throw new Error("arg2 for msolve must be a list of variables");
		
		ArrayList<Expseq> r = new ArrayList<Expseq>();
		ArrayList<Expression> rt = new ArrayList<Expression>();
		Variable v1 = ((Variable) av.get(0));
		ArrayList<Equation> unused = new ArrayList<Equation>();
		for (int i = 0; i < ae.size(); i++)
		{
			if (!contains((Equation) ae.get(i), v1)) {unused.add((Equation) ae.get(i)); continue;}
			try {rt.add(solve(((Equation) ae.get(i)), v1));}
			catch (SolverError se) {throw se;}
		}
		r.add(new Expseq(rt));
		for (int i = 1; i < av.size(); i++)
		{
			ArrayList<Expression> r0 = r.get(i-1).args;
			v1 = (Variable) av.get(i);
			rt = new ArrayList<Expression>();
			ArrayList<Equation> temp = new ArrayList<Equation>();
			for (int j = 0; j < unused.size(); j++)
			{
				if (!contains(unused.get(j), v1)) {temp.add(unused.get(j)); continue;}
				try {rt.add(solve((unused.get(j)), v1));}
				catch (SolverError se) {throw se;}
			}
			unused = temp;
			for (int j = 0; j < r0.size() - 1; j++)
			{
				Equation eq = new Equation(r0.get(j), r0.get(j+1));
				if (!contains(eq, v1)) {unused.add(eq); continue;}
				try {rt.add(solve(eq, v1));}
				catch (SolverError se) {throw se;}
			}
			r.add(new Expseq(rt));
			if (rt.size() == 0) continue;
			ArrayList<Expression> temp2 = new ArrayList<Expression>();
			for (int j = 0; j < i; j++)
			{
				for (int k = 0; k < r.get(j).args.size(); k++)
					temp2.add(r.get(j).args.get(k).substitute(v1, rt.get(0)));
				r.set(j, new Expseq(temp2));
			}
		}
		ArrayList<Expression> result = new ArrayList<Expression>();
		for (int i = 0; i < r.size(); i++) result.add(r.get(i));
		return new List(result);
	}
}