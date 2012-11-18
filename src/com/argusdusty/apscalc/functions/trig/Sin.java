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
import com.argusdusty.apscalc.types.mathconsts.Pi;
import com.argusdusty.apscalc.types.mathconsts.Unique;
import com.argusdusty.apscalc.utils.OrderedMap;

public class Sin extends TrigFunction
{
	public Sin(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "sin";
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof Unique) return new Indeterminate();
		if (arg1 instanceof Pi) return Int.ZERO;
		if (arg1 instanceof Complex)
		{
			Expression a = ((Complex) arg1).real, b = ((Complex) arg1).imag;
			Expression r = new Cosh(b).simplify().mul(new Sin(a).simplify());
			Expression i = new Cos(a).simplify().mul(new Sinh(b).simplify());
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
					if (c instanceof Int) return Int.ZERO;
					if (c.equals(Rational.HALF)) return Int.ONE;
					Int THREE = new Int(3), FOUR = new Int(4), SIX = new Int(6);
					if (c.equals(Rational.THIRD)) return THREE.pow(Rational.HALF).div(Int.TWO);
					Rational FOURTH = new Rational(Int.ONE, FOUR), SIXTH = new Rational(Int.ONE, SIX); 
					if (c.equals(FOURTH)) return Int.TWO.pow(Rational.HALF.negate());
					if (c.equals(SIXTH)) return Rational.HALF;
				}
				if (c instanceof Int  && ((Int) c).add(new Int(5).negate()).signum() != 1)
				{
					Expression d = c.add(Int.NEGONE);
					if (((Int) c).signum() == -1) return new Sin(c.negate()).simplify().negate();
					Expression r = new Sin(d.mul(b)).simplify().mul(new Cos(b).simplify());
					return r.add(new Sin(b).simplify().mul(new Cos(d.mul(b)).simplify()));
				}
				return new Sin(arg1);
			}
			Expression b = ((Sum) arg1).args.getKey(0);
			b = b.mul(((Sum) arg1).args.getVal(0));
			Expression c = arg1.sub(b);
			Expression r = new Cos(b).simplify().mul(new Sin(c).simplify());
			return r.add(new Cos(c).simplify().mul(new Sin(b).simplify()));
		}
		if (arg1.equals(Int.ZERO) || arg1.equals(Float.ZERO)) return arg1;
		if (arg1 instanceof Asin) return ((Asin) arg1).arg1;
		if (arg1 instanceof Acos) return Int.ONE.sub(((Acos) arg1).arg1.pow(Int.TWO)).pow(Rational.HALF);
		if (arg1 instanceof Atan)
		{
			return ((Atan) arg1).arg1.mul(Int.ONE.add(((Atan) arg1).arg1.pow(Int.TWO)).pow(Rational.HALF.negate()));
		}
		if (arg1 instanceof Float) return new Float(FastMath.sin(((Float) arg1).value));
		return new Sin(arg1);
	}
	
	public Expression derivative(Variable x) // d/dx(sin(f(x))) = cos(f(x))*f'(x)
	{
		return new Cos(arg1).mul(arg1.derivative(x));
	}
	
	public Expression substitute(Variable x, Expression e) {return new Sin(arg1.substitute(x, e).simplify()).simplify();}
	public Asin finverse(Expression e) {return new Asin(e);}
	
	public Expression expForm() // sin(x)=1/2*i*(e^(-i*x)-e^(i*x))
	{
		Expression i = Int.ONE.negate().pow(Rational.HALF);
		Expression x = Rational.HALF.mul(i);
		return x.mul(new Exp(i.mul(arg1).negate()).simplify().add(new Exp(i.mul(arg1)).simplify().negate()));
	}
	
	public Sin copy() {return new Sin(arg1);}
}
