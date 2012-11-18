package com.argusdusty.apscalc.utils;

import com.argusdusty.apscalc.functions.AbstractFunction;
import com.argusdusty.apscalc.functions.BivarFunction;
import com.argusdusty.apscalc.functions.Function;
import com.argusdusty.apscalc.functions.Product;
import com.argusdusty.apscalc.functions.Summation;
import com.argusdusty.apscalc.functions.UnivarFunction;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Power;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Sum;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.Text;
import com.argusdusty.apscalc.types.mathconsts.MathConst;

public class SortUtils
{
	public static int isFirstProd(Expression a, Expression b) // 1 if a goes first. -1 if b goes first. For Prod.
	{
		if (a instanceof Num)
		{
			if (b instanceof Num) return isFirst((Num) a, (Num) b);
			return 1;
		}
		else if (b instanceof Num) return -1;
		if (a instanceof MathConst)
		{
			if (b instanceof MathConst) return isFirst((MathConst) a, (MathConst) b);
			return 1;
		}
		else if (b instanceof MathConst) return -1;
		else if (a instanceof Variable)
		{
			if (b instanceof Variable) return isFirst((Variable) a, (Variable) b);
			return 1;
		}
		else if (b instanceof Variable) return -1;
		else if (a instanceof AbstractFunction)
		{
			if (b instanceof AbstractFunction) return isFirst((AbstractFunction) a, (AbstractFunction) b);
			return 1;
		}
		else if (b instanceof Function) return -1;
		else if (b instanceof Prod) return -1;
		else if (a instanceof Power)
		{
			if (b instanceof Power) return isFirst((Power) a, (Power) b);
			return 1;
		}
		else if (b instanceof Power) return -1;
		else if (a instanceof Sum)
		{
			if (b instanceof Sum) return isFirst((Sum) a, (Sum) b);
			return 1;
		}
		return -1;
	}
	
	public static int isFirstSum(Expression a, Expression b) // 1 if a goes first. -1 if b goes first. For Sum.
	{
		if (a instanceof Power)
		{
			if (b instanceof Power) return isFirst((Power) a, (Power) b);
			return 1;
		}
		else if (b instanceof Power) return -1;
		else if (a instanceof Prod)
		{
			if (b instanceof Prod) return isFirst((Prod) a, (Prod) b);
			return 1;
		}
		else if (b instanceof Prod) return -1;
		else if (a instanceof AbstractFunction)
		{
			if (b instanceof AbstractFunction) return isFirst((AbstractFunction) a, (AbstractFunction) b);
			return 1;
		}
		else if (b instanceof Function) return -1;
		else if (a instanceof Variable)
		{
			if (b instanceof Variable) return isFirst((Variable) a, (Variable) b);
			return 1;
		}
		else if (b instanceof Variable) return -1;
		else if (a instanceof MathConst)
		{
			if (b instanceof MathConst) return isFirst((MathConst) a, (MathConst) b);
			return 1;
		}
		else if (b instanceof MathConst) return -1;
		else if (a instanceof Num)
		{
			if (b instanceof Num) return isFirst((Num) a, (Num) b);
			return 1;
		}
		else if (b instanceof Num) return -1;
		return -1;
	}
	
	public static int isFirst(Num a, Num b) {return (b.add(a.negate()).signum());}
	public static int isFirst(MathConst a, MathConst b) {return b.name.compareTo(a.name);}
	public static int isFirst(Variable a, Variable b) {return b.name.compareTo(a.name);}
	public static int isFirst(AbstractFunction a, AbstractFunction b)
	{
		if (a instanceof UnivarFunction)
		{
			int i = a.name.compareTo(b.name);
			if (i != 0) return i;
			if (!(b instanceof UnivarFunction)) return -1;
			return isFirstSum(((UnivarFunction) a).arg1, ((UnivarFunction) b).arg1);
		}
		if (a instanceof BivarFunction)
		{
			int i = a.name.compareTo(b.name);
			if (i != 0) return i;
			if (!(b instanceof BivarFunction)) return -1;
			i = isFirstSum(((BivarFunction) a).arg1, ((BivarFunction) b).arg1);
			if (i == 0) i = isFirstSum(((BivarFunction) a).arg2, ((BivarFunction) b).arg2);
			return i;
		}
		if (a instanceof Summation)
		{
			int i = a.name.compareTo(b.name);
			if (i != 0) return i;
			if (!(b instanceof Summation)) return -1;
			i = isFirstSum(((Summation) a).arg1, ((Summation) b).arg1);
			if (i == 0) i = isFirstSum(((Summation) a).arg2, ((Summation) b).arg2);
			if (i == 0) i = isFirstSum(((Summation) a).arg3, ((Summation) b).arg3);
			if (i == 0) i = isFirstSum(((Summation) a).arg4, ((Summation) b).arg4);
			return i;
		}
		if (a instanceof Product)
		{
			int i = a.name.compareTo(b.name);
			if (i != 0) return i;
			if (!(b instanceof Product)) return -1;
			i = isFirstSum(((Product) a).arg1, ((Product) b).arg1);
			if (i == 0) i = isFirstSum(((Product) a).arg2, ((Product) b).arg2);
			if (i == 0) i = isFirstSum(((Product) a).arg3, ((Product) b).arg3);
			if (i == 0) i = isFirstSum(((Product) a).arg4, ((Product) b).arg4);
			return i;
		}
		int i = a.name.compareTo(b.name);
		if (i != 0) return i;
		Function f1 = (Function) a, f2 = (Function) b; int j = 0;
		if (f1.args.size() != f2.args.size()) return Integer.signum(f1.args.size() - f2.args.size());
		if (j == f1.args.size() || j == f2.args.size()) return i;
		while (i == 0)
		{
			i = isFirstSum(f1.args.get(j), f1.args.get(j)); j++;
			if (j == f1.args.size() || j == f2.args.size()) break;
		}
		return i;
	}
	public static int isFirst(Prod a, Prod b)
	{
		int i = 0, j = 0;
		if (j == a.args.size() || j == b.args.size()) return i;
		while (i == 0)
		{
			i = isFirstSum(a.args.getKey(j), b.args.getKey(j)); j++;
			if (j == a.args.size() || j == b.args.size()) return i;
		}
		return i;
	}
	public static int isFirst(Power a, Power b)
	{
		int i = isFirstSum(a.base, b.base);
		if (i == 0) return isFirstSum(a.exp, b.exp);
		return i;
	}
	public static int isFirst(Sum a, Sum b)
	{
		int i = 0, j = 0;
		if (j == a.args.size() || j == b.args.size()) return i;
		while (i == 0)
		{
			i = isFirstSum(a.args.getKey(j), b.args.getKey(j)); j++;
			if (j == a.args.size() || j == b.args.size()) return i;
		}
		return i;
	}
	public static int isFirst(Text a, Text b) {return a.text.compareTo(b.text);}
}
