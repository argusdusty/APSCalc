package com.argusdusty.apscalc.utils;

import java.util.ArrayList;

import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.functions.BivarFunction;
import com.argusdusty.apscalc.functions.Function;
import com.argusdusty.apscalc.functions.UnivarFunction;
import com.argusdusty.apscalc.types.Bool;
import com.argusdusty.apscalc.types.Complex;
import com.argusdusty.apscalc.types.Constant;
import com.argusdusty.apscalc.types.EmptyExpression;
import com.argusdusty.apscalc.types.Equation;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Power;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Sum;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.Expseq;
import com.argusdusty.apscalc.types.mathconsts.Real;

public class EvalfUtils
{
	public static Expression evalf(Expression e)
	{
		if (e instanceof Bool) return evalf(((Bool) e).toInt());
		else if (e instanceof Int) return new Float((Int) e).simplify();
		else if (e instanceof Float) return e.simplify();
		else if (e instanceof Rational) return new Float((Rational) e).simplify();
		else if (e instanceof Real) return ((Real) e).decForm();
		else if (e instanceof Complex)
		{
			Complex c = (Complex) e;
			return new Complex(evalf(c.real), evalf(c.imag));
		}
		else if (e instanceof Variable || e instanceof Constant) return e.simplify();
		else if (e instanceof Power)
		{
			Power p = (Power) e;
			Expression r = evalf(p.base).pow(evalf(p.exp));
			return r;
		}
		else if (e instanceof Prod)
		{
			Prod p = (Prod) e;
			Expression r = evalf(p.args.getKey(0)).pow(evalf(p.args.getVal(0)));
			for (int i = 1; i < p.args.size(); i++)
			{
				r = r.mul(evalf(p.args.getKey(i)).pow(evalf(p.args.getVal(i))));
			}
			return r.simplify();
		}
		else if (e instanceof Sum)
		{
			Sum p = (Sum) e;
			Expression r = evalf(p.args.getKey(0)).mul(evalf(p.args.getVal(0)));
			for (int i = 1; i < p.args.size(); i++)
			{
				r = r.add(evalf(p.args.getKey(i)).mul(evalf(p.args.getVal(i))));
			}
			return r.simplify();
		}
		else if (e instanceof Equation) return new Equation(evalf(((Equation) e).left), evalf(((Equation) e).right));
		else if (e instanceof Expseq)
		{
			Expseq p = (Expseq) e;
			ArrayList<Expression> a = p.args;
			for (int i = 0; i < a.size(); i++)
			{
				a.set(i, evalf(a.get(i)));
			}
			return new Expseq(a);
		}
		else if (e instanceof EmptyExpression) return e;
		else if (e instanceof Function)
		{
			Function p = (Function) e;
			ArrayList<Expression> a = p.args;
			for (int i = 0; i < a.size(); i++)
			{
				a.set(i, evalf(a.get(i)));
			}
			return new Function(p.name, a);
		}
		else if (e instanceof UnivarFunction)
		{
			((UnivarFunction) e).arg1 = evalf(((UnivarFunction) e).arg1);
			return e.simplify();
		}
		else if (e instanceof BivarFunction)
		{
			((BivarFunction) e).arg1 = evalf(((BivarFunction) e).arg1);
			((BivarFunction) e).arg2 = evalf(((BivarFunction) e).arg2);
			return e.simplify();
		}
		throw new UnexpectedError("efc");
	}
}
