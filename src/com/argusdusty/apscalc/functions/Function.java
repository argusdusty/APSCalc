package com.argusdusty.apscalc.functions;

import java.util.ArrayList;

import com.argusdusty.apscalc.ExpressionParser;
import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.functions.bool.BoolFunc;
import com.argusdusty.apscalc.functions.list.Determinant;
import com.argusdusty.apscalc.functions.list.Diagonalize;
import com.argusdusty.apscalc.functions.list.Echelon;
import com.argusdusty.apscalc.functions.list.Len;
import com.argusdusty.apscalc.functions.list.MatrixGen;
import com.argusdusty.apscalc.functions.list.QRDec;
import com.argusdusty.apscalc.functions.list.REchelon;
import com.argusdusty.apscalc.functions.list.Transpose;
import com.argusdusty.apscalc.functions.trig.Acos;
import com.argusdusty.apscalc.functions.trig.Acosh;
import com.argusdusty.apscalc.functions.trig.Asin;
import com.argusdusty.apscalc.functions.trig.Asinh;
import com.argusdusty.apscalc.functions.trig.Atan;
import com.argusdusty.apscalc.functions.trig.Cos;
import com.argusdusty.apscalc.functions.trig.Cosh;
import com.argusdusty.apscalc.functions.trig.Cot;
import com.argusdusty.apscalc.functions.trig.Csc;
import com.argusdusty.apscalc.functions.trig.Sec;
import com.argusdusty.apscalc.functions.trig.Sin;
import com.argusdusty.apscalc.functions.trig.Sinh;
import com.argusdusty.apscalc.functions.trig.Tan;
import com.argusdusty.apscalc.types.Bool;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.Expseq;
import com.argusdusty.apscalc.types.solve.MSolve;
import com.argusdusty.apscalc.types.solve.Solve;

public class Function extends AbstractFunction
{
	public ArrayList<Expression> args;
	
	public Function(String name, ArrayList<Expression> args)
	{
		this.name = name.toLowerCase(); this.args = args;
	}
	
	public String toString()
	{
		String result = name + "(";
		for (int i = 0; i < args.size(); i++) {result += args.get(i).toString() + ", ";}
		return result.substring(0, result.length() - 2) + ")";
	}
	
	public Expression simplify()
	{
		ArrayList<Expression> v = new ArrayList<Expression>(args);
		for (int i = 0; i < v.size(); i++) {v.set(i, v.get(i).simplify());}
		for (int i = 0; i < v.size(); i++)
		{
			if (v.get(i) instanceof Expseq)
			{
				if (name.equals("len"))
				{
					if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
					return new Len(v.get(0)).simplify();
				}
				ArrayList<Expression> a = new ArrayList<Expression>();
				Expseq s = (Expseq) v.get(i);
				for (int j = 0; j < s.args.size(); j++)
				{
					ArrayList<Expression> temp = v;
					temp.set(i, s.args.get(j));
					a.add(new Function(name, temp).simplify());
				}
				return new Expseq(a);
			}
		}
		if (name.equals("ln"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Ln(v.get(0)).simplify();
		}
		if (name.equals("log"))
		{
			if (v.size() != 2) throw new Error("Function " + name + " takes 2 argument");
			return new Log(v.get(0), v.get(1)).simplify();
		}
		else if (name.equals("exp"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Exp(v.get(0)).simplify();
		}
		else if (name.equals("sin"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Sin(v.get(0)).simplify();
		}
		else if (name.equals("cos"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Cos(v.get(0)).simplify();
		}
		else if (name.equals("tan"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Tan(v.get(0)).simplify();
		}
		else if (name.equals("sec"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Sec(v.get(0)).simplify();
		}
		else if (name.equals("csc"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Csc(v.get(0)).simplify();
		}
		else if (name.equals("cot"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Cot(v.get(0)).simplify();
		}
		else if (name.equals("asin"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Asin(v.get(0)).simplify();
		}
		else if (name.equals("acos"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Acos(v.get(0)).simplify();
		}
		else if (name.equals("sinh"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Sinh(v.get(0)).simplify();
		}
		else if (name.equals("cosh"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Cosh(v.get(0)).simplify();
		}
		else if (name.equals("asinh"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Asinh(v.get(0)).simplify();
		}
		else if (name.equals("acosh"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Acosh(v.get(0)).simplify();
		}
		else if (name.equals("atan"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Atan(v.get(0)).simplify();
		}
		else if (name.equals("sum"))
		{
			if (v.size() != 4) throw new Error("Function " + name + " takes 4 arguments");
			return new Summation(v.get(0), v.get(1), v.get(2), v.get(3)).simplify();
		}
		else if (name.equals("prod"))
		{
			if (v.size() != 4) throw new Error("Function " + name + " takes 4 arguments");
			return new Product(v.get(0), v.get(1), v.get(2), v.get(3)).simplify();
		}
		else if (name.equals("fact"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Fact(v.get(0)).simplify();
		}
		else if (name.equals("gcd"))
		{
			if (v.size() != 2) throw new Error("Function " + name + " takes 2 arguments");
			return new Gcd(v.get(0), v.get(1)).simplify();
		}
		else if (name.equals("lcm"))
		{
			if (v.size() != 2) throw new Error("Function " + name + " takes 2 arguments");
			return new Lcm(v.get(0), v.get(1)).simplify();
		}
		else if (name.equals("mod"))
		{
			if (v.size() != 2) throw new Error("Function " + name + " takes 2 arguments");
			return new Mod(v.get(0), v.get(1)).simplify();
		}
		else if (name.equals("solve"))
		{
			if (v.size() != 2) throw new Error("Function " + name + " takes 2 arguments");
			return new Solve(v.get(0), v.get(1)).simplify();
		}
		else if (name.equals("msolve"))
		{
			if (v.size() != 2) throw new Error("Function " + name + " takes 2 arguments");
			return new MSolve(v.get(0), v.get(1)).simplify();
		}
		else if (name.equals("derivative"))
		{
			if (v.size() != 2 && v.size() != 3) throw new Error("Function " + name + " takes 2 or 3 arguments");
			Expression e1 = v.get(0), e2 = v.get(1);
			if (!(e2 instanceof Variable)) throw new Error("Arg 2 of function 'derivative' must be a variable");
			if (v.size() == 2) return e1.derivative((Variable) e2);
			Expression e3 = v.get(2);
			if (!(e3 instanceof Int)) throw new Error("Arg 3 of function 'derivative' must be an integer");
			return e1.derivative((Variable) e2, ((Int) e3).value.intValue());
		}
		else if (name.equals("integral") || name.equals("integrate"))
		{
			if (v.size() != 2) throw new Error("Function " + name + " takes 2 arguments");
			Expression e1 = v.get(0), e2 = v.get(1);
			if (!(e2 instanceof Variable)) throw new Error("Arg 2 of function " + name + " must be a variable");
			if (v.size() == 2) return e1.integrate((Variable) e2);
		}
		else if (name.equals("factor"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " only takes 1 argument");
			Expression e = v.get(0);
			if (!(e instanceof Num)) throw new Error("Argument of " + name + " must be a number");
			return new Factor(e).simplify();
		}
		else if (name.equals("sqrt"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " only takes 1 argument");
			return v.get(0).pow(Rational.HALF);
		}
		else if (name.equals("isprime"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			Expression e = v.get(0);
			if (!(e instanceof Int)) throw new Error("Argument of " + name + " must be an integer");
			return new Bool(((Int) e).isPrime());
		}
		else if (name.equals("dismantle"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			Expression e = v.get(0); ExpressionParser.dismantle(e);
			return e;
		}
		else if (name.equals("int"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new ToInt(v.get(0)).simplify();
		}
		else if (name.equals("len"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Len(v.get(0)).simplify();
		}
		else if (name.equals("matrix"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new MatrixGen(v.get(0)).simplify();
		}
		else if (name.equals("str"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Str(v.get(0)).simplify();
		}
		else if (name.equals("determinant") || name.equals("det"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Determinant(v.get(0)).simplify();
		}
		else if (name.equals("transpose"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Transpose(v.get(0)).simplify();
		}
		else if (name.equals("diagonalize"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Diagonalize(v.get(0)).simplify();
		}
		else if (name.equals("not"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new BoolFunc(v.get(0), -1).simplify();
		}
		else if (name.equals("and"))
		{
			if (v.size() != 2) throw new Error("Function " + name + " takes 2 arguments");
			return new BoolFunc(v.get(0), v.get(1), 1).simplify();
		}
		else if (name.equals("or"))
		{
			if (v.size() != 2) throw new Error("Function " + name + " takes 2 arguments");
			return new BoolFunc(v.get(0), v.get(1), 2).simplify();
		}
		else if (name.equals("xor"))
		{
			if (v.size() != 2) throw new Error("Function " + name + " takes 2 arguments");
			return new BoolFunc(v.get(0), v.get(1), 3).simplify();
		}
		else if (name.equals("echelon"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Echelon(v.get(0)).simplify();
		}
		else if (name.equals("rechelon"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new REchelon(v.get(0)).simplify();
		}
		else if (name.equals("qrdec"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new QRDec(v.get(0)).simplify();
		}
		else if (name.equals("gamma"))
		{
			if (v.size() != 1) throw new Error("Function " + name + " takes 1 argument");
			return new Gamma(v.get(0)).simplify();
		}
		return new Function(name, v);
	}
	
	public Expression derivative(Variable x) {throw new Error("Can not take derivative of unknown function: " + name);}
	
	public boolean equals(Expression e)
	{
		if (!(e instanceof Function)) return false;
		ArrayList<Expression> v = ((Function) e).args;
		if (v.size() != args.size()) return false;
		for (int i = 0; i < args.size(); i++)
		{
			if (!(args.get(i).equals(v.get(i)))) return false;
		}
		return true;
	}
	
	public Function substitute(Variable x, Expression e)
	{
		ArrayList<Expression> v = new ArrayList<Expression>(args);
		for (int i = 0; i < v.size(); i++) {v.set(i, v.get(i).substitute(x, e));}
		return new Function(name, v);
	}
	
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
	public Function copy() {return new Function(name, args);}
}