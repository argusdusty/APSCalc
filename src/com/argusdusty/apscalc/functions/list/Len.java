package com.argusdusty.apscalc.functions.list;

import com.argusdusty.apscalc.errors.DerivativeError;
import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.functions.UnivarFunction;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.Expseq;
import com.argusdusty.apscalc.types.arrays.List;
import com.argusdusty.apscalc.types.arrays.Matrix;
import com.argusdusty.apscalc.types.arrays.Text;

public class Len extends UnivarFunction
{
	public Len(Expression arg1) {this.arg1 = arg1; this.name = "len";}
	
	public Expression derivative(Variable x) {throw new DerivativeError(name);}
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
	public Expression substitute(Variable x, Expression e) {return new Len(arg1.substitute(x, e)).simplify();}
	public Expression copy() {return new Len(arg1);}
	
	public Expression simplify()
	{
		if (arg1 instanceof Text) return new Int(((Text) arg1).text.length());
		if (arg1 instanceof Expseq) return new Int(((Expseq) arg1).args.size());
		if (arg1 instanceof Matrix) return new Int(((Matrix) arg1).args.size());
		if (arg1 instanceof List) return new Int(((List) arg1).args.size());
		return copy();
	}
}
