import java.io.*;
import java.util.*;

public class PA1{
  
  private static final int[][] goalState = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
  private static final String[] moveDirections = {"up", "down", "left", "right"};
  
  private static int[][] currentState = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
  private static PriorityQueue<Tile> queue;
  private static int maxLimit = 100000;
  private static int node;
  private static Random r;
  
  // All indicate the number of solved cases during these searches
  private static int h1Solved = 0;
  private static int h2Solved = 0;
  private static int beamSolved = 0;
  
  // set the currentState
  public static void setState(String command){
    //System.out.println("setState " + command);
    String[] parseState = command.split(" ");
    for(int i = 0;i < parseState.length;i++){
      for(int j = 0;j < parseState[i].length();j++){
        if(parseState[i].charAt(j) == 'b') currentState[i][j] = 0;
        else
          currentState[i][j] = parseState[i].charAt(j) - '0';
      }
    }
  }
  
  // set the specific state
  public static void setState(int[][] state, String command){
    String[] parseState = command.split(" ");
    for(int i = 0;i < parseState.length;i++){
      for(int j = 0;j < parseState[i].length();j++){
        if(parseState[i].charAt(j) == 'b') state[i][j] = 0;
        else
          state[i][j] = parseState[i].charAt(j) - '0';
      }
    }
  }
  
  // print the currentState
  public static void printState(){
    //System.out.println("printState");
    for(int i = 0;i < currentState.length;i++){
      for(int j = 0;j < currentState[i].length;j++){
        System.out.print(currentState[i][j] + "\t");
      }
      System.out.print("\n");
    }
    System.out.println();
  }
  
  // print the specific state
  public static void printState(int[][] state){
    for(int i = 0;i < state.length;i++){
      for(int j = 0;j < state[i].length;j++){
        System.out.print(state[i][j] + "\t");
      }
      System.out.print("\n");
    }
    System.out.println();
  }
  
  // make N random moves from the goal state
  public static void randomizeState(int N){
    //System.out.println("randomizeState: " + N);
    currentState = myClone(goalState);
    for(int i = 0;i < N;i++){
      int direction = r.nextInt(4);
      if(!isAvailable(currentState, direction)) i--;
      else{
        move(moveDirections[direction]);
      }
    }
  }
  
  // move the blank tile 'up', 'down', 'left', or 'right' in the currentState
  public static void move(String moveDirection){
    //System.out.println("move " + moveDirection);
    int direction = 0;
    if(moveDirection.equals(moveDirections[0])) direction = 0;
    else if(moveDirection.equals(moveDirections[1])) direction = 1;
    else if(moveDirection.equals(moveDirections[2])) direction = 2;
    else
      direction = 3;
    int[] position = findBlank(currentState);
    int[] changed = new int[2];
    changed[0] = position[0] + (3-direction)/2 * (int)Math.pow(-1,direction+1);
    changed[1] = position[1] + direction/2 * (int)Math.pow(-1, direction+1);
    StringBuilder builder = new StringBuilder();
    for(int i = 0;i < 3;i++){
      for(int j = 0;j < 3;j++){
        if(i == changed[0] && j == changed[1]) builder.append('b');
        else if(i == position[0] && j == position[1]) builder.append((char)('0'+currentState[changed[0]][changed[1]]));
        else
          builder.append((char)('0'+currentState[i][j]));
      }
      if(i != 2) builder.append(' ');
    }
    setState(builder.toString());
  }
  
  // move the blank tile 'up', 'down', 'left', or 'right' in the specific state
  public static void move(int[][] state, String moveDirection){
    int direction = 0;
    if(moveDirection.equals(moveDirections[0])) direction = 0;
    else if(moveDirection.equals(moveDirections[1])) direction = 1;
    else if(moveDirection.equals(moveDirections[2])) direction = 2;
    else
      direction = 3;
    int[] position = findBlank(state);
    int[] changed = new int[2];
    changed[0] = position[0] + (3-direction)/2 * (int)Math.pow(-1,direction+1);
    changed[1] = position[1] + direction/2 * (int)Math.pow(-1, direction+1);
    StringBuilder builder = new StringBuilder();
    for(int i = 0;i < 3;i++){
      for(int j = 0;j < 3;j++){
        if(i == changed[0] && j == changed[1]) builder.append('b');
        else if(i == position[0] && j == position[1]) builder.append((char)('0'+state[changed[0]][changed[1]]));
        else
          builder.append((char)('0'+state[i][j]));
      }
      if(i != 2) builder.append(' ');
    }
    setState(state, builder.toString());
  }
  
  // solve the given puzzle by A* search
  public static void solveAstar(String heuristic){
    //System.out.println("solve A star " + heuristic);
    if(isFinished()){
      System.out.println("Number of tile moves 0");
    }
    while(!isFinished() && node <= maxLimit){
      Tile nodeptr = queue.poll();
      currentState = myClone(nodeptr.state);
      if(isFinished()){
        if(heuristic.equals("h1")) h1Solved++;
        else
          h2Solved++;
        System.out.println("Number of tile moves " + nodeptr.g);
        printMoveSequences(nodeptr);
      }
      else{
        if(isAvailable(currentState, 0)){
          int[][] newState1 = myClone(currentState);
          move(newState1, moveDirections[0]);
          if(heuristic.equals("h1")) queue.add(new Tile(nodeptr, newState1, nodeptr.g + 1, findHFirst(newState1), 0));
          else
            queue.add(new Tile(nodeptr, newState1, nodeptr.g + 1, findHSecond(newState1), 0));
          node++;
        }
        if(isAvailable(currentState, 1)){
          int[][] newState2 = myClone(currentState);
          move(newState2, moveDirections[1]);
          if(heuristic.equals("h1")) queue.add(new Tile(nodeptr, newState2, nodeptr.g + 1, findHFirst(newState2), 1));
          else
            queue.add(new Tile(nodeptr, newState2, nodeptr.g + 1, findHSecond(newState2), 1));
          node++;
        }
        if(isAvailable(currentState, 2)){
          int[][] newState3 = myClone(currentState);
          move(newState3, moveDirections[2]);
          if(heuristic.equals("h1")) queue.add(new Tile(nodeptr, newState3, nodeptr.g + 1, findHFirst(newState3), 2));
          else
            queue.add(new Tile(nodeptr, newState3, nodeptr.g + 1, findHSecond(newState3), 2));
          node++;
        }
        if(isAvailable(currentState, 3)){
          int[][] newState4 = myClone(currentState);
          move(newState4, moveDirections[3]);
          if(heuristic.equals("h1")) queue.add(new Tile(nodeptr, newState4, nodeptr.g + 1, findHFirst(newState4), 3));
          else
            queue.add(new Tile(nodeptr, newState4, nodeptr.g + 1, findHSecond(newState4), 3));
          node++;
        }
      }
    }
    if(node > maxLimit) System.out.println("Unsolvable! Exceed max nodes!");
  }
  
  // h1 = the number of misplaced tiles
  public static int findHFirst(int[][] state){
    int misplace = 0;
    for(int i = 0;i < 3;i++){
      for(int j = 0;j < 3;j++){
        if(state[i][j] != goalState[i][j]) misplace++;
      }
    }
    return misplace;
  }
  
  // h2 = the sum of the distances of the tiles from their goal positions
  public static int findHSecond(int[][] state){
    int  manhattan = 0;
    for(int i = 0;i < 3;i++){
      for(int j = 0;j < 3;j++){
        manhattan += (Math.abs(i-state[i][j]/3) + Math.abs(j-state[i][j] % 3));
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
          beamSolved++;
          System.out.println("Number of tile moves " + nodeptr.g);
          printMoveSequences(nodeptr);
          break;
        }
        else{ 
          if(isAvailable(nodeptr.state, 0)){
            int[][] newState1 = myClone(nodeptr.state);
            move(newState1, moveDirections[0]);
            temp.add(new Tile(nodeptr, newState1, nodeptr.g+1, findHSecond(newState1), 0));
            node++;
          }
          if(isAvailable(nodeptr.state, 1)){
            int[][] newState2 = myClone(nodeptr.state);
            move(newState2, moveDirections[1]);
              temp.add(new Tile(nodeptr, newState2, nodeptr.g+1, findHSecond(newState2), 1));
              node++;
          }
          if(isAvailable(nodeptr.state, 2)){
            int[][] newState3 = myClone(nodeptr.state);
            move(newState3, moveDirections[2]);
              temp.add(new Tile(nodeptr, newState3, nodeptr.g+1, findHSecond(newState3), 2));
              node++;
          }
          if(isAvailable(nodeptr.state, 3)){
            int[][] newState4 = myClone(nodeptr.state);
            move(newState4, moveDirections[3]);
               temp.add(new Tile(nodeptr, newState4, nodeptr.g+1, findHSecond(newState4), 3));
               node++;
          }
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
  
  // print the solution
  public static void printMoveSequences(Tile nodeptr){
    if(nodeptr.parent == null){
      System.out.println("Start state: ");
      printState(nodeptr.state);
    }
    else{
      printMoveSequences(nodeptr.parent);
      System.out.println(moveDirections[nodeptr.direction]);
      printState(nodeptr.state);
    }
  }
  
  // set the number of max node
  public static void maxNodes(int n){
    maxLimit = n;
  }
  
  /**
   ******************************************************
   * helper methods below
   ******************************************************
   **/
  
  // customized comparator for priority queue 
  private static Comparator<Tile> cmp = new Comparator<Tile>(){
    public int compare(Tile nodeptr1, Tile nodeptr2){
      int f1 = nodeptr1.g + nodeptr1.h;
      int f2 = nodeptr2.g + nodeptr2.h;
      return f1 - f2;
    }
  };
  
  // check whether the move is available
  private static boolean isAvailable(int[][] state, int direction){
    int[] position = findBlank(state);
    if(direction == 0){
      if(position[0] -1 < 0) return false;
      else
        return true;
    }
    else if(direction == 1){
      if(position[0] + 1 > 2) return false;
      else
        return true;
    }
    else if(direction == 2){
      if(position[1] - 1 < 0) return false;
      else
        return true;
    }
    else{
      if(position[1] + 1 > 2) return false;
      else
        return true;
    }
  }
  
  // find the position of blank tile in the specific state
  private static int[] findBlank(int[][] state){
    int[] result = new int[2];
    for(int i = 0;i < 3;i++){
      for(int j = 0;j < 3;j++){
        if(state[i][j] == 0){
          result[0] = i;
          result[1] = j;
          return result;
        }
      }
    }
    return result;
  }
  
  private static boolean isFinished(){
    return isFinished(currentState);
  }
  
  // check whether the state is the goal state
  private static boolean isFinished(int[][] state){
    for(int i = 0;i < 3;i++){
      for(int j = 0;j < 3;j++){
        if(state[i][j] != goalState[i][j]) return false;
      }
    }
    return true;
  }
  
  // return a double int array which is the same as the input state
  private static int[][] myClone(int[][] original){
    int[][] clone = new int[original.length][original[0].length];
    for(int i = 0;i < original.length;i++){
      for(int j = 0;j < original[i].length;j++){
        clone[i][j] = original[i][j];
      }
    }
    return clone;
  }
  
  // test method for question a in Experiment
  public static void testEa(int nodeLimit, int total,int K, int N){
    maxNodes(nodeLimit);
    h1Solved = 0;
    h2Solved = 0;
    beamSolved = 0;
    r = new Random();
    r.setSeed(2);
    for(int i = 0;i < total;i++){
      queue = new PriorityQueue<Tile>(cmp);
      node = 1;
      randomizeState(N);
      queue.add(new Tile(null, currentState, 0, findHFirst(currentState), -1));
      solveAstar("h1");
      
      queue = new PriorityQueue<Tile>(cmp);
      node = 1;
      randomizeState(N);
      queue.add(new Tile(null, currentState, 0, findHSecond(currentState), -1));
      solveAstar("h2");
      
      queue = new PriorityQueue<Tile>(cmp);
      node = 1;
      randomizeState(N);
      queue.add(new Tile(null, currentState, 0, findHSecond(currentState), -1));
      solveBeam(K);
    }
    System.out.println("maxNodes = " + maxLimit);
    System.out.println("A*(h1) solved = " + (h1Solved*1.0/total));
    System.out.println("A*(h2) solved = " + (h2Solved*1.0/total));
    System.out.println("Beam(" + K + ")solved = " + (beamSolved*1.0/total));
  }
  
  // test method for question b in Experiment
  public static void testEb(int nodeLimit, int total){
    maxNodes(nodeLimit);
    r = new Random();
    r.setSeed(2);
    for(int i = 1;i <= total;i++){
      queue = new PriorityQueue<Tile>(cmp);
      node = 1;
      randomizeState(i*5);
      queue.add(new Tile(null, currentState, 0, findHFirst(currentState), -1));
      solveAstar("h1");
      System.out.println("Total nodes generated for A*(h1)" + " " + node + "\n");
    }
    System.out.println("****************************************************");
    r = new Random();
    r.setSeed(2);
    for(int i = 1;i <= total;i++){
      queue = new PriorityQueue<Tile>(cmp);
      node = 1;
      randomizeState(i*5);
      queue.add(new Tile(null, currentState, 0, findHSecond(currentState), -1));
      solveAstar("h2");
      System.out.println("Total nodes generated for A*(h2)" + " " + node + "\n");
    }
  }
  
  // test method for question c in Experiment
  public static void testEc(int nodeLimit, int total,int K){
    maxNodes(nodeLimit);
    int[][] startState = new int[3][3];
    r = new Random();
    r.setSeed(2);
    for(int i = 1;i <= total;i++){
      System.out.println("Random walk "+ i*2 + " stpes!");
      randomizeState(i*2);
      startState = myClone(currentState);
      
      queue = new PriorityQueue<Tile>(cmp);
      node = 1;
      queue.add(new Tile(null, currentState, 0, findHFirst(currentState), -1));
      solveAstar("h1");
      System.out.println();
      
      queue = new PriorityQueue<Tile>(cmp);
      node = 1;
      currentState = myClone(startState);
      queue.add(new Tile(null, currentState, 0, findHSecond(currentState), -1));
      solveAstar("h2");
      System.out.println();
      
      queue = new PriorityQueue<Tile>(cmp);
      node = 1;
      currentState = myClone(startState);
      queue.add(new Tile(null, currentState, 0, findHSecond(currentState), -1));
      solveBeam(K);
      System.out.println();
    }
  }
  /**
   ****************************************
   * helper class
   ****************************************
   **/
  // Object that contains information for a specific state
  private static class Tile{
    private Tile parent;
    private int[][] state;
    private int g;
    private int h;
    private int direction;
    
    public Tile(Tile parent, int[][] state, int g, int h, int direction){
      this.parent = parent;
      this.state = state;
      this.g = g;
      this.h = h;
      this.direction = direction;
    }
  }
  
  // main function
  // invoke format: run PA1 inputFile
  public static void main(String[] args) throws IOException{
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    String line = "";
    PA1 p = new PA1();
//    for(int m = 1;m < 20;m++){
//      p.testEa(m*100,5000,20,100);
//      System.out.println();
//    }
      //p.testEb(100000, 20);
    //p.testEc(1000000, 20, 20);
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