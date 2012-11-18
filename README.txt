APSCalc: Arbitrary Precision Symbolic Calculator

I make no guarantees of compilation. This is a pre-alpha unfinished project. I also make no guarantees that this README is up to date and represents the current status of the code.

Features:
	Symbolic manipulation
	Differentiation
	Sigma and Capital Pi notations ('sum' and 'prod')
	Evaluating the expression to a certain number of digits ('evalf')
	Full suite of trigonometric functions.
	Integer factorization and very quick primality testing.
	Arbitrary precision - Currently limited to 16 digits, but is capable of handling up to 50+ digit accuracy, and will soon be extended.
	Function solver - Capable of expression reduction, floating-point approximations, and advanced polynomial recognition.
		Exact solutions limited to polynomials of degree 3 or less. Planned to be extended soon.
	Expression ordering - x + y + z + a -> a+x+y+z
	Variable assignment: a:=2 sets variable 'a' to the value 2. Then, a^4 -> 16

Available Functions:
	Univar:
		Exp: Exponential -- exp(Expression):  exp(1) -> Exp(1),         exp(0.5) -> 1.648721270700128
		Fact: Factorial --- fact(Int):        fact(1) -> 1,             fact(20) -> 2432902008176640000
		Ln: Natural log --- ln(Expression):   ln(1) -> Ln(1),           ln(0.5) -> -0.6931471805599453
		Factor: Factoring - factor(Int):      factor(24) -> 2^3*3,      factor(fact(10)) -> 2^8*3^4*5^2*7
		ToInt: To Integer - int(Expression):  int(1.2) -> 1,            int(Pi) -> 3  
		Trig:
			Acos: Arccosine ---------- acos(Expression):  acos(1) -> Acos(1),        acos(0.5) -> 1.047197551196598
			Asin: Arcsine ------------ asin(Expression):  asin(1) -> Asin(1),        asin(0.5) -> 0.5235987755982989
			Atan: Arctangent --------- atan(Expression):  atan(1) -> Atan(1),        atan(0.5) -> 0.4636476090008061
			Cos: Cosine -------------- cos(Expression):   cos(1) -> Cos(1),          cos(0.5) -> 0.8775825618903727
			Cot: Cotangent ----------- cot(Expression):   cot(1) -> Cot(1),          cot(0.5) -> 1.830487721712452
			Csc: Cosecant ------------ csc(Expression):   csc(1) -> Csc(1),          csc(0.5) -> 2.085829642933488
			Sin: Sine ---------------- sin(Expression):   sin(1) -> Sin(1),          sin(0.5) -> 0.479426
			Sec: Secant -------------- sec(Expression):   sec(1) -> Sec(1),          sec(0.5) -> 1.139493927324549
			Tan: Tangent ------------- tan(Expression):   tan(1) -> Tan(1),          tan(0.5) -> 0.5463024898437905
			Sinh: Hyperbolic Sin ----- sinh(Expression):  sinh(1) -> 1/2*e-1/2*1/e,  sinh(0.5) -> 0.521095
			Cosh: Hyperbolic Cos ----- cosh(Expression):  cosh(1) -> 1/2*e+1/2*1/e,  cosh(0.5) -> 1.12763
			Asinh: Hyperbolic Arcsin - asinh(Expression): asinh(1) -> ln(2^(1/2)+1), asinh(0.5) -> 0.481212
			Acosh: Hyperbolic Arccos - acosh(Expression): acosh(1) -> 0,             acosh(0.5) -> 1.04720*i
	Bivar:
		Gcd: GCD ------- gcd(Expression, Expression): gcd(1, 2) -> 1,         gcd(fact(10), fact(5)) -> 120,     gcd(-fact(10), fact(5)) -> 120
		Lcm: LCM ------- lcm(Expression, Expression): lcm(1, 2) -> 2,         gcd(fact(10), fact(5)) -> 3628800, lcm(-fact(10), fact(5)) -> -3628800
		Log: Logarithm - log(Expression, Expression): log(3, 4) -> Log(3, 4), log(3., 4) -> 0.792481,            log(3.1, 2.7) -> 1.13909
		Mod: Modulus --- mod(Expression, Expression): mod(fact(5), 7) -> 1,   mod(12312.2, 5) -> 2.2,            mod(x, y) -> x-int(x/y)*y
		Solve: Solver -- solve(Equation, Variable):   solve(x=1, x) -> 1,     solve((1+y)*x=z, x) -> z/(y+1),    solve(x^2=1) -> (1,-1)
	Alternate:
		Summation: sum(Expression, Variable, Int, Int):  sum(x^2, x, 1, 5) -> 55,                       sum(x^2*y^x, x, 1, 2) -> 4*y^2+y
		Product:   prod(Expression, Variable, Int, Int): prod(x^2, x, 1, 5) -> 14400,                   prod(x^2*y^x, x, 1, 2) -> 4*y^3
	Builtin: These are not separate classes, but are evalutated at the function call (or earlier).
		Derivative: derivative(Expression, Variable):    derivative(y+4*y^2, x, 1, 2), y) -> 8*y+1, derivative(tan(x), x) -> sec(x)^2
		Evalf:      evalf(Expression, Int):              evalf(8*27+sin(2)) -> 216.909,                 evalf(8*27+sin(2), 10) -> 216.9092975
		Sqrt:       sqrt(Expression):                    sqrt(x) -> x^(1/2),                            sqrt(1.5) -> 1.22474
		IsPrime:    isprime(Int):                        isprime(99) -> false,                          isprime(101) -> true
		Dismantle: Get a look at the inner workings of the program. Takes too much room to describe here, but try it out for yourself!

Extras:
	ans: Call upon the previous result by using "ans" just like any variable: Input: x^2 -> Output: x^2, Input: ans^2 -> x^4
	":=": Variable assignment - a:=2, then a^2 -> 4.

Notes on the function solver:
	1) Capable of expression reduction - sin(x^2+a*x+b^2)^2+1 = 0 -> x^2+a*x+b^2 = arcsin((-1)^(1/2)) -> x^2+a*x+b^2-arcsin((-1)^(1/2)) = 0
	2) Capable of advanced polynomial recognition - x^(1/2)+x+1 -> (x^(1/2))^2+(x^(1/2))^1+1, 2^(2*x+1)+8^(x+1) -> 8*(4^x)^2+2*(4^x)^1
	3) Solves up to cubic equations in exact form once reduced - solve(a*x^3+b*x^2+c*x+d = 0, x)
	4) Multiple solutions to the equations are comma deliminated - solve(x^2=1, x) -> 1,-1
	5) Capable of floating-point root-finding using the secant & newton methods
		a) That is, if no exact form is found, it approximates the answer to a floating point number.
		b) Can handle multiple solutions if the original equation was reduced to a valid polynomial.
		
License:
	CC BY-NC-SA 3.0 (http://creativecommons.org/licenses/by-nc-sa/3.0/)
	You are free to share and remix this work, so long as you attribute the original author, share it under a similar license, and don't use it for commercial purposes.

Author: Mark Canning