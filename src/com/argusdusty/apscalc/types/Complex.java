package com.argusdusty.apscalc.types;


public class Complex extends Expression
{
	public static Complex I = new Complex(Int.ZERO, Int.ONE);
	
	boolean CommutativeAdd = true;
	boolean CommutativeMul = true;
	
	public Expression real;
	public Expression imag;
	
	public Complex(Expression real, Expression imag)
	{
		if (real instanceof Complex)
		{
			if (imag instanceof Complex)
			{
				this.real = ((Complex) real).real.sub(((Complex) imag).imag);
				this.imag = ((Complex) real).imag.add(((Complex) imag).real);
			}
			else
			{
				this.real = ((Complex) real).real;
				this.imag = ((Complex) real).imag.add(imag);
			}
		}
		else
		{
			if (imag instanceof Complex)
			{
				this.real = ((Complex) real).real.sub(((Complex) imag).imag);
				this.imag = ((Complex) imag).real;
			}
			else this.real = real; this.imag = imag;
		}
	}

	public Expression simplify()
	{
		Expression r = real.simplify(), i = imag.simplify();
		if (i.equals(Int.ZERO) || i.equals(Float.ZERO)) return r;
		return new Complex(r, i);
	}
	
	public String toString()
	{
		String a = real.toString(), b = imag.toString();
		if (imag instanceof Sum) b = "(" + b + ")";
		boolean negb = b.startsWith("-");
		if (negb)
		{
			if (a.equals("0"))
			{
				if (b.equals("-1")) return "-i";
				return b + "*i";
			}
			return a + b + "*i";
		}
		if (a.equals("0"))
		{
			if (b.equals("0")) return b;
			if (b.equals("1")) return "i";
			return b + "*i";
		}
		if (b.equals("0")) return a;
		if (b.equals("1")) return a + "+i";
		return a + "+" + b + "*i";
	}
	
	public Complex mul(Complex c)
	{
		return new Complex(real.mul(c.real).add(imag.mul(c.imag).negate()), imag.mul(c.real).add(real.mul(c.imag)));
	}
	
	public boolean equals(Expression e)
	{
		return (e instanceof Complex) && (((Complex) e).real.equals(real) && ((Complex) e).imag.equals(imag));
	}
	
	public Expression substitute(Variable x, Expression e)
	{
		return new Complex(real.substitute(x, e), imag.substitute(x, e)).simplify();
	}
	
	public Expression inverse()
	{
		Expression m = real.pow(Int.TWO).add(imag.pow(Int.TWO)).inverse();
		return new Complex(real.mul(m), imag.mul(m));
	}
	
	public Complex copy() {return new Complex(real, imag);}
}
