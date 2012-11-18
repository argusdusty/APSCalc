package com.argusdusty.apscalc.types.solve;

import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.functions.BivarFunction;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.utils.SolverUtils;

public class MSolve extends BivarFunction
{
	public MSolve(Expression arg1, Expression arg2) {this.arg1 = arg1; this.arg2 = arg2; this.name = "solve";}
	public Expression simplify() {return SolverUtils.msolve(arg1, arg2);}
	public Expression derivative(Variable x) {throw new UnexpectedError("bsd");}
	public Expression substitute(Variable x, Expression e) {throw new UnexpectedError("bss");}
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
	public MSolve copy() {return new MSolve(arg1, arg2);}
}
