package com.argusdusty.apscalc.functions.trig;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.functions.Exp;
import com.argusdusty.apscalc.types.Complex;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Sum;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.mathconsts.Indeterminate;
import com.argusdusty.apscalc.types.mathconsts.Inf;
import com.argusdusty.apscalc.types.mathconsts.Pi;
import com.argusdusty.apscalc.utils.OrderedMap;

public class Cos extends TrigFunction
{
	public Cos(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "cos";
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof Inf || arg1 instanceof Indeterminate) return new Indeterminate();
		if (arg1 instanceof Pi) return Int.ONE.negate();
		if (arg1 instanceof Complex)
		{
			Expression a = ((Complex) arg1).real, b = ((Complex) arg1).imag;
			Expression r = new Cosh(b).simplify().mul(new Cos(a).simplify());
			Expression i = new Sinh(b).simplify().mul(new Sin(a).simplify()).negate();
			return new Complex(r, i);
		}
		if (arg1 instanceof Sum)
		{
			OrderedMap<Expression, Num> a = new OrderedMap<Expression, Num>(((Sum) arg1).args);
			if (a.size() == 1)
			{
				Expression b = ((Sum) arg1).args.getKey(0), c = ((Sum) arg1).args.getVal(0);
				if (b instanceof Pi)
				{
					if (c instanceof Int) return Int.ONE.mul(new Int(((Int) c).signum()));
					if (c.equals(Rational.HALF)) return Int.ZERO;
					if (c.equals(Rational.THIRD)) return Rational.HALF;
					Int THREE = new Int(3), FOUR = new Int(4), SIX = new Int(6);
					Rational FOURTH = new Rational(Int.ONE, FOUR), SIXTH = new Rational(Int.ONE, SIX); 
					if (c.equals(FOURTH)) return Int.TWO.pow(Rational.HALF.negate());
					if (c.equals(SIXTH)) return THREE.pow(Rational.HALF).div(Int.TWO);
				}
				if (c instanceof Int && ((Int) c).add(new Int(5).negate()).signum() != 1)
				{
					if (((Int) c).signum() == -1) return new Sin(c.negate()).simplify().negate();
					Num d = ((Int) c).add(Int.NEGONE);
					Expression r = new Cos(d.mul(b)).simplify().mul(new Cos(b).simplify());
					return r.sub(new Sin(b).simplify().mul(new Sin(d.mul(b)).simplify()));
				}
				return new Sin(arg1);
			}
			Expression b = ((Sum) arg1).args.getKey(0);
			b = b.mul(((Sum) arg1).args.getVal(0));
			Expression c = arg1.sub(b);
			Expression r = new Cos(b).simplify().mul(new Cos(c).simplify());
			return r.sub(new Sin(c).simplify().mul(new Sin(b).simplify()));
		}
		if (arg1.equals(Int.ZERO) || arg1.equals(Float.ZERO)) return arg1.add(Int.ONE);
		if (arg1 instanceof Acos) return ((Acos) arg1).arg1;
		if (arg1 instanceof Asin) return Int.ONE.sub(((Asin) arg1).arg1.pow(Int.TWO)).pow(Rational.HALF);
		if (arg1 instanceof Atan)
		{
			return Int.ONE.mul(Int.ONE.add(((Atan) arg1).arg1.pow(Int.TWO)).pow(Rational.HALF.negate()));
		}
		if (arg1 instanceof Float) return new Float(FastMath.cos(((Float) arg1).value));
		return new Cos(arg1);
	}
	
	public Expression derivative(Variable x) // d/dx(cos(f(x))) = -sin(f(x))*f'(x)
	{
		return new Sin(arg1).negate().mul(arg1.derivative(x));
	}
	
	public Expression substitute(Variable x, Expression e) {return new Cos(arg1.substitute(x, e).simplify()).simplify();}
	
	public Acos finverse(Expression e) {return new Acos(e);}
	
	public Expression expForm() // cos(x)=1/2*(e^(-i*x)+e^(i*x))
	{
		Expression i = Int.ONE.negate().pow(Rational.HALF);
		return Rational.HALF.mul(new Exp(i.mul(arg1).negate()).simplify().add(new Exp(i.mul(arg1)).simplify()));
	}
	
	public Cos copy() {return new Cos(arg1);}
}
