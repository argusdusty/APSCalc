package com.argusdusty.apscalc.types.solve;

import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.functions.BivarFunction;
import com.argusdusty.apscalc.types.Equation;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.utils.SolverUtils;

public class Solve extends BivarFunction
{
	public Equation arg1;
	public Variable arg2;
	
	public Solve(Expression arg1, Expression arg2)
	{
		if (!(arg1 instanceof Equation)) throw new Error("Arg 1 of Solve must be an equation");
		this.arg1 = (Equation) arg1;
		if (!(arg2 instanceof Variable)) throw new Error("Arg 2 of Solve must be a variable");
		this.arg2 = (Variable) arg2;
		this.name = "solve";
	}
	
	public Expression simplify() {return SolverUtils.solve(arg1, arg2);}
	public Expression derivative(Variable x) {throw new UnexpectedError("bsd");}
	public Expression substitute(Variable x, Expression e) {throw new UnexpectedError("bss");}
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
	public Solve copy() {return new Solve(arg1, arg2);}
}
