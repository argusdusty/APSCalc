package com.argusdusty.apscalc.types;


public class Bool extends Constant
{
	public boolean val;
	public Bool(boolean val) {this.val = val;}
	public String toString() {return val?"true":"false";}
	public Expression simplify() {return new Bool(val);}
	public boolean equals(Expression e) {return e instanceof Bool && !(val ^ ((Bool) e).val);}
	public Expression substitute(Variable x, Num c) {return new Bool(val);}
	public Int toInt() {return val?Int.ONE:Int.ZERO;}
	public Bool copy() {return new Bool(val);}
	public int signum() {return val?1:0;}
}
