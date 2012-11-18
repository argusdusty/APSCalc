package com.argusdusty.apscalc.functions;

import java.math.BigInteger;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.types.EmptyExpression;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Power;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.utils.OrderedMap;

public class Log extends BivarFunction
{
	public Log(Expression arg1, Expression arg2)
	{
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.name = "log";
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof Float && (arg2 instanceof Float))
		{
			return new Float(FastMath.log(((Float) arg1).value, ((Float) arg2).value));
		}
		if (arg1 instanceof Float && (arg2 instanceof Int))
		{
			return new Float(FastMath.log(((Float) arg1).value, ((Int) arg2).value));
		}
		if (arg1 instanceof Int)
		{
			OrderedMap<Int, Int> a = ((Int) arg1).factor(BigInteger.ZERO);
			Expression r = new EmptyExpression();
			for (int i = 0; i < a.size(); i++)
			{
				r = r.add(new Ln(a.getKey(i)).simplify().mul(a.getVal(i)).div(new Ln(arg2).simplify()));
			}
			return r;
		}
		if (arg1 instanceof Prod) //log(f(x)^a*g(x)^b, c) = a*log(f(x), c)+b*log(g(x), c)
		{
			Expression r = new EmptyExpression();
			OrderedMap<Expression, Num> v = new OrderedMap<Expression, Num>(((Prod) arg1).args);
			for (int i = 0; i < v.size(); i++)
			{
				r = r.add(new Log(v.getKey(i), arg2).simplify().mul(v.getVal(i)));
			}
			return r;
		}
		if (arg1 instanceof Power) //log(f(x)^g(x), c) = log(f(x), c)*g(x)
		{
			return new Log(((Power) arg1).base, arg2).simplify().mul(((Power) arg1).exp);
		}
		return (new Ln(arg1)).simplify().div((new Ln(arg2)).simplify());
	}
	
	public Expression derivative(Variable x) // d/dx(log(f(x), g(x))) = f'(x)/(f(x)*ln(g(x)))-ln(f(x))*g'(x)/(g(x)*ln(g(x))^2)
	{
		Expression r1 = arg1.derivative(x);
		Expression e1 = new Ln(arg2);
		r1 = r1.mul(arg1.mul(e1).inverse());
		Expression r2 = (new Ln(arg1)).mul(arg2.derivative(x));
		r2 = r2.mul(arg2.mul(e1.pow(Int.TWO)).inverse());
		return r1.add(r2.negate());
	}
	
	public Expression substitute(Variable x, Expression e)
	{
		return new Log(arg1.substitute(x, e), arg2.substitute(x, e)).simplify();
	}
	
	public Log copy() {return new Log(arg1, arg2);}
}
