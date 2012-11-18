package com.argusdusty.apscalc.types.solve;

import java.math.BigInteger;
import java.util.ArrayList;

import com.argusdusty.apscalc.errors.PolyError;
import com.argusdusty.apscalc.functions.Gcd;
import com.argusdusty.apscalc.functions.Ln;
import com.argusdusty.apscalc.types.Constant;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Power;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Sum;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.utils.PolyUtils;
import com.argusdusty.apscalc.utils.SolverUtils;

public class Poly extends Solveable
{
	public ArrayList<Expression> consts;
	public int dim;
	
	public Poly(Expression var, ArrayList<Expression> consts)
	{
		if (var instanceof Constant) throw new PolyError();
		if (consts.size() == 0) throw new PolyError();
		if (var instanceof Sum && ((Sum) var).args.size() == 1)
		{
			var = ((Sum) var).args.getKey(0);
			Expression mul = ((Sum) var).args.getVal(0);
			for (int i = 0; i < consts.size(); i++)
				consts.set(i, consts.get(i).mul(mul.pow(new Int(i))));
		}
		for (int i = 0; i < consts.size(); i++) if (SolverUtils.contains(consts.get(i), var)) throw new PolyError();
		for (int i = consts.size() - 1; i >= 0; i--)
		{
			if (consts.get(i).equals(Int.ZERO) || consts.get(i).equals(Float.ZERO)) consts.remove(i);
			else break;
		}
		if (consts.size() == 0) consts.add(Int.ZERO);
		this.var = var;
		this.consts = consts;
		this.dim = consts.size() - 1;
	}

	public Expression simplify()
	{
		return new Poly(var, consts);
	}
	
	public Expression derivative(Variable x)
	{
		return PolyUtils.toSum(this).derivative(x);
	}
	
	public String toString()
	{
		return PolyUtils.toSum(this).toString();
	}
	
	public Solveable add(Expression e)
	{
		ArrayList<Expression> v = new ArrayList<Expression>(consts);
		if (var.equals(e))
		{
			v.set(1, v.get(1).add(e));
			return new Poly(var, v);
		}
		else if (e instanceof Poly)
		{
			Poly p = (Poly) e;
			if (p.var.equals(var))
			{
				if (dim >= p.dim)
				{
					for (int i = 0; i < p.consts.size(); i++)
					{
						v.set(i, v.get(i).add(p.consts.get(i)));
					}
					return new Poly(var, v);
				}
				return p.add(this);
			}
			else if (var instanceof Prod && ((Prod) var).args.size() == 2)
			{
				if (p.var instanceof Prod && ((Prod) p.var).args.size() == 2)
				{
					if (((Prod) var).args.getKey(0).equals(((Prod) p.var).args.getKey(0)))
					{
						Num b1 = (Num) ((Prod) var).args.getVal(0); Num b2 = (Num) ((Prod) p.var).args.getVal(0);
						Expression x = ((Prod) var).args.getKey(0); Num gcd = (Num) new Gcd(b1, b2).simplify();
						int m1 = ((Int) b1.mul(gcd.inverse())).value.intValue();
						ArrayList<Expression> a1 = new ArrayList<Expression>();
						for (int i = 0; i <= dim*m1; i++) {a1.add(Int.ZERO);}
						for (int i = 0; i < consts.size(); i++) {a1.set(i*m1, consts.get(i));}
						Poly p1 = new Poly(x.pow(gcd), a1);
						int m2 = ((Int) b2.mul(gcd.inverse())).value.intValue();
						ArrayList<Expression> a2 = new ArrayList<Expression>();
						for (int i = 0; i <= p.dim*m2; i++) {a2.add(Int.ZERO);}
						for (int i = 0; i < p.consts.size(); i++) {a2.set(i*m2, p.consts.get(i));}
						Poly p2 = new Poly(x.pow(gcd), a2);
						return p1.add(p2);
					}
					else throw new PolyError();
				}
				else if (p.var.equals(((Prod) var).args.getKey(0)))
				{
					Num b1 = (Num) ((Prod) var).args.getVal(0); Num b2 = Int.ONE;
					Expression x = p.var; Num gcd = (Num) new Gcd(b1, b2).simplify();
					int m1 = ((Int) b1.mul(gcd.inverse())).value.intValue();
					ArrayList<Expression> a1 = new ArrayList<Expression>();
					for (int i = 0; i <= dim*m1; i++) {a1.add(Int.ZERO);}
					for (int i = 0; i < consts.size(); i++) {a1.set(i*m1, consts.get(i));}
					Poly p1 = new Poly(x.pow(gcd), a1);
					int m2 = ((Int) b2.mul(gcd.inverse())).value.intValue();
					ArrayList<Expression> a2 = new ArrayList<Expression>();
					for (int i = 0; i <= p.dim*m2; i++) {a2.add(Int.ZERO);}
					for (int i = 0; i < p.consts.size(); i++) {a2.set(i*m2, p.consts.get(i));}
					Poly p2 = new Poly(x.pow(gcd), a2);
					return p1.add(p2);
				}
			}
			else if (p.var instanceof Prod && ((Prod) p.var).args.size() == 2) return p.add(this);
			else if (var instanceof Power && p.var instanceof Power)
			{
				Power p1 = (Power) var, p2 = (Power) p.var;
				if (p1.base.equals(p2.base))
				{
					Num n1 = PolyUtils.factorNum(p1.exp), n2 = PolyUtils.factorNum(p2.exp);
					if (!p1.exp.mul(n1.inverse()).equals(p2.exp.mul(n2.inverse()))) throw new PolyError();
					Expression x = p1.base.pow(p1.exp.mul(n1.inverse())); Num gcd = (Num) new Gcd(n1, n2).simplify();
					int m1 = ((Int) n1.mul(gcd.inverse())).value.intValue();
					ArrayList<Expression> a1 = new ArrayList<Expression>();
					for (int i = 0; i <= dim*m1; i++) {a1.add(Int.ZERO);}
					for (int i = 0; i < consts.size(); i++) {a1.set(i*m1, consts.get(i));}
					Poly pn1 = new Poly(x.pow(gcd), a1);
					int m2 = ((Int) n2.mul(gcd.inverse())).value.intValue();
					ArrayList<Expression> a2 = new ArrayList<Expression>();
					for (int i = 0; i <= p.dim*m2; i++) {a2.add(Int.ZERO);}
					for (int i = 0; i < p.consts.size(); i++) {a2.set(i*m2, p.consts.get(i));}
					Poly pn2 = new Poly(x.pow(gcd), a2);
					return pn1.add(pn2);
				}
				Expression lns = new Ln(p2.base).simplify().mul(new Ln(p1.base).simplify().inverse());
				if (lns instanceof Num)
				{
					if (new Poly(p1.base.pow(p2.exp.mul(lns)), p.consts).equals(p))
					{
						return p.add(new Poly(p2.base.pow(p1.exp.mul(lns.inverse())), p.consts));
					}
					return add(new Poly(p1.base.pow(p2.exp.mul(lns)), p.consts));
				}
			}
			if (dim < p.dim) return p.add(this);
			v.set(0, v.get(0).add(PolyUtils.toSum(p)));
			return new Poly(var, v);
		}
		else if (e instanceof PolyFrac) return ((PolyFrac) e).add(this);
		else return add(PolyUtils.toPoly(var, e));
	}
	
	public Solveable mul(Expression e)
	{
		ArrayList<Expression> v = new ArrayList<Expression>(consts);
		if (e instanceof Constant)
		{
			for (int i = 0; i < v.size(); i++) {v.set(i, v.get(i).mul(e));}
			return new Poly(var, v);
		}
		if (e instanceof Poly)
		{
			Poly p = (Poly) e;
			if (p.var.equals(var))
			{
				v = new ArrayList<Expression>();
				for (int i = 0; i <= dim + p.dim; i++) {v.add(Int.ZERO);}
				for (int i = 0; i <= dim; i++)
				{
					for (int j = 0; j <= p.dim; j++) {v.set(i + j, v.get(i + j).add(consts.get(i).mul(p.consts.get(j))));}
				}
				return new Poly(var, v);
			}
			else if ((PolyUtils.uninomial(this) == PolyUtils.uninomial(p)) && (PolyUtils.uninomial(p) != -1))
			{
				int a = PolyUtils.uninomial(p);
				ArrayList<Expression> c = new ArrayList<Expression>();
				for (int i = 0; i < 2*a; i++) {c.add(Int.ZERO);}
				c.add(consts.get(a).mul(p.consts.get(a)));
				return new Poly(var.mul(p.var), c);
			}
			else if (p.dim > dim) return p.mul(this);
			else
			{
				Expression s = PolyUtils.toSum(p);
				for (int i = 0; i <= dim; i++) {v.set(i, v.get(i).mul(s));}
				return new Poly(var, v);
			}
		}
		if (e instanceof PolyFrac) return ((PolyFrac) e).mul(this);
		else return PolyUtils.toPoly(var, e).mul(this);
	}
	
	public Solveable pow(Expression e)
	{
		if (e instanceof Num && ((Num) e).signum() == -1) return pow(e.negate()).inverse();
		if (e instanceof Int && ((Int) e).value.signum() == 1)
		{
			Solveable result = new Poly(var, consts);
			Poly p = new Poly(var, consts);
			BigInteger n = ((Int) e).value;
			for (BigInteger i = BigInteger.ONE; i.compareTo(n) == -1; i = i.add(BigInteger.ONE))
			{
				result = result.mul(p);
			}
			return result;
		}
		else if (e instanceof Int && ((Int) e).value.signum() == 0)
		{
			ArrayList<Expression> a = new ArrayList<Expression>();
			a.add(Int.ONE); return new Poly(var, a);
		}
		else if (e instanceof Rational)
		{
			int u = PolyUtils.uninomial(this);
			if (u == -1) throw new PolyError();
			ArrayList<Expression> a = new ArrayList<Expression>(consts);
			a.set(u, a.get(u).pow(e));
			return new Poly(var.pow(e), a);
		}
		throw new PolyError();
	}
	
	public boolean equals(Expression e)
	{
		if (!(e instanceof Poly)) return false;
		ArrayList<Expression> v = ((Poly) e).consts;
		if (v.size() != consts.size()) return false;
		for (int i = 0; i < consts.size(); i++)
		{
			if (!(consts.get(i).equals(v.get(i)))) return false;
		}
		return true;
	}
	
	public Poly negate()
	{
		ArrayList<Expression> v = new ArrayList<Expression>(consts);
		for (int i = 0; i < v.size(); i++) {v.set(i, v.get(i).negate());}
		return new Poly(var, v);
	}
	
	public Solveable inverse() {return new PolyFrac(PolyUtils.toPoly(var, Int.ONE), this);}
	public Poly copy() {return new Poly(var, consts);}
	
	public Expression substitute(Variable x, Expression e)
	{
		if (SolverUtils.contains(var, x)) return PolyUtils.toSum(this).substitute(x, e);
		ArrayList<Expression> v = new ArrayList<Expression>(consts);
		for (int i = 0; i < v.size(); i++) {v.set(i, v.get(i).substitute(x, e));}
		return new Poly(var, v);
	}
}
