package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		
		Node ptr1 = poly1;
		Node ptr2 = poly2;
		Node result = new Node(0,0,null);
		Node resultPtr = result;
		
		while(ptr1 != null && ptr2 != null) { //make sure that when one ptr reachs end, add rest of other node to the result
			if(ptr1.term.degree == ptr2.term.degree) {				
				resultPtr.next = new Node(ptr1.term.coeff + ptr2.term.coeff, ptr1.term.degree, null);
				resultPtr = resultPtr.next;	
				
				ptr1 = ptr1.next;
				ptr2 = ptr2.next;
			}else if(ptr1.term.degree < ptr2.term.degree) {				
				resultPtr.next = new Node(ptr1.term.coeff, ptr1.term.degree, null);
				resultPtr = resultPtr.next;				
				ptr1 = ptr1.next;
				
			}else if(ptr1.term.degree > ptr2.term.degree) {				
				resultPtr.next = new Node(ptr2.term.coeff, ptr2.term.degree, null);
				resultPtr = resultPtr.next;		
				
				ptr2 = ptr2.next;
			}
		}
;
		if(ptr1 == null && ptr2 != null) {
			do {					
				resultPtr.next = new Node(ptr2.term.coeff, ptr2.term.degree, null);
				resultPtr = resultPtr.next;
				ptr2 = ptr2.next;
				
			}while(ptr2 != null);	
		}else if(ptr1 != null && ptr2 == null) {
			do {					
				resultPtr.next = new Node(ptr1.term.coeff, ptr1.term.degree, null);
				resultPtr = resultPtr.next;
				ptr1 = ptr1.next;

			}while(ptr1 != null);				
		}	
		result = result.next;
		resultPtr = result;
		
		while(true) {
			if(result == null) {
				break;
			}else if(result.term.coeff == 0.0) {
				result = result.next;
				resultPtr = result;
			}else{
				break;
			}
			
		}
		
		result = delete(result, 0);
		
		return result;
	}
	
	private static Node delete(Node front, float target){
		Node ptr = front;
		Node prev = null;
		while(ptr != null && ptr.term.coeff != target){
			prev = ptr;
			ptr = ptr.next;
		}
		if(ptr == null){
			return front;
		}else if(ptr == front){
			return front.next;
		}else{
			prev.next = ptr.next;
			return front;
		}
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		
		if(poly1 == null || poly2 == null) {
			return null;
		}
		
		Node tempPoly = null;
		Node resultPoly = null;
		
		for(Node ptr1 = poly1; ptr1 != null; ptr1 = ptr1.next) {
			System.out.println("li ho");
			for(Node ptr2 = poly2; ptr2 != null; ptr2 = ptr2.next) {
				tempPoly = new Node(ptr1.term.coeff * ptr2.term.coeff, ptr1.term.degree + ptr2.term.degree, null);
				System.out.println("FUCK");
				resultPoly = add(resultPoly, tempPoly);
				System.out.println("henlo");
			}
			
			tempPoly = null;
		}
		return resultPoly;
		
		
	}
	
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		float solution = 0;
		for(Node ptr = poly; ptr != null; ptr = ptr.next) {
			solution += ptr.term.coeff * Math.pow(x, ptr.term.degree);
		}
		return solution;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
