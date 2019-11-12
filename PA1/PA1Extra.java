import java.io.*;
import java.util.*;

public class PA1Extra{
  
  // front, left, right, up, down and back faces
  // front face: a b c d
  // left face: e f g h
  // right face: i j k l
  // up face: m n o p
  // down face: q r s t
  // back face: u v w x
  private static final char[][] goalState = {{'a','b','c','d'}, {'e','f','g','h'}, {'i','j','k','l'}, {'m','n','o','p'}, {'q','r','s','t'}, {'u','v','w','x'}};
  // w = white; b = brown; r = red; g = green; y = yellow; o = orange
  private static final char[][] goalColor = {{'w','w','w','w'},{'b','b','b','b'},{'r','r','r','r'},{'g','g','g','g'},{'y','y','y','y'},{'o','o','o','o'}};
  private static final int[][][] coordinates = {{{3,1,2},{3,2,2},{3,1,1},{3,2,1}},{{1,0,2},{2,0,2},{1,0,1},{2,0,1}},{{2,3,2},{1,3,2},{2,3,1},{1,3,1}},{{1,1,3},{1,2,3},{2,1,3},{2,2,3}},{{2,1,0},{2,2,0},{1,1,0},{1,2,0}},{{0,1,1},{0,2,1},{0,2,2},{0,1,2}}};
  //private static final char[][] edges = {{'b','n'},{'b','r'},{'b','j'},{'b','f'},{'b','a'},{'b','d'},{}}
  
  // R = rotate right face clockwise
  // r = rotate right face counterclockwise
  // D = rotate the down face clockwise
  // d = rotate the down face counterclockwise
  // F = rotate the front face clockwise
  // f = rotate the front face counterclockwise
  private static final String[] moveDirections = {"R", "r", "D", "d", "F", "f"};
  
  private static char[][] currentState;
  private static PriorityQueue<Tile> queue;
  private static int maxLimit = 100000;
  private static Random r;
  private static int node;
  
  // ex. format for command: "abcd efgh ijkl mnop qrst uvwx"
  public static void setState(String command){
    //System.out.println("setState " + command);
    String[] parseState = command.split(" ");
    for(int i = 0;i < 6;i++){
      for(int j = 0;j < 4;j++){
        currentState[i][j] = parseState[i].charAt(j);
      }
    }
  }
  
  public static void setState(char[][] state, String command){
    String[] parseState = command.split(" ");
    for(int i = 0;i < 6;i++){
      for(int j = 0;j < 4;j++){
        state[i][j] = parseState[i].charAt(j);
      }
    }
  }
  
  public static void printState(){
    for(int i = 0;i < currentState.length;i++){
      for(int j = 0;j < currentState[i].length;j++){
        if(j == 2) System.out.println();
        System.out.print(currentState[i][j] + "\t");
      }
      System.out.println("\n");
    }
  }
  
  public static void printState(char[][] state){
    for(int i = 0;i < state.length;i++){
      for(int j = 0;j < state[i].length;j++){
        if(j == 2) System.out.println();
        System.out.print(state[i][j] + "\t");
      }
      System.out.println("\n");
    }
  }
  
  // make N random moves from the goal state
  public static void randomizeState(int N){
    currentState = myClone(goalState);
    for(int i = 0;i < N;i++){
      int direction = r.nextInt(6);
      move(moveDirections[direction]);
    }
  }
  
  public static void move(String moveDirection){
    move(currentState, moveDirection);
  }
  
  public static void move(char[][] state, String moveDirection){
    String command = "";
    int direction = 0;
    if(moveDirection.equals(moveDirections[0])) direction = 0;
    else if(moveDirection.equals(moveDirections[1])) direction = 1;
    else if(moveDirection.equals(moveDirections[2])) direction = 2;
    else if(moveDirection.equals(moveDirections[3])) direction = 3;
    else if(moveDirection.equals(moveDirections[4])) direction = 4;
    else
      direction = 5;
    //System.out.println("direction: " + direction);
    if(direction == 0){
      command = command + rotate(state, 0, 4, 0);
      command = command + rotate(state, 1, 1, 0);
      command = command + rotate(state, 2, 2, 0);
      command = command + rotate(state, 3, 0, 0);
      command = command + rotate(state, 4, 5, 0);
      command = command + rotate(state, 5, 3, 0);
      //System.out.println("command: " + command);
    }
    else if(direction == 1){
      command = command + rotate(state, 0, 3, 1);
      command = command + rotate(state, 1, 1, 1);
      command = command + rotate(state, 2, 2, 1);
      command = command + rotate(state, 3, 5, 1);
      command = command + rotate(state, 4, 0, 1);
      command = command + rotate(state, 5, 4, 1);
      //System.out.println("command: " + command);
    }
    else if(direction == 2){
      command = command + rotate(state, 0, 1, 2);
      command = command + rotate(state, 1, 5, 2);
      command = command + rotate(state, 2, 0, 2);
      command = command + rotate(state, 3, 3, 2);
      command = command + rotate(state, 4, 4, 2);
      command = command + rotate(state, 5, 2, 2);
      //System.out.println("command: " + command);
    }
    else if(direction == 3){
      command = command + rotate(state, 0, 2, 3);
      command = command + rotate(state, 1, 0, 3);
      command = command + rotate(state, 2, 5, 3);
      command = command + rotate(state, 3, 3, 3);
      command = command + rotate(state, 4, 4, 3);
      command = command + rotate(state, 5, 1, 3);
      //System.out.println("command: " + command);
    }
    else if(direction == 4){
      command = command + rotate(state, 0, 0, 4);
      command = command + rotate(state, 1, 4, 4);
      command = command + rotate(state, 2, 3, 4);
      command = command + rotate(state, 3, 1, 4);
      command = command + rotate(state, 4, 2, 4);
      command = command + rotate(state, 5, 5, 4);
      //System.out.println("command: " + command);
    }
    else if(direction == 5){
      command = command + rotate(state, 0, 0, 5);
      command = command + rotate(state, 1, 3, 5);
      command = command + rotate(state, 2, 4, 5);
      command = command + rotate(state, 3, 2, 5);
      command = command + rotate(state, 4, 1, 5);
      command = command + rotate(state, 5, 5, 5);
      //System.out.println("command: " + command);
    }
    setState(state, command);
  }
  
  // solve the given puzzle by A* search
  public static void solveAstar(String heuristic){
    //System.out.println("solve A star " + heuristic);
    if(isFinished(currentState)){
      System.out.println("Number of tile moves 0");
    }
    while(!isFinished(currentState) && node <= maxLimit){
      Tile nodeptr = queue.poll();
      currentState = myClone(nodeptr.state);
      if(isFinished(currentState)){
        System.out.println("Number of tile moves " + nodeptr.g);
        printMoveSequences(nodeptr);
      }
      else{
        char[][] newState1 = myClone(currentState);
        move(newState1, moveDirections[0]);
        if(heuristic.equals("h1")) queue.add(new Tile(nodeptr, newState1, nodeptr.g + 1, findHFirst(newState1), 0));
        else
          queue.add(new Tile(nodeptr, newState1, nodeptr.g + 1, findHSecond(newState1), 0));
        node++;
        
        char[][] newState2 = myClone(currentState);
        move(newState2, moveDirections[1]);
        if(heuristic.equals("h1")) queue.add(new Tile(nodeptr, newState2, nodeptr.g + 1, findHFirst(newState2), 1));
        else
          queue.add(new Tile(nodeptr, newState2, nodeptr.g + 1, findHSecond(newState2), 1));
        node++;
        
        char[][] newState3 = myClone(currentState);
        move(newState3, moveDirections[2]);
        if(heuristic.equals("h1")) queue.add(new Tile(nodeptr, newState3, nodeptr.g + 1, findHFirst(newState3), 2));
        else
          queue.add(new Tile(nodeptr, newState3, nodeptr.g + 1, findHSecond(newState3), 2));
        node++;
        
        char[][] newState4 = myClone(currentState);
        move(newState4, moveDirections[3]);
        if(heuristic.equals("h1")) queue.add(new Tile(nodeptr, newState4, nodeptr.g + 1, findHFirst(newState4), 3));
        else
          queue.add(new Tile(nodeptr, newState4, nodeptr.g + 1, findHSecond(newState4), 3));
        node++;
        
        char[][] newState5 = myClone(currentState);
        move(newState5, moveDirections[4]);
        if(heuristic.equals("h1")) queue.add(new Tile(nodeptr, newState5, nodeptr.g + 1, findHFirst(newState5), 4));
        else
          queue.add(new Tile(nodeptr, newState5, nodeptr.g + 1, findHSecond(newState5), 4));
        node++;
        
        char[][] newState6 = myClone(currentState);
        move(newState6, moveDirections[5]);
        if(heuristic.equals("h1")) queue.add(new Tile(nodeptr, newState6, nodeptr.g + 1, findHFirst(newState6), 5));
        else
          queue.add(new Tile(nodeptr, newState6, nodeptr.g + 1, findHSecond(newState6), 5));
        node++;
      }
    }
    if(node > maxLimit) System.out.println("Unsolvable! Exceed max nodes!");
  }
  
  // h1 = the number of misplaced tiles
  public static int findHFirst(char[][] state){
    int misplace = 0;
    for(int i = 0;i < 6;i++){
      for(int j = 0;j < 4;j++){
        if(state[i][j] != goalState[i][j]) misplace++;
      }
    }
    return misplace;
  }
  
  // h2 = the sum of the distances of the tiles from their goal positions
  public static int findHSecond(char[][] state){
    int  manhattan = 0;
    for(int i = 0;i < 6;i++){
      for(int j = 0;j < 4;j++){
        int[] positionN = findElement(state, state[i][j]);
        int[] positionG = findElement(goalState, state[i][j]);
        int[] coordinateN = coordinates[positionN[0]][positionN[1]];
        int[] coordinateG = coordinates[positionG[0]][positionG[1]];
        manhattan += (Math.abs(coordinateN[0]-coordinateG[0])+Math.abs(coordinateN[1]-coordinateG[1])+Math.abs(coordinateN[2]-coordinateG[2]));
      }
    }
    return manhattan;
  }
  
  
  
  // solve the given puzzle by beam search 
  public static void solveBeam(int K){
    //System.out.println("solve beam " + K);
    boolean end = false;
    while((!end) && node <= maxLimit){
      PriorityQueue<Tile> temp = new PriorityQueue<Tile>(cmp);
      for(int i = 0;i < K && queue.size() != 0 && node <= maxLimit;i++){
        Tile nodeptr = queue.poll();
        end = isFinished(nodeptr.state);
        if(end){
          System.out.println("Number of tile moves " + nodeptr.g);
          printMoveSequences(nodeptr);
          break;
        }
        else{ 
          char[][] newState1 = myClone(nodeptr.state);
          move(newState1, moveDirections[0]);
          temp.add(new Tile(nodeptr, newState1, nodeptr.g+1, findHSecond(newState1), 0));
          node++;
          
          char[][] newState2 = myClone(nodeptr.state);
          move(newState2, moveDirections[1]);
          temp.add(new Tile(nodeptr, newState2, nodeptr.g+1, findHSecond(newState2), 1));
          node++;
          
          char[][] newState3 = myClone(nodeptr.state);
          move(newState3, moveDirections[2]);
          temp.add(new Tile(nodeptr, newState3, nodeptr.g+1, findHSecond(newState3), 2));
          node++;
          
          char[][] newState4 = myClone(nodeptr.state);
          move(newState4, moveDirections[3]);
          temp.add(new Tile(nodeptr, newState4, nodeptr.g+1, findHSecond(newState4), 3));
          node++;
          
          char[][] newState5 = myClone(nodeptr.state);
          move(newState5, moveDirections[4]);
          temp.add(new Tile(nodeptr, newState5, nodeptr.g+1, findHSecond(newState5), 4));
          node++;
          
          char[][] newState6 = myClone(nodeptr.state);
          move(newState6, moveDirections[5]);
          temp.add(new Tile(nodeptr, newState6, nodeptr.g+1, findHSecond(newState6), 5));
          node++;
        }
      }
      if(!end){
        for(int j = 0;j < K && temp.size() != 0;j++){
          queue.add(temp.poll());
        }
      }
    }
    if(node > maxLimit) System.out.println("Unsolvable! Exceed max nodes!");
  }
  
  public static void printMoveSequences(Tile nodeptr){
    if(nodeptr.parent == null){
      System.out.println("Start state: ");
      printState(nodeptr.state);
    }
    else{
      printMoveSequences(nodeptr.parent);
      System.out.println("move " + moveDirections[nodeptr.direction]);
      printState(nodeptr.state);
    }
  }
  
  public static void maxNodes(int n){
    maxLimit = n;
  }
  
  /**
   * helper methods below
   **/
  private static Comparator<Tile> cmp = new Comparator<Tile>(){
    public int compare(Tile nodeptr1, Tile nodeptr2){
      int f1 = nodeptr1.g + nodeptr1.h;
      int f2 = nodeptr2.g + nodeptr2.h;
      return f1 - f2;
    }
  };
  
  private static int[] findElement(char[][] state, char element){
    int[] result = new int[2];
    for(int i = 0;i < 3;i++){
      for(int j = 0;j < 3;j++){
        if(state[i][j] == element){
          result = coordinates[i][j];
          return result;
        }
      }
    }
    return result;
  }
  
  private static String rotate(char[][] state, int now, int change, int direction){
    StringBuilder builder = new StringBuilder();
    if(direction == 0 || direction == 1){
      if(now == 2){
        if(direction == 0){
          builder.append(state[change][2]);
          builder.append(state[change][0]);
          builder.append(state[change][3]);
          builder.append(state[change][1]);
          builder.append(' ');
        }
        else{
          builder.append(state[change][1]);
          builder.append(state[change][3]);
          builder.append(state[change][0]);
          builder.append(state[change][2]);
          builder.append(' ');
        }
      }
      else{
        for(int j = 0;j < state[now].length;j++){
          if(j % 2 == 1) builder.append(state[change][j]);
          else
            builder.append(state[now][j]);
        }
        builder.append(' ');
      }
    }
    else if(direction == 2 || direction == 3){
      if(now == 4){
        if(direction == 2){
          builder.append(state[change][2]);
          builder.append(state[change][0]);
          builder.append(state[change][3]);
          builder.append(state[change][1]);
          builder.append(' ');
          //result = "sqtr ";
        }
        else{
          builder.append(state[change][1]);
          builder.append(state[change][3]);
          builder.append(state[change][0]);
          builder.append(state[change][2]);
          builder.append(' ');
          //result = "rtqs ";
        }
      }
      else if(direction == 2 && now == 1){
        builder.append(state[now][0]);
        builder.append(state[now][1]);
        builder.append(state[change][1]);
        builder.append(state[change][0]);
        builder.append(' ');
      }
      else if(direction == 2 && now == 5){
        builder.append(state[change][3]);
        builder.append(state[change][2]);
        builder.append(state[now][2]);
        builder.append(state[now][3]);
        builder.append(' ');
      }
      else if(direction == 3 && now == 2){
        builder.append(state[now][0]);
        builder.append(state[now][1]);
        builder.append(state[change][1]);
        builder.append(state[change][0]);
        builder.append(' ');
      }
      else if(direction == 3 && now == 5){
        builder.append(state[change][3]);
        builder.append(state[change][2]);
        builder.append(state[now][2]);
        builder.append(state[now][3]);
        builder.append(' '); 
      }
      else{
        for(int j = 0;j < state[now].length;j++){
          if(j > 1) builder.append(state[change][j]);
          else
            builder.append(state[now][j]);
        }
        builder.append(' ');
      }
    }
    else if(direction == 4){
      if(now == 0){
        builder.append(state[change][2]);
        builder.append(state[change][0]);
        builder.append(state[change][3]);
        builder.append(state[change][1]);
        builder.append(' ');
      }
      else if(now == 1){
        builder.append(state[now][0]);
        builder.append(state[change][0]);
        builder.append(state[now][2]);
        builder.append(state[change][1]);
        builder.append(' ');
      }
      else if(now == 2){
        builder.append(state[change][2]);
        builder.append(state[now][1]);
        builder.append(state[change][3]);
        builder.append(state[now][3]);
        builder.append(' ');
      }
      else if(now == 3){
        builder.append(state[now][0]);
        builder.append(state[now][1]);
        builder.append(state[change][3]);
        builder.append(state[change][1]);
        builder.append(' ');
      }
      else if(now == 4){
        builder.append(state[change][2]);
        builder.append(state[change][0]);
        builder.append(state[now][2]);
        builder.append(state[now][3]);
        builder.append(' ');
      }
      else{
        builder.append(state[change][0]);
        builder.append(state[change][1]);
        builder.append(state[change][2]);
        builder.append(state[change][3]);
        builder.append(' ');
      }
      
//      if(now == 0) result = "cadb ";
//      else if(now == 1) result = "eqgr ";
//      else if(now == 2) result = "ojpl ";
//      else if(now == 3) result = "mnhf ";
//      else if(now == 4) result = "kist ";
//      else
//        result = "uvwx ";
//      return result;
    }
    else{
      if(now == 0){
        builder.append(state[change][1]);
        builder.append(state[change][3]);
        builder.append(state[change][0]);
        builder.append(state[change][2]);
        builder.append(' ');
      }
      else if(now == 1){
        builder.append(state[now][0]);
        builder.append(state[change][3]);
        builder.append(state[now][2]);
        builder.append(state[change][2]);
        builder.append(' ');
      }
      else if(now == 2){
        builder.append(state[change][1]);
        builder.append(state[now][1]);
        builder.append(state[change][0]);
        builder.append(state[now][3]);
        builder.append(' ');
      }
      else if(now == 3){
        builder.append(state[now][0]);
        builder.append(state[now][1]);
        builder.append(state[change][0]);
        builder.append(state[change][2]);
        builder.append(' ');
      }
      else if(now == 4){
        builder.append(state[change][1]);
        builder.append(state[change][3]);
        builder.append(state[now][2]);
        builder.append(state[now][3]);
        builder.append(' ');
      }
      else{
        builder.append(state[change][0]);
        builder.append(state[change][1]);
        builder.append(state[change][2]);
        builder.append(state[change][3]);
        builder.append(' ');
      }
//      if(now == 0) result = "bdac ";
//      else if(now == 1) result = "epgo ";
//      else if(now == 2) result = "rjql ";
//      else if(now == 3) result = "mnik ";
//      else if(now == 4) result = "fhst ";
//      else
//        result = "uvwx ";
//      return result;
    }
    return builder.toString();
  }
  
  private static boolean isFinished(char[][] state){
    for(int i = 0;i < 5;i++){
      for(int j = 0;j < 2;j++){
        int[] position1 = findElement(goalState, state[i][j]);
        int[] position2 = findElement(goalState, state[i+1][j+1]);
        if(goalColor[position1[0]][position1[1]] != goalColor[position2[0]][position2[1]]) return false;
      }
    }
    return true;
  }
  
  private static char[][] myClone(char[][] original){
    char[][] clone = new char[original.length][original[0].length];
    for(int i = 0;i < original.length;i++){
      for(int j = 0;j < original[i].length;j++){
        clone[i][j] = original[i][j];
      }
    }
    return clone;
  }
  /**
   * helper class
   **/
  private static class Tile{
    private Tile parent;
    private char[][] state;
    private int g;
    private int h;
    private int direction;
    
    public Tile(Tile parent, char[][] state, int g, int h, int direction){
      this.parent = parent;
      this.state = state;
      this.g = g;
      this.h = h;
      this.direction = direction;
    }
  }
  
  private static class MyException extends Exception{ 
    public MyException(String s) 
    { 
      // Call constructor of parent Exception 
      super(s); 
    } 
  }
  
  // main function
  // invoke format: run PA1 inputFile
  public static void main(String[] args) throws IOException{
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    String line = "";
    PA1 p = new PA1();
    currentState = myClone(goalState);
    while ((line = br.readLine()) != null) {
      System.out.println(line);
      if(line.charAt(0) == 's' && line.charAt(1) == 'e'){
        String[] temp = line.split(" ", 2);
        setState(temp[1]);
      }
      else{
        String[] temp = line.split(" ");
        if(temp[0].equals("printState")) printState();
        else if(temp[0].equals("move")) move(temp[1]);
        else if(temp[0].equals("randomizeState")){
          r = new Random();
          r.setSeed(2);
          randomizeState(Integer.parseInt(temp[1]));
        }
        else if(temp[0].equals("solve")){
          long startTime = System.currentTimeMillis();
          queue = new PriorityQueue<Tile>(cmp);
          node = 1;
          if(temp[1].equals("A-star")){
            if(temp[2].equals("h1")) queue.add(new Tile(null, currentState, 0, findHFirst(currentState), -1));
            else
              queue.add(new Tile(null, currentState, 0, findHSecond(currentState), -1));
            solveAstar(temp[2]);
            System.out.println();
            System.out.println("Total nodes generated for " + temp[0] + " " + temp[1] + " " + node);
            maxNodes(10000);
          }
          else{
            queue = new PriorityQueue<Tile>(cmp);
            queue.add(new Tile(null, currentState, 0, findHSecond(currentState), -1));
            solveBeam(Integer.parseInt(temp[2]));
            System.out.println();
            System.out.println("Total nodes generated for " + temp[0] + " " + temp[1] + " " + node);
            maxNodes(10000);
          }
          long endTime = System.currentTimeMillis();
          System.out.println("Total Time for " + temp[0] + " " + temp[1] + " " + (endTime - startTime) + "ms");
          System.out.println();
        }
        else if(temp[0].equals("maxNodes"))
          maxNodes(Integer.parseInt(temp[1]));
        else{
          System.out.println("There is no such a method with the argument: " + line);
        }
      }
    }
  }
}