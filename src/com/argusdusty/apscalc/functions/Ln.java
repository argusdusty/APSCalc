package com.argusdusty.apscalc.functions;

import java.math.BigInteger;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.functions.trig.Atan;
import com.argusdusty.apscalc.types.Complex;
import com.argusdusty.apscalc.types.EmptyExpression;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Power;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Sum;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.mathconsts.E;
import com.argusdusty.apscalc.types.mathconsts.Indeterminate;
import com.argusdusty.apscalc.types.mathconsts.Inf;
import com.argusdusty.apscalc.types.mathconsts.NegInf;
import com.argusdusty.apscalc.utils.OrderedMap;

public class Ln extends UnivarFunction
{
	public Ln(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "ln";
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof E) return Int.ONE;
		if (arg1 instanceof Indeterminate) return new Indeterminate();
		if (arg1 instanceof Inf || arg1 instanceof NegInf) return new Inf();
		if (arg1 instanceof Complex) // ln(a+b*i) = ln(a^2+b^2)/2 + atan(b/a)*i
		{
			Expression a = ((Complex) arg1).real, b = ((Complex) arg1).imag;
			Expression r = new Ln(a.pow(Int.TWO).add(b.pow(Int.TWO))).simplify().mul(Rational.HALF);
			Expression i = new Atan(b.div(a)).simplify();
			return new Complex(r, i);
		}
		if (arg1.equals(Int.ZERO) || arg1.equals(Float.ZERO)) return new Inf().negate();
		if (arg1.equals(Int.ONE) || arg1.equals(Float.ONE)) return Int.ZERO;
		if (arg1 instanceof Float) return new Float(FastMath.ln(((Float) arg1).value));
		if (arg1 instanceof Exp) return ((Exp) arg1).arg1;
		if (arg1 instanceof Prod) // ln(f(x)^a*g(x)^b) = a*ln(f(x)) + b*ln(g(x))
		{
			OrderedMap<Expression, Num> v = new OrderedMap<Expression, Num>(((Prod) arg1).args);
			Expression r = new EmptyExpression();
			for (int i = 0; i < v.size(); i++) {r = r.add(new Ln(v.getKey(i)).simplify().mul(v.getVal(i)));}
			return r;
		}
		if (arg1 instanceof Sum && ((Sum) arg1).args.size() == 1) // ln(a*f(x)) = ln(a) + ln(f(x))
		{
			OrderedMap<Expression, Num> v = new OrderedMap<Expression, Num>(((Sum) arg1).args);
			return new Ln(v.getKey(0)).add(new Ln(v.getVal(0)));
		}
		if (arg1 instanceof Power) // ln(f(x)^g(x)) = g(x)*ln(f(x))
		{
			return new Ln(((Power) arg1).base).simplify().mul(((Power) arg1).exp);
		}
		if (arg1 instanceof Int)
		{
			OrderedMap<Int, Int> a = ((Int) arg1).factor(BigInteger.ZERO);
			if (a.size() == 1) return new Ln(a.getKey(0)).mul(a.getVal(0));
			Expression r = new EmptyExpression();
			for (int i = 0; i < a.size(); i++) {r = r.add(new Ln(a.getKey(i)).simplify()).mul(a.getVal(i));}
			return r;
		}
		if (arg1 instanceof Rational)
		{
			Rational r = (Rational) arg1;
			return new Ln(new Int(r.num)).simplify().add(new Ln(new Int(r.denom)).simplify().negate());
		}
		return new Ln(arg1);
	}
	
	public Expression derivative(Variable x) // d/dx(ln(f(x))) = f'(x)/f(x)
	{
		return arg1.derivative(x).mul(arg1.inverse());
	}
	
	public Expression substitute(Variable x, Expression e) {return new Ln(arg1.substitute(x, e)).simplify();}
	public Exp finverse(Expression e) {return new Exp(e);}
	public Ln copy() {return new Ln(arg1);}
}
