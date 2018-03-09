package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	
    	String var = (Character.isLetter(expr.charAt(0))) ? Character.toString(expr.charAt(0)) : "";
    	System.out.println("var here is: " + var);
    	System.out.println("making variable list");
    	for(int i = 1; i < expr.length(); i++) {
    		if(Character.isLetter(expr.charAt(i))){
    			var += Character.toString(expr.charAt(i));
    			System.out.println("inside");
    		}else {   			
    			System.out.println("here");
    			if(var == "") {
    				continue;
    			}    			
	    		if(expr.charAt(i) == '[') {
	    			System.out.println("Adding an Array variable named: " + var);
	        		arrays.add(new Array(var));
	        		var = "";
	        	}else if(Character.isDigit(expr.charAt(i))) {
	        		var += Character.toString(expr.charAt(i));
	        	}else{
	        		vars.add(new Variable(var));
	        		var = "";
	        	}
    			
    		}
    		System.out.println("end. relooping");
    	}
    	System.out.println("var is: " + var);
    	
    	if(var != "") {
    		if(var.contains("[")) {
    			arrays.add(new Array(var.substring(0, var.length() - 1)));
    		}else {
    			vars.add(new Variable(var));
    		}
    	}

    	
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	
    	Stack<String> stack = new Stack<String>();
    	Stack<String> subStack = new Stack<String>();
    	expr = expr.replace(" ", "");
    	
    	
    	StringTokenizer st = new StringTokenizer(expr, "+-*/()[]", true);
    	
    	while(st.hasMoreTokens()) {
    		String next = st.nextToken();

    		if(next.equals(")")){
    			stack.push(next);
    			stack.pop();
    			
    			while(!stack.peek().equals("(")) { //keep popping into substack until reach a (
    				String str = stack.pop();
    				if(vars.contains(str)){
    					str = String.valueOf(retrieveVarVal(str, vars));
    				}
    				subStack.push(str);
    			}
    			stack.pop(); //top of stack must be a (, get rid of it
    			
    			stack.push(evaluateInfix(subStack, vars, arrays));
    			
    			while(subStack.size() != 0) { //reset substack
    				subStack.pop();
    			}
    			
    		}else if(next.equals("]")) {
    			stack.push(next); //push the ] into stack and then get rid of it
    			stack.pop();
    			
    			while(!stack.peek().equals("[")) { //push elements into substack until reach array
    				String stri = stack.pop();
    				if(vars.contains(stri)){
    					stri = String.valueOf(retrieveVarVal(stri, vars));
    				}
    				subStack.push(stri);
    			}
    			System.out.println("peeking into: " + stack.peek());

    			System.out.println("popping: "+stack.pop());
    			System.out.println("peeking into: " + stack.peek());
    			String arr = stack.pop();
    			System.out.println("The variable arr is: " + arr + " and the size of subStack is: " + subStack.size());
    			
    			String a = evaluateInfix(subStack, vars, arrays);
    			float temp = Float.valueOf(a);
    			int index = Math.round(temp);	
    			System.out.println("The index is: " + index);
    			//index = Integer.valueOf(evaluateInfix(subStack, vars, arrays));			
    			stack.push(Float.toString(retrieveArrayVal(arr, index, arrays)));
    			
    			
    			while(subStack.size() != 0) { //reset substack
    				subStack.pop();
    			}
    			
    		}else {
    			stack.push(next);
    		}
    	}
    	
    	System.out.println("The size of the stack is: " + stack.size());
    	System.out.println("The top of the stack is: " + stack.peek());
    	
    	while(subStack.size() != 0) { //reset substack
			subStack.pop();
		}
    	
    	
    	if(stack.size() != 0) { //messy logic
    		while(stack.size() != 0) {
    			subStack.push(stack.pop());
    		}
//    		for(int i = 0; i < subStack.size(); i++) {
//    			System.out.println(subStack.pop());
//    		}
    		float result = Float.valueOf(evaluateInfix(subStack, vars, arrays));
    		//return -999;
    		return result;
    		
    	}
    	
    	//float result = Float.valueOf(evaluateInfix(stack, vars, arrays));
    	
    	
    	
    	//return result;
    	return Float.parseFloat(stack.pop());
    }
    
    private static String evaluateInfix(Stack<String> expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	//The expr Stack is in reverse order. The element you get from pushing initially is the first
    	
    	Stack<Float> numbers = new Stack<Float>();
    	Stack<Character> plusMinusOps = new Stack<Character>();   	
    	Stack<Character> multiDivideOps = new Stack<Character>();
    	 /// 2 + 3 * 4 + 5
    	
    	//push num1
    	System.out.println("numbers size is: " +numbers.size());
    	System.out.println("Evaluating infix. Top of expr stack is: " + expr.peek());
    	while(expr.size() != 0) {
    		//if the item is a number or a variable
    		if(expr.peek().equals("+") || expr.peek().equals("-")){
	    		plusMinusOps.push(expr.pop().charAt(0));
	    	}else if(!expr.peek().equals("*") || !expr.peek().equals("/") || !expr.peek().equals("+") || !expr.peek().equals("-")) {
	    		String var = expr.pop();
	    		System.out.println("Now on variable: " + var );
	    		if(Character.isDigit(var.charAt(0))) { //the string is a number
	    			System.out.println("string is a num, going to push " + Float.valueOf(var) + "into numbers Stack");
	    			numbers.push(Float.valueOf(var)); //push num1
	    		}else{ //the string is a variable
	    			System.out.println("string is a var, going to push " + Float.valueOf(retrieveVarVal(var, vars)) + "into numbers Stack");
	    			float x = Float.valueOf(retrieveVarVal(var, vars));
	    			System.out.println("the infix evaluator retrieved a value of: " + x);
	    			numbers.push(x); //push num1
	    		}
	    		
	    	}
	    		    	
	    	
	    	if(expr.size() != 0) {
	    		System.out.println(expr.peek());
		    	if(expr.peek().equals("*") || expr.peek().equals("/")) {
		    		System.out.println("hello");
		    		multiDivideOps.push(expr.pop().charAt(0));
		    		
		    		String var = expr.pop();
		    		if(Character.isDigit(var.charAt(0))) { //the string is a number
		    			System.out.println("the token," + var + ", is a num");
		    			numbers.push(Float.valueOf(var)); //push num1
		    		}else { //the string is a variable
		    			System.out.println("string is a var");
		    			float x = Float.valueOf(retrieveVarVal(var, vars));
		    			System.out.println("the infix evaluator retrieved a value of: " + x);
		    			numbers.push(x); //push num1
		    		}
		    		
		    		float term2 = numbers.pop();
			    	float term1 = numbers.pop();
		    		char operator = multiDivideOps.pop();
			    	if(operator == '*') { //operator is multiplication
			    		numbers.push(term1 * term2);
			    	}else { //operator is division
			    		numbers.push(term1 / term2);
			    	}
		    	}else if(expr.peek().equals("+") || expr.peek().equals("-")){
		    		plusMinusOps.push(expr.pop().charAt(0));
		    	}
	    	}
    	}
    	
    	Stack<Float> answer = new Stack<Float>();
    	Stack<Character> flippedplusMinusOps = new Stack<Character>();
    	
    	while(plusMinusOps.size() != 0) {
    		flippedplusMinusOps.push(plusMinusOps.pop());
    	}
    	
    	while(numbers.size() != 0) {
    		answer.push(numbers.pop());
    	}
    	
    	while(flippedplusMinusOps.size() != 0) {
    		float term1 = answer.pop();
		    float term2 = answer.pop();
    		char operator = flippedplusMinusOps.pop();
    		
    		if(operator == '+') {
    			System.out.println("Now computing: " + term1 + " plus " + term2);
    	    	answer.push(term1 + term2);
    	    }else {
    	    	System.out.println("Now computing: " + term1 + " minus " + term2);
    	    	answer.push(term1 - term2);
    	    }
    	}
    	float x = answer.pop();
    	System.out.println(x);
    	return Float.toString(x);
    	//return x.toString(); //converts number back to string
    }
    
    private static float retrieveVarVal(String name, ArrayList<Variable> vars) {  	
    	float value = -1;
    	
    	for(int i = 0; i < vars.size(); i++) {
    		if(name.equals(vars.get(i).name)){
    			System.out.println("inside, and this variable's value is: " + vars.get(i).value);
    			value = vars.get(i).value;
    			break;
    		}
    	}  	
    	System.out.println("The var val is " + value);
    	return value;
    }
    
    
	private static float retrieveArrayVal(String name, int index, ArrayList<Array> arrays) {	    	
	    	float value = -1;
	    	
	    	for(int i = 0; i < arrays.size(); i++) {
	    		if(name.equals(arrays.get(i).name)){
	    			value = arrays.get(i).values[index];
	    			System.out.println("Found match and will retrieve array value.");
	    			break;
	    		}
	    	}    	
	    	
	    	return value;
	    }
}