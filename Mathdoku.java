import java.util.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Mathdoku {

	private static int choice = 0;
	private static int dim; // dimension of the matrix
	private static Vector<Piece> problem = null; // a mathdoku problem, consisting of computation pieces
	private static StringBuffer buff = new StringBuffer(); // store the solution
	private static int[][] cand; // the current solution candidate under trying, which is a matrix
	private static States[][] states;
	private static int[] init_vals;

	// one point in matrix
	private static class Point {
		int x;
		int y;

		Point(int a, int b) {
			x = a;
			y = b;
		}
	}

	// one computation piece in mathdoku
	private static class Piece {
		Op op;
		int num;
		ArrayList<Point> points;

		Piece(Op o, int n, ArrayList<Point> p) {
			op = o;
			num = n;
			points = p;
		}

	}

	// five possible operators in one piece
	private enum Op {
		ADD, SUB, MUL, DIV, NOP
	}

	// copy the elements of matrix s to matrix t
	private static void solcopy(int[][] s, int[][] t) {
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++)
				t[i][j] = s[i][j];
		}
	}

	// the status of the point
	private static class States {
		int[] avails; // array recording the available values that can be used by the point
		int[] col_avails; // array recording the used values in the same column of the point
		int[] row_avails; // array recording the used values in the same row of the point
		boolean inited;
	}

	// initiate the status of the whole matrix
	private static void init_states() {
		states = new States[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				states[i][j] = new States();
			}
		}

		init_vals = new int[dim];
		// initialize the default available values
		for (int i = 0; i < dim; i++) {
			init_vals[i] = 1;
		}

	}

	// using the print rules to put one matrix into the StringBuffer
	public static void toBuff(int[][] one) {
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				buff.append(one[i][j]);
			}
			buff.append("\\n");
		}
	}

	// match the solution with its addition
	static boolean match_add(int[][] sol, Piece quest) {
		int sum = 0;
		for (Point p : quest.points) {
			sum += sol[p.x][p.y];
		}
		return (sum == quest.num);
	}

	// match the solution with its subtraction
	static boolean match_sub(int[][] sol, Piece quest) {
		Point p0 = quest.points.get(0);
		Point p1 = quest.points.get(1);

		int v1 = sol[p0.x][p0.y];
		int v2 = sol[p1.x][p1.y];

		return ((v1 - v2) == quest.num) || ((v2 - v1) == quest.num);
	}

	// match the solution with its multiplication
	static boolean match_mul(int[][] sol, Piece quest) {
		int prod = 1;
		for (Point p : quest.points) {
			prod *= sol[p.x][p.y];
		}
		return (prod == quest.num);
	}

	// match the solution with its division
	static boolean match_div(int[][] sol, Piece quest) {
		Point p0 = quest.points.get(0);
		Point p1 = quest.points.get(1);

		int v1 = sol[p0.x][p0.y];
		int v2 = sol[p1.x][p1.y];

		return ((v1 / v2) == quest.num) || ((v2 / v1) == quest.num);
	}

	// match the solution with the equation
	static boolean match_nop(int[][] sol, Piece quest) {
		Point p = quest.points.get(0);
		int v = sol[p.x][p.y];
		return (quest.num == v);
	}

	// match the solution with the corresponding operation request
	static boolean match_piece(int[][] sol, Piece quest) {
		boolean ok = false;
		switch (quest.op) {
		case ADD:
			ok = match_add(sol, quest);
			break;
		case SUB:
			ok = match_sub(sol, quest);
			break;
		case MUL:
			ok = match_mul(sol, quest);
			break;
		case DIV:
			ok = match_div(sol, quest);
			break;
		case NOP:
			ok = match_nop(sol, quest);
			break;
		default:
			assert (false);
		}
		return ok;
	}

	// find the max point in the points arraylist
	public static Point find_max(ArrayList<Point> points) {
		Point max = new Point(0, 0);
		for (Point point : points) {
			if (max.x == point.x) {
				if (max.y < point.y)
					max.y = point.y;
			} else if (max.x < point.x) {
				max.x = point.x;
				max.y = point.y;
			}
		}
		return max;
	}

	// check if the point exists a valid piece of computation
	public static class Problem_at {
		boolean exist;
		Piece piece;

		Problem_at(boolean e, Piece p) {
			exist = e;
			piece = p;
		}
	}

	private static Problem_at[][] matchset;

	// set the pieces for each point
	private static void problem_opt(Vector<Piece> prob) {
		matchset = new Problem_at[dim][dim];
		// initialize the matchset array
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				matchset[i][j] = new Problem_at(false, null);
			}
		}

		for (Piece piece : prob) {
			Point max_point = find_max(piece.points);
			matchset[max_point.x][max_point.y] = new Problem_at(true, piece);
		}
	}

	// loadPuzzle to initialize and store the data that will be used later
	public boolean loadPuzzle(BufferedReader stream) throws IOException {
		dim = 0;
		problem = null;

		// store the puzzle and all the computations
		if (stream.ready()) {
			problem = new Vector<Piece>();
			load(stream);
		}

		// initialize the problems for each point
		if (problem != null) {
			problem_opt(problem);
		}

		// load finished
		
		// initialize the candidates and the status of every point
		cand = new int[dim][dim];
		try {
			init_states();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean load(BufferedReader stream) {
		int result = midLoadPuzzle(stream);
		if (result > 0) {
			return true;
		}
		return false;
	}

	public static Map<Character, ArrayList<Point>> map = new HashMap<Character, ArrayList<Point>>();
	public static Set<Character> letters = new HashSet<Character>();

	public static int midLoadPuzzle(BufferedReader stream) {
		int col = 0;
		int row = 0;
		int last_col = 0;

		Scanner sc = new Scanner(stream);

		// read one row of the matrix
		while (sc.hasNextLine()) {
			String line = sc.nextLine();

			if (line.length() == 0)
				break;
			// System.out.println(line);
			Scanner scline = new Scanner(line);
			Scanner sl = scline.useDelimiter("\\s*");

			// read one column of the row
			while (sl.hasNext()) {
				char c = sl.next().charAt(0);
				letters.add(c);
				ArrayList<Point> points = map.get(c);
				// new the region that has not been read yet
				if (points == null) {
					points = new ArrayList<Point>();
				}
				Point point = new Point(row, col);
				points.add(point);
				map.put(c, points);
				col++;
			}
			sl.close();

			if (last_col != 0)
				assert (last_col == col);
			else
				last_col = col;
			col = 0;
			row++;
			if (last_col == row) {
				break;
			}
			scline.close();
		}

		// System.out.printf("===%dx%d===\n", row, last_col);
		assert (last_col == row);

		// read the region operation input
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			// System.out.println(line);
			Scanner scline = new Scanner(line);
			Scanner sl = scline.useDelimiter("\\s+");

			String a = sl.next();
			assert (a.length() == 1);
			char letter = a.charAt(0);

			int num = Integer.parseInt(sl.next());
			assert (num != 0);

			Op op = null;

			String operator = sl.next();
			assert (operator.length() == 1);
			char opChar = operator.charAt(0);
			switch (opChar) {
			case '+':
				op = Op.ADD;
				break;
			case '-':
				op = Op.SUB;
				break;
			case '*':
				op = Op.MUL;
				break;
			case '/':
				op = Op.DIV;
				break;
			case '=':
				op = Op.NOP;
				break;
			default:
				assert (false);
			}

			assert (op != null);
			ArrayList<Point> points = map.get(letter);
			assert (points != null);

			Piece p = new Piece(op, num, points);

			scline.close();
			problem.add(p);
		}
		sc.close();
		dim = row;
		return row;
	}

	// check if the input is ready to solve
	public boolean readyToSolve() {
		return toSolve();
	}

	// check for the validity of file path
	public boolean toSolve() {
		BufferedReader stream = null;
		try {
			stream = new BufferedReader(new FileReader(MainUI.filePath));
			return check(stream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Set<Character> lettersOp = new HashSet<Character>();
	private static Scanner sc;

	// check the whole input structure
	public static boolean check(BufferedReader stream) {
		sc = new Scanner(stream);
		int i = 0;
		int lineSize = 0;
		while (sc.hasNextLine()) {
			String line = sc.nextLine().trim();
			if (i == 0)
				lineSize = line.length();
			i++;
			if (i <= lineSize) {
				if (!check1(line)) {
					return false;
				}
			} else {
				if (!check2(line)) {
					return false;
				}
			}
		}
		sc.close();
		// letters appeared in the first part should also appear in the second part
		return letters.equals(lettersOp);
	}

	// check if the first part only consists letters
	private static boolean check1(String line) {
		return line.matches("[a-zA-Z]+");
	}

	// check if the second part is valid operations
	private static boolean check2(String line) {
		String[] newLine = line.trim().split(" ");
		// each line contains three parts
		if (newLine.length != 3) {
			return false;
		}
		String one = newLine[0];
		char letter = one.charAt(0);
		lettersOp.add(letter);
		// the first part should only be letters
		if (!one.matches("[a-zA-Z]+")) {
			return false;
		}
		// the second part should only be numbers
		String two = newLine[1];
		if (!two.matches("[0-9]+")) {
			return false;
		}

		// the third part should only be within the five operators
		String three = newLine[2];
		if (!three.equals("/") && !three.equals("*") && !three.equals("-") && !three.equals("+")
				&& !three.equals("=")) {
			return false;
		}

		ArrayList<Point> points = map.get(letter);

		// if one letter only covers a single piece
		// the corresponding operator should only be '='
		if (!three.equals("=") && points.size() == 1) {
			return false;
		}
		return true;
	}

	
	// solve the matrix from the first point
	public boolean solve() {
		return solve(0, 0);
	}

	// the core algorithms to solve the matrix using recursions
	public boolean solve(int s, int t) {

		// only initialize when compute the same point first time
		if (states[s][t].inited == false) {
			states[s][t].avails = new int[dim];
			states[s][t].col_avails = new int[dim];
			states[s][t].row_avails = new int[dim];
			states[s][t].inited = true;
		}

		int[] avail = states[s][t].avails;
		int[] col_avail = states[s][t].col_avails;
		int[] row_avail = states[s][t].row_avails;
		int[] last_col_avail, last_row_avail;

		// all values are available by default
		last_col_avail = last_row_avail = init_vals;

		// if not in the first row
		if (s != 0)
			// inherit col_avails from point (last_row, same_col)
			last_col_avail = states[s - 1][t].col_avails;
		// if not in the first column
		if (t != 0)
			// inherit row_avails from point of (same_row, last_col)
			last_row_avail = states[s][t - 1].row_avails;

		// if the values are available to both column and row directions
		// then they are available to point (s, t)
		for (int k = 0; k < dim; k++) {
			// save value in last compute of the same point
			avail[k] = 0;
			if (last_row_avail[k] == 1 && last_col_avail[k] == 1) {
				avail[k] = 1;
			}

			// inherit the availability
			col_avail[k] = last_col_avail[k];
			row_avail[k] = last_row_avail[k];
		}

		for (int i = 0; i < dim; i++) {
			if (avail[i] == 0)
				continue;
			// fill point(s,t) with a valid value i
			cand[s][t] = i + 1;

			// check if the candidate can match the operation request
			if (problem != null && matchset[s][t].exist) {
				boolean ok = match_piece(cand, matchset[s][t].piece);
				// the candidate cannot match, give up this value
				// count the choice times
				if (!ok) {
					cand[s][t] = 0;
					choice++;
					continue;
				}
			}

			// mark value i as unavailable
			col_avail[i] = 0;
			row_avail[i] = 0;

			// (s,t) is in the last column but not last row
			// continue to solve the point in next row
			if (t + 1 == dim && s + 1 != dim) {
				solve(s + 1, 0);
			}
			// (s,t) is in the last column
			// continue to solve the point in next column
			else if (t + 1 != dim) {
				solve(s, t + 1);
			}
			// (s,t) is in last column and last row
			// finish this solution searching
			else {
				int[][] sol = new int[dim][dim];
				// copy the solution
				solcopy(cand, sol);

				// add the solution to buff
				toBuff(sol);
			}
			// restore the candidate
			cand[s][t] = 0;
			// restore the available value used by last iteration
			col_avail[i] = 1;
			row_avail[i] = 1;
		}
		if (buff.length() == 0) {
			return false;
		}
		return true;
	}

	// print the string of solution
	public String print() {
		return buff.toString();
	}

	public int choices() {
		return choice;
	}
}