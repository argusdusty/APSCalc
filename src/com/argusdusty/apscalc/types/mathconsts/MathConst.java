package com.argusdusty.apscalc.types.mathconsts;

import com.argusdusty.apscalc.types.Constant;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;

public abstract class MathConst extends Constant
{
	public static MathConst INF = new Inf();
	public String name;
	public MathConst(String name) {this.name = name;}
	public Constant simplify() {return this;}
	public String toString() {return name;}
	public boolean equals(Expression e) {return e instanceof MathConst && ((MathConst) e).name.equals(name);}
	public Expression inverse() {return this.pow(Int.ONE.negate());}
	public Expression negate() {return this.mul(Int.ONE.negate());}
}
