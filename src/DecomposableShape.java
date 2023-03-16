/*
 * Description: The program below produce abstract polygon from an original image when points are algorithmically removed using circular doubly linked list from the outline and are "decomposed" and also have the ability to return to an original image that was provided before decomposing them with the help of stack. 
 */

import java.util.Scanner;
import java.awt.Polygon;

public class DecomposableShape {
	
	private int initPoint; // number of points initially in the shape
	private int currPoint; // number of points currently in the shape
	private PointNode front; // PointNode reference to the first node in the shape named front
	private ArrayStack<PointNode> stack; // stack of nodes

	/** Construct a DecomposableShape Object with nested PoinNode static class
	 * 
	 * @param input - data files which access x and y coordinates from each line of the file
	 * 				 
	 */
	public DecomposableShape(Scanner input) {
		makingNode(input);
		currPoint = initPoint;
		importanace();
		stack = new ArrayStack<>();
	}

	/** Makes a node by reading the file from Scanner input which is connected with a file and access x and y coordinates.
	 * 
	 * @param input- The parameter input accessed the the x and y coordinates from each line of the file
	 */
	public void makingNode(Scanner input) {
		String line = input.nextLine();
		String[] data = line.split(",+");
		int x = Integer.parseInt(data[0]);
		int y = Integer.parseInt(data[1]);
		front = new PointNode(x, y);
		initPoint = 1;
		PointNode current = front;
		while (input.hasNextLine()) {
			line = input.nextLine();
			data = line.split(",+");
			x = Integer.parseInt(data[0]);
			y = Integer.parseInt(data[1]);

			current.next = new PointNode(x, y);
			initPoint++;
			current.next.prev = current;
			current = current.next;
		}
		front.prev = current;
		current.next = front;
	}

	/** Calculates the importance of the PointNode
	 * 
	 */
	public void importanace() {
		PointNode current = front;
		for (int i = 0; i < currPoint; i++) {
			current.calculateImportance();
			current = current.next;
		}
	}
	
	/** Creates a Polygon shape using toPolygon method
	 * 
	 * @return Polygon shape using toPolygon method
	 */
	public Polygon toPolygon() {
		
		int[] xPoints = new int[currPoint];
		int[] yPoints = new int[currPoint];
		int nPoint = currPoint;
		PointNode current = front;
		for (int i = 0; i < currPoint; i++) {
			xPoints[i] = current.x;
			yPoints[i] = current.y;
			current = current.next;
		}
		Polygon polygon = new Polygon(xPoints, yPoints, nPoint);
		return polygon;
	}
	
	/** Resize the shape of the image to the given target and make a polygon figure.
	 * 
	 * @param Target- the percent of the initial number of points 
	 */
	public void setSizeTo(int target) {
		int pointValue = (initPoint * target) / 100;
		int difference = currPoint - pointValue;
		if (difference > 0) {
			removeMinNodes(difference);
		} else {

			addRemovedNodes(difference);
		}
	}

	/** Removes the least important node from PointNode and move it to the stack to preserve it
	 * 
	 * @param difference - difference of pointValue of point with current number of points
	 */
	public void removeMinNodes(int difference) {
		for (int i = 0; i < difference; i++) {
			PointNode minNode = front;
			Double min = front.importance;
			PointNode current = front;
			for (int j = 0; j < currPoint; j++) {
				if (current.importance < min) {
					min = current.importance;
					minNode = current;
				}
				current = current.next;
			}

			if (front == minNode) {
				front = front.next;
			}

			minNode.next.prev = minNode.prev;
			minNode.prev.next = minNode.next;
			currPoint--;
			minNode.next.calculateImportance();
			minNode.prev.calculateImportance();
			stack.push(minNode);
		}
	}

	/** Add the Removed Nodes back to the to PointNode to the original size of the image.
	 * 
	 * @param difference - difference of pointValue of point with current number of points
	 */
	public void addRemovedNodes(int difference) {
		difference = difference * (-1);
		for (int a = 0; a < difference; a++) {
			PointNode temp = stack.pop();
			temp.prev.next = temp;
			temp.next.prev = temp;

			temp.next.calculateImportance();
			temp.prev.calculateImportance();
			currPoint++;
		}
	}

	/**
	 * Returns a string traversing list forward using next fields.
	 * 
	 * @return a string traversing list forward using next fields.
	 */
	public String toString() {
		String sms = "";
		PointNode current = front;
		for (int i = 0; i < currPoint; i++) {
			sms += "\n " + current.toString();
			current = current.next;
		}
		return sms;
	}

	/**	A nested static PointNode class that calculated the importance of x and y coordinated of previous and next point with another two point.
	 * 
	 * @author Ad
	 *
	 */
	private static class PointNode {
		
		private int x; // x coordinate of a point
		private int y; // y coordinate of a point
		private double importance; // importance of the point
		private PointNode prev; // reference to the previous node
		private PointNode next; // reference to the next node

		/**
		 * Creates a PoinNode object with x and y coordinate.
		 * @param x - x coordinate of PointNode
		 * @param y - y coordinate of PointNode
		 */
		public PointNode(int x, int y) {
			this.x = x;
			this.y = y;
			this.importance = 0;
			this.prev = null;
			this.next = null;
		}

		/** Calculates the importance of the previous and next points with another two points using distance formula.
		 * 
		 */
		public void calculateImportance() {

			double previousPoint = Math.sqrt((prev.x - x) * (prev.x - x) + (prev.y - y) * (prev.y - y));
			double nextPoint = Math.sqrt((x - next.x) * (x - next.x) + (y - next.y) * (y - next.y));
			double twoPoint = Math.sqrt((prev.x - next.x) * (prev.x - next.x) + (prev.y - next.y) * (prev.y - next.y));
			importance = previousPoint + nextPoint - twoPoint;
		}

		/**
		 * Returns a string description of x coordinate, y coordinate and the importance.
		 * 
		 * @return a string description of x coordinate, y coordinate and the importance.
		 */
		public String toString() {
			String msg = "";
			msg += "x = " + this.x;
			msg += ", y = " + this.y;
			msg += ", importance = " + this.importance;
			return msg;
		}
	}
}
