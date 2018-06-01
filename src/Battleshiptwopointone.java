import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

class battleshipTwoPointOne {
    static int[][][] coordinate = new int[22][12][11];    // 3D array introduced, coordinating the status and property of each grid on the map
    /*
     * array explanation
     * [22] is used for the rows of the grids, you would assume [20] but [0] and [11] are kept empty for the calculation of the possiblities during attack of the grid
     * [12] is used as the columns, you would assume [10] but [0] and [11] are kept empty once more for the calculation of the possiblities during attack of the grid
     * [11] represents each property of every grid
     * [0] to [6] are used for probability calculations
     * [7] is to determine if the grid has been clicked
     * [8] is to determine if it is a hit (not sunk)
     * [9] is to determine which SHIP (of the 5) is hit
     * [10] is to determine the name of the ship
     */
    static int[] hitNum = new int[6]; // used to determine how many hits a particular ship has sustained
    static int[] AIhitNum = new int[6]; //same as above but for AI
    static int[] maxNum = {0, 5, 4, 3, 3, 2}; //maximun number of lives for each ship (0) is not used as explained above

    static int shots = 0, hits = 0, AIhits = 0, check = 0, position, opponent; //shots and hits of AI and Human
    static boolean placed = false, won = false;

    static JLabel statistics = new JLabel ("You                         Shots: " + shots + "   Hits: " + hits + "                        AI");
    static JButton[][] space = new JButton[10][10];
    static JButton[][] AIspace = new JButton[10][10];
    static JFrame myWindow;

    public static void main (String[] args) throws Exception {
        //Variables
        int winShots , moveChoice , sum = 0 , count = 0 , test;
        String winner , ending;
        char letter;
        String[] shipNames = {null , "Carrier" , "Battleship" , "Cruiser" , "Submarine" , "Destroyer"};

        // Prompt User to choose different versions
        Object[] versions = {"Version 1.4","Version 1.4.1","Version 1.5"};
        int ver = JOptionPane.showOptionDialog(null,
                "Choose a version to run:",
                "Version?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                versions,
                null);
        if (ver == -1){
            System.exit(0);
        }

        //Decide which player goes first
        Object[] options = {"Player","AI"};
        int first = JOptionPane.showOptionDialog(null,
                "Who will go first?",
                "Start",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                null);
        if (first == -1){
            System.exit(0);
        }

        Object[] oppo = {"Human Being","Turing Machine"};
        opponent = JOptionPane.showOptionDialog(null,
                "Play against a human or a Turing machine?",
                "Opponent?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                oppo,
                null);
        if (opponent == -1){
            System.exit(0);
        }

        Object[] testing = {"Playing","Testing"};
        test = JOptionPane.showOptionDialog(null,
                "Playing or testing for the game?",
                "Test?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                testing,
                null);
        if (test == -1){
            System.exit(0);
        }

        //Window
        myWindow = new JFrame("Battleship");

        //Window Size
        myWindow.setSize(1150,600);

        //Close on exit
        myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Layout
        myWindow.setLayout(new BorderLayout());

        //Panel
        JPanel grid = new JPanel();
        JPanel title = new JPanel();
        JPanel stats = new JPanel();
        JPanel AI = new JPanel();
        JPanel filler = new JPanel();

        //Panel Layout
        grid.setLayout(new GridLayout(10,10));
        AI.setLayout (new GridLayout(10,10));

        //Buttons
        for (int i = 0; i < 10; i ++){
            letter = (char)(i + 65);
            for (int j = 0; j < 10; j ++){
                //Create the button
                space[i][j] = new JButton ((String.valueOf(letter)) + (j + 1));

                //Change Font
                space[i][j].setFont(new Font("Monospaced", Font.PLAIN, 12));

                //Add the listeners
                space[i][j].addActionListener (new gridListener());
            }
        }

        //AI Side
        for (int i = 0; i < 10; i ++){
            letter = (char)(i + 65);
            for (int j = 0; j < 10; j ++){
                //Create the button
                AIspace[i][j] = new JButton ((String.valueOf(letter)) + (j + 1));
                //Change Font
                AIspace[i][j].setFont(new Font("Monospaced", Font.PLAIN, 12));
                //Change Color
                AIspace[i][j].setBackground(Color.LIGHT_GRAY);
                //Add the listeners
                AIspace[i][j].addActionListener (new shipListener());
            }
        }

        //Labels
        //Game Name
        JLabel name = new JLabel("BATTLESHIP");
        //Board labels and stats
        statistics = new JLabel ("You                         Shots: " + shots + "   Hits: " + hits + "                        AI");

        //Label Size
        statistics.setFont(new Font("Serif", Font.PLAIN, 36));
        name.setFont(new Font("Impact", Font.PLAIN, 70));

        //Add to panels
        //Human grid
        for (int i = 0; i < 10; i ++){
            for (int j = 0; j < 10; j ++){
                grid.add(space[i][j]);
            }
        }
        //Title
        title.add(name);
        //Stats
        stats.add(statistics);
        //AI
        for (int i = 0; i < 10; i ++){
            for (int j = 0; j < 10; j ++){
                AI.add(AIspace[i][j]);
            }
        }

        //Add to window
        myWindow.add(title,BorderLayout.NORTH);
        myWindow.add(grid,BorderLayout.WEST);
        myWindow.add(filler,BorderLayout.CENTER);
        myWindow.add(AI,BorderLayout.EAST);
        myWindow.add(stats,BorderLayout.SOUTH);
        myWindow.setVisible(true);

        for (int i = 1 ; i < 11 ; i++){     // initialize the coordinate variable
            for (int j = 1 ; j < 11 ; j++){
                for (int k = 0 ; k < 9 ; k++){
                    coordinate[i][j][k] = 1;
                }
            }
        }

        //Ship Placement
        switch (ver){
            case 0:
                placeOnePointFour();
                break;
            case 1:
                placeOnePointFourPointOne();
                break;
            case 2:
                placeOnePointFive();
                break;
        }

        //Ship Placement by user
        for (int i = 1 ; i < 6 ; i++){
            position = i;
            System.out.println("Place the " + shipNames[position] + ".");
            for (int j = 1 ; j <= maxNum[position] ; j++){
                check = 0;
                do{
                } while (check == 0);
            }
        }
        placed = true;

        //Start the shooting
        if (first == 1){     // if the AI moves first, move it for one before circulation
            moveChoice = move();
            if (moveChoice == 100){
                hitMiss(1 , 1);
            } else {
                hitMiss(moveChoice / 10 + 1 , moveChoice % 10 + 1);
            }
            check = 1;
        }

        while (hits < 17 && AIhits < 17) {     // shoots in turn until a side won
            if (check == 0) {
                moveChoice = move();
                if (moveChoice == 100){
                    hitMiss(1 , 1);
                } else {
                    hitMiss(moveChoice / 10 + 1 , moveChoice % 10 + 1);
                }
                check = 1;
            }

            if (test == 1){    // Testing code, emulate human clicking the sam e grid every time, test how many moves does it take for the computer to win the game
                hitMiss(17,5);
                shots++;
                check = 0;
            }
        }

        won = true;
        if (hits == 17) {    // When player wins
            winner = "Player";
            winShots = shots + 1;
            ending = winner + " wins in " + winShots + " moves!";
        } else {    // When computer wins
            winner = "Computer";
            if (first == 1){
                winShots = shots + 1;
            } else {
                winShots = shots;
            }

            File record = new File("record.txt");

            PrintWriter out = new PrintWriter(new FileWriter(record , true));
            out.println(winShots);
            out.close();

            Scanner in = new Scanner(record);
            while (in.hasNextInt()){
                sum += in.nextInt();
                count++;
            }
            in.close();

            ending = winner + " wins in " + winShots + " moves!\nPrevious average shot is " + sum / count + " during " + count + " tries.";
        }

        System.out.println(ending);


        Object[] quit = {"Quit"};
        int n = JOptionPane.showOptionDialog(null,
                ending,
                winner + " wins!",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,     //do not use a custom Icon
                quit,  //the titles of buttons
                quit[0]); //default button title
        if (n == 0 || n == -1){
            System.exit(0);
        }
    }

    static class gridListener implements ActionListener {     // invokes when an action button is pressed
        public void actionPerformed(ActionEvent event) {
            //Variables
            int r = 0, c = 0;
            //Find out which button was pressed
            for (int i = 0; i < 10; i ++){
                for (int j = 0; j < 10; j ++){
                    //Check each button
                    if (event.getSource() == space[i][j]){
                        r = i + 12;
                        c = j + 1;
                        break;
                    }
                }
            }
            hitMiss(r,c);     // to determine the human-clicked grid is a hit or a miss
            shots++;
            statistics.setText ("You                         Shots: " + shots + "   Hits: " + hits + "                        AI");     // update the
            check = 0;     // induce the loop go go back and prompt user to place another grid
        }
    }

    static class shipListener implements ActionListener {     // invokes when an AI button is pressed
        public void actionPerformed(ActionEvent event) {
            if (!placed){     // if the map hasn't already placed
                for (int i = 0; i < 10; i ++){
                    for (int j = 0; j < 10; j ++){
                        //Check each button
                        if (event.getSource() == AIspace[i][j] && coordinate[i + 1][j + 1][10] == 0){     // if the grid has not already occupied by other parts of any ship
                            coordinate[i + 1][j + 1][10] = position;
                            AIspace[i][j].setBackground(Color.WHITE);     // Make the positive grids visible
                            check = 1;     // induce the loop go go back and prompt AI to make a move
                            break;
                        }
                    }
                }
            }
        }
    }


    public static void placeOnePointFour() {     //Placing the ships

        int xValue, yValue; //these are the integers which will be given a random value to randomly place the ships
        int carrierAxis, battleshipAxis, cruiserAxis, carrierAxis1, battleshipAxis1, cruiserAxis1, carrierAxis2, battleshipAxis2, cruiserAxis2;
        //These axis integers are used to keep track of where the ships are placed to prevent overlapping/parallel ship placement
        int cruiserAxisy; //records this seperatley to make sure the other ship correspondent is placed opposite direction to it (used for destroyer placement later on)
        //cruiserAxisy is key to the code to prevent overlapping whatsoever while still only giving a 50% hit chance... 5/10 grids of a column

        xValue = randomizerx(); //these call on the methods below which creates a random value for the axis
        yValue = randomizery(); //these call on the methods below which creates a random value for the axis

        carrierAxis = xValue;     //prevents future overlap
        carrierAxis1 = xValue + 1; //this means ships cannot be placed 1 row UP from this first ship
        carrierAxis2 = xValue - 1; //this means ships cannot be placed 1 row DOWN from this first ship


        if (yValue==1) { //yValue == 1 means ships start from left to right
            for (int i=1; i<6; i++) {
                coordinate [xValue + 11] [i] [10] = 1;     //Carrier arrays, 1 means carrier
                //xValue + 11 to match the originally declared array size
                // it will run from 1-5 which is the the columns they will be placed in
            }
        } else if (yValue==2) { //yValue == 2 means ships start from right to left
            for (int i=10; i>5; i--){
                coordinate [xValue + 11] [i] [10] = 1;     //runs from 10-6
            }
        }

        do {
            xValue = randomizerx(); //calls upon randomizer method until overlapping does not happen
            yValue = randomizery();
        } while (xValue == carrierAxis || xValue == carrierAxis1 || xValue == carrierAxis2);  //no overlapping

        battleshipAxis = xValue; //prevents future overlap
        battleshipAxis1 = xValue + 1; //ships cannot be placed 1 row up or down from the original ships
        battleshipAxis2 = xValue - 1;

        if (yValue==1) {
            for (int i = 1; i<5; i++){ //runs from 1-4
                coordinate [xValue + 11] [i] [10] = 2; //Battleship arrays, 2 means battleship
            }
        } else if (yValue==2) { //runs from 10-7
            for (int i = 10; i>6; i--) {
                coordinate [xValue + 11] [i] [10] = 2;
            }
        }

        do {
            xValue = randomizerx();
            yValue = randomizery();
        } while (xValue == carrierAxis || xValue == carrierAxis1 || xValue == carrierAxis2 || xValue == battleshipAxis || xValue == battleshipAxis1 || xValue == battleshipAxis2);  //no overlapping!

        cruiserAxis = xValue; //prevents future overlap
        cruiserAxis1 = xValue + 1;
        cruiserAxis2 = xValue - 1;

        cruiserAxisy = yValue;

        if (yValue==1) {
            for (int i = 1; i<4; i++){ //runs from 1-3
                coordinate [xValue + 11] [i] [10] = 3; //cruiser arrays, 3 means cruiser
            }
        } else if (yValue==2) { //runs from 10-8
            for (int i = 10; i>7; i--) {
                coordinate [xValue + 11] [i] [10] = 3;
            }
        }

        do {
            xValue = randomizerx();
            yValue = randomizery();
        } while (xValue == carrierAxis || xValue == carrierAxis1 || xValue == carrierAxis2 || xValue == battleshipAxis || xValue == battleshipAxis1 || xValue == battleshipAxis2 || xValue == cruiserAxis || xValue == cruiserAxis1 || xValue == cruiserAxis2);  //no overlapping!

        if (yValue==1) {
            for (int i = 1; i<4; i++){ //runs from 1-3
                coordinate [xValue + 11] [i] [10] = 4; //sub arrays, 4 means sub
            }
        } else if (yValue==2) {
            for (int i = 10; i>7; i--) { //runs from 10-8
                coordinate [xValue + 11] [i] [10] = 4;
            }
        }

        if (cruiserAxisy==2) {
            //destoyer is linked with the cruiser (they belong to the same row always) as 3 + 2 = 5
            // 5 out of 10 spaces is only a 70% chance, too low for a hunt/target parity system
            coordinate [cruiserAxis + 11] [1] [10] = 5; //destroyer arrays, 5 means destroyer
            coordinate [cruiserAxis + 11] [2] [10] = 5; // we do not use a for-loop here to demonstrate how the code works and also because it only has 2 lines of code
            // thus, making a for loop not neccesarily more efficient
        } else if (cruiserAxisy==1) {
            coordinate [cruiserAxis + 11] [9] [10] = 5;
            coordinate [cruiserAxis + 11] [10] [10] = 5;
        }

        if (opponent == 1){
            for (int i = 12 ; i < 22 ; i++){
                for (int j = 1 ; j < 11 ; j++){
                    if (coordinate[i][j][10] != 0){
                        space[i - 12][j - 1].setBackground(Color.WHITE);     // Make the positive grids visible
                    }
                }
            }
        }
    }

    public static void placeOnePointFourPointOne() {     //Placing the ships

        int xValue, yValue; //these are the integers which will be given a random value to randomly place the ships
        int carrierAxis, battleshipAxis, cruiserAxis, carrierAxis1, battleshipAxis1, cruiserAxis1, carrierAxis2, battleshipAxis2, cruiserAxis2;
        //These axis integers are used to keep track of where the ships are placed to prevent overlapping/parallel ship placement
        int cruiserAxisy; //records this seperatley to make sure the other ship correspondent is placed opposite direction to it (used for destroyer placement later on)
        //cruiserAxisy is key to the code to prevent overlapping whatsoever while still only giving a 50% hit chance... 5/10 grids of a column

        xValue = randomizerx(); //these call on the methods below which creates a random value for the axis
        yValue = randomizery(); //these call on the methods below which creates a random value for the axis

        carrierAxis = xValue;     //prevents future overlap
        carrierAxis1 = xValue + 1; //this means ships cannot be placed 1 row UP from this first ship
        carrierAxis2 = xValue - 1; //this means ships cannot be placed 1 row DOWN from this first ship


        if (yValue==1) { //yValue == 1 means ships start from left to right
            for (int i=1; i<6; i++) {
                coordinate [xValue + 11] [i+1] [10] = 1;     //Carrier arrays, 1 means carrier
                //xValue + 11 to match the originally declared array size
                // it will run from 1-5 which is the the columns they will be placed in
            }
        } else if (yValue==2) { //yValue == 2 means ships start from right to left
            for (int i=10; i>5; i--){
                coordinate [xValue + 11] [i-1] [10] = 1;     //runs from 10-6
            }
        }

        do {
            xValue = randomizerx(); //calls upon randomizer method until overlapping does not happen
            yValue = randomizery();
        } while (xValue == carrierAxis || xValue == carrierAxis1 || xValue == carrierAxis2);  //no overlapping

        battleshipAxis = xValue; //prevents future overlap
        battleshipAxis1 = xValue + 1; //ships cannot be placed 1 row up or down from the original ships
        battleshipAxis2 = xValue - 1;

        if (yValue==1) {
            for (int i = 1; i<5; i++){ //runs from 1-4
                coordinate [xValue + 11] [i+1] [10] = 2; //Battleship arrays, 2 means battleship
            }
        } else if (yValue==2) { //runs from 10-7
            for (int i = 10; i>6; i--) {
                coordinate [xValue + 11] [i-1] [10] = 2;
            }
        }

        do {
            xValue = randomizerx();
            yValue = randomizery();
        } while (xValue == carrierAxis || xValue == carrierAxis1 || xValue == carrierAxis2 || xValue == battleshipAxis || xValue == battleshipAxis1 || xValue == battleshipAxis2);  //no overlapping!

        cruiserAxis = xValue; //prevents future overlap
        cruiserAxis1 = xValue + 1;
        cruiserAxis2 = xValue - 1;

        cruiserAxisy = yValue;

        if (yValue==1) {
            for (int i = 1; i<4; i++){ //runs from 1-3
                coordinate [xValue + 11] [i+1] [10] = 3; //cruiser arrays, 3 means cruiser
            }
        } else if (yValue==2) { //runs from 10-8
            for (int i = 10; i>7; i--) {
                coordinate [xValue + 11] [i-1] [10] = 3;
            }
        }

        do {
            xValue = randomizerx();
            yValue = randomizery();
        } while (xValue == carrierAxis || xValue == carrierAxis1 || xValue == carrierAxis2 || xValue == battleshipAxis || xValue == battleshipAxis1 || xValue == battleshipAxis2 || xValue == cruiserAxis || xValue == cruiserAxis1 || xValue == cruiserAxis2);  //no overlapping!

        if (yValue==1) {
            for (int i = 1; i<4; i++){ //runs from 1-3
                coordinate [xValue + 11] [i+1] [10] = 4; //sub arrays, 4 means sub
            }
        } else if (yValue==2) {
            for (int i = 10; i>7; i--) { //runs from 10-8
                coordinate [xValue + 11] [i-1] [10] = 4;
            }
        }

        if (cruiserAxisy==2) {
            //destoyer is linked with the cruiser (they belong to the same row always) as 3 + 2 = 5
            // 5 out of 10 spaces is only a 70% chance, too low for a hunt/target parity system
            coordinate [cruiserAxis + 11] [2] [10] = 5; //destroyer arrays, 5 means destroyer
            coordinate [cruiserAxis + 11] [3] [10] = 5; // we do not use a for-loop here to demonstrate how the code works and also because it only has 2 lines of code
            // thus, making a for loop not neccesarily more efficient
        } else if (cruiserAxisy==1) {
            coordinate [cruiserAxis + 11] [8] [10] = 5;
            coordinate [cruiserAxis + 11] [9] [10] = 5;
        }

        if (opponent == 1){
            for (int i = 12 ; i < 22 ; i++){
                for (int j = 1 ; j < 11 ; j++){
                    if (coordinate[i][j][10] != 0){
                        space[i - 12][j - 1].setBackground(Color.WHITE);     // Make the positive grids visible
                    }
                }
            }
        }
    }

    public static void placeOnePointFive() {     //Placing the ships

        int xValue, yValue; //these are the integers which will be given a random value to randomly place the ships
        int carrierAxis, battleshipAxis, cruiserAxis, carrierAxis1, battleshipAxis1, cruiserAxis1, carrierAxis2, battleshipAxis2, cruiserAxis2;
        //These axis integers are used to keep track of where the ships are placed to prevent overlapping/parallel ship placement
        int cruiserAxisy; //records this seperatley to make sure the other ship correspondent is placed opposite direction to it (used for destroyer placement later on)
        //cruiserAxisy is key to the code to prevent overlapping whatsoever while still only giving a 50% hit chance... 5/10 grids of a column

        xValue = randomizerx(); //these call on the methods below which creates a random value for the axis
        yValue = randomizery(); //these call on the methods below which creates a random value for the axis

        carrierAxis = xValue;     //prevents future overlap
        carrierAxis1 = xValue + 1; //this means ships cannot be placed 1 row UP from this first ship
        carrierAxis2 = xValue - 1; //this means ships cannot be placed 1 row DOWN from this first ship


        if (yValue==1) { //yValue == 1 means ships start from left to right
            for (int i=1; i<6; i++) {
                coordinate [i + 11] [xValue] [10] = 1;     //Carrier arrays, 1 means carrier
                //xValue + 11 to match the originally declared array size
                // it will run from 1-5 which is the the columns they will be placed in
            }
        } else if (yValue==2) { //yValue == 2 means ships start from right to left
            for (int i=10; i>5; i--){
                coordinate [i + 11] [xValue] [10] = 1;     //runs from 10-6
            }
        }

        do {
            xValue = randomizerx(); //calls upon randomizer method until overlapping does not happen
            yValue = randomizery();
        } while (xValue == carrierAxis || xValue == carrierAxis1 || xValue == carrierAxis2);  //no overlapping

        battleshipAxis = xValue; //prevents future overlap
        battleshipAxis1 = xValue + 1; //ships cannot be placed 1 row up or down from the original ships
        battleshipAxis2 = xValue - 1;

        if (yValue==1) {
            for (int i = 1; i<5; i++){ //runs from 1-4
                coordinate [i + 11] [xValue] [10] = 2; //Battleship arrays, 2 means battleship
            }
        } else if (yValue==2) { //runs from 10-7
            for (int i = 10; i>6; i--) {
                coordinate [i + 11] [xValue] [10] = 2;
            }
        }

        do {
            xValue = randomizerx();
            yValue = randomizery();
        } while (xValue == carrierAxis || xValue == carrierAxis1 || xValue == carrierAxis2 || xValue == battleshipAxis || xValue == battleshipAxis1 || xValue == battleshipAxis2);  //no overlapping!

        cruiserAxis = xValue; //prevents future overlap
        cruiserAxis1 = xValue + 1;
        cruiserAxis2 = xValue - 1;

        cruiserAxisy = yValue;

        if (yValue==1) {
            for (int i = 1; i<4; i++){ //runs from 1-3
                coordinate [i + 11] [xValue] [10] = 3; //cruiser arrays, 3 means cruiser
            }
        } else if (yValue==2) { //runs from 10-8
            for (int i = 10; i>7; i--) {
                coordinate [i + 11] [xValue] [10] = 3;
            }
        }

        do {
            xValue = randomizerx();
            yValue = randomizery();
        } while (xValue == carrierAxis || xValue == carrierAxis1 || xValue == carrierAxis2 || xValue == battleshipAxis || xValue == battleshipAxis1 || xValue == battleshipAxis2 || xValue == cruiserAxis || xValue == cruiserAxis1 || xValue == cruiserAxis2);  //no overlapping!

        if (yValue==1) {
            for (int i = 1; i<4; i++){ //runs from 1-3
                coordinate [i + 11] [xValue] [10] = 4; //sub arrays, 4 means sub
            }
        } else if (yValue==2) {
            for (int i = 10; i>7; i--) { //runs from 10-8
                coordinate [i + 11] [xValue] [10] = 4;
            }
        }

        if (cruiserAxisy==2) {
            //destoyer is linked with the cruiser (they belong to the same row always) as 3 + 2 = 5
            // 5 out of 10 spaces is only a 70% chance, too low for a hunt/target parity system
            coordinate [1 + 11] [cruiserAxis] [10] = 5; //destroyer arrays, 5 means destroyer
            coordinate [2 + 11] [cruiserAxis] [10] = 5; // we do not use a for-loop here to demonstrate how the code works and also because it only has 2 lines of code
            // thus, making a for loop not neccesarily more efficient
        } else if (cruiserAxisy==1) {
            coordinate [9 + 11] [cruiserAxis] [10] = 5;
            coordinate [10 + 11] [cruiserAxis] [10] = 5;
        }

        if (opponent == 1){
            for (int i = 12 ; i < 22 ; i++){
                for (int j = 1 ; j < 11 ; j++){
                    if (coordinate[i][j][10] != 0){
                        space[i - 12][j - 1].setBackground(Color.WHITE);     // Make the positive grids visible
                    }
                }
            }
        }
    }

    public static int randomizerx () { // method to call upon x axis

        Random xAxis = new Random();
        int x = xAxis.nextInt(10) + 1;

        return x;
    }

    public static int randomizery () { // method to call upon up or down (towards the very edge always for best % win)

        Random downUp = new Random();
        int y = downUp.nextInt(2) + 1;

        return y;
    }

    public static void hitMiss(int x, int y){ // determine hit or miss
        switch (coordinate[x][y][10]){
            case 0:
                miss(x, y);//call miss method
                if (x < 11){//determine whether it's AI or player by checking x value
                    AIspace[x - 1][y - 1].setBackground(Color.CYAN);//change the button colour to cyan when misses
                } else {
                    space[x - 12][y - 1].setBackground(Color.CYAN);//change the button colour to cyan when misses
                }
                System.out.print("MISS.\n");
                break;

            default:
                hit(x, y);//call hit method
                System.out.print("HIT, ");

                if (x < 11){//if AI is playing
                    AIhitNum[coordinate[x][y][10]]++;//update number of hit for AI
                    AIhits++;//update total number of hit for AI
                    AIspace[x - 1][y - 1].setBackground(Color.RED);//change the button colour to red when hits
                } else {//if player is playing
                    hitNum[coordinate[x][y][10]]++;//update number of hit for player
                    hits++;//update total number of hit by player
                    space[x - 12][y - 1].setBackground(Color.RED);//change the button colour to red when hits
                }
                if ((x > 11 && hitNum[coordinate[x][y][10]] == maxNum[coordinate[x][y][10]]) || (x < 11 && AIhitNum[coordinate[x][y][10]] == maxNum[coordinate[x][y][10]])){//maximum number of coordinates have been hit for a ship
                    sink(x, y);//call sink method
                    System.out.print("SUNK ");
                }
                switch (coordinate[x][y][10]){//use the spot coordinate[x][y][10] to determine and print which ship has been hit
                    case 1:
                        System.out.print("CARRIER.\n");
                        break;
                    case 2:
                        System.out.print("BATTLESHIP.\n");
                        break;
                    case 3:
                        System.out.print("CRUISER.\n");
                        break;
                    case 4:
                        System.out.print("SUBMARINE.\n");
                        break;
                    case 5:
                        System.out.print("DESTORYER.\n");
                        break;
                    default:
                        break;
                }
                coordinate[x][y][10] = 0;//clear the spot
                break;
        }
    }

    public static void hit(int x , int y){     // changing possibilities when one grid is hit
        coordinate[x][y][7] = 0;     // when [][][7] = 0 means that the grid has been clicked
        coordinate[x][y][8] = 0;     // when [][][8] = 0 means that the grid has been hit but not sunk, will restore to 1 if sunk
        coordinate[x][y][9] = coordinate[x][y][10];     // to record the type of the ship hit in [][][9]
    }

    public static void miss(int x , int y){     // changing possibilities when one grid is missed
        for (int i = 0 ; i < 11 ; i++){
            coordinate[x][y][i] = 0;     // eliminate any possibility that the grid can be hit again
        }
        coordinate[x][y][8] = 1;    // make sure that the grid remains unhit
    }

    public static void sink(int x , int y){     // changing possibilities when one grid is sunk
        if (x > 0 && x < 11) {     // AI grid
            for (int i = 1 ; i < 11 ; i++){
                for (int j = 1 ; j < 11 ; j++){
                    if (coordinate[i][j][9] == coordinate[x][y][10]){     // this type of ship is to be eradicated
                        for (int k = 0 ; k < 10 ; k++){
                            coordinate[i][j][k] = 0;     // eliminate the possibility as it is already sunk
                        }
                        coordinate[i][j][8] = 1;     // restore the unhit status of the grid
                    }
                }
            }
        } else {     // Human grid
            for (int i = 12 ; i < 22 ; i++){
                for (int j = 1 ; j < 11 ; j++){
                    if (coordinate[i][j][9] == coordinate[x][y][10]){
                        for (int k = 0 ; k < 10 ; k++){
                            coordinate[i][j][k] = 0;
                        }
                        coordinate[i][j][8] = 1;
                    }
                }
            }
        }
    }

    public static int find0Up(int x , int y) {     // finds the nearest 0 to the left in order to calculate possiblity of each grid
        if (coordinate[x][y][1] == 0){
            return 0;     // find the nearest 0 on the left side, [x][0][1] is already kept 0
        } else {
            return (find0Up(x , y - 1) + 1);     // Plus one for every box to the right
        }
    }

    public static int find0Down(int x , int y) {     // finds the nearest 0 to the right in order to calculate possiblity of each grid
        if (coordinate[x][y][2] == 0){
            return 0;     // find the nearest 0 on the right side, [x][11][1] is already kept 0
        } else {
            return (find0Down(x , y + 1) + 1);     // Plus one for every box to the left
        }
    }

    public static int find0Left(int x , int y) {     // finds the nearest 0 on upwards in order to calculate possiblity of each grid
        if (coordinate[x][y][4] == 0){
            return 0;     // find the nearest 0 upwards, [0][y][1] is already kept 0
        } else {
            return (find0Left(x - 1 , y) + 1);     // Plus one for every box downwards
        }
    }

    public static int find0Right(int x , int y) {     // finds the nearest 0 downwards in order to calculate possiblity of each grid
        if (coordinate[x][y][5] == 0){
            return 0;     // find the nearest 0 downwards, [11][y][1] is already kept 0
        } else {
            return (find0Right(x + 1 , y) + 1);     // Plus one for every box upwards
        }
    }

    public static int nullCell (int[] array){    // find a position to fill in the informaton for int[]
        for (int i = 0 ; i < array.length ; i++){
            if (array[i] == 0){     // find 0
                return i;
            }
        }
        return array.length - 1;
    }

    public static int sameORnullCell (int[] array , int a){     // find a position to fill in the informaton or to accumulate if same items already exists
        for (int i = 0 ; i < array.length ; i++){
            if (array[i] == a){     // find a
                return i;
            } else if (array[i] == 0){     // find 0
                return i;
            }
        }
        return array.length - 1;
    }

    public static int draw(int[] array , int maxN){     // draws a cell from an array to make it the choice
        Random random = new Random();
        position = array[random.nextInt(maxN)];
        return position;
    }

    public static int[] bubbleSortPossibilities(int[] arr){     // BubbleSort the possibilities of hitting among cells in an array
        int buffer , a , b;
        position = nullCell(arr);
        if (position < 2){
            return arr;
        }
        for(int i = 0; i < position ; i++){     // only a filled cell from the array will participate the sorting
            for(int j = 1; j < position - i; j++){     // compare using BubbleSort algorithm
                if (arr[j - 1] == 100){     // find the possibility calculated in the array
                    a = coordinate[1][1][0];
                } else {
                    a = coordinate[arr[j - 1] / 10 + 1][arr[j - 1] % 10 + 1][0];
                }
                if (arr[j] == 100){
                    b = coordinate[1][1][0];
                } else {
                    b = coordinate[arr[j] / 10 + 1][arr[j] % 10 + 1][0];
                }
                if(a < b){     // swap if the possibility of the former cell is less than that of the latter cell
                    buffer = arr[j - 1];
                    arr[j - 1] = arr[j];
                    arr[j] = buffer;
                }
            }
        }
        return arr;
    }

    public static int shipLeft() {    // Calculate the minimum cell left unhit by the AI , used in parity guessing hit
        for (int i = 5 ; i > 0 ; i--){
            if (AIhitNum[i] != maxNum[i]){
                return maxNum[i];
            }
        }
        return 2;
    }

    public static int move() {    // determine which grid AI moves
        int[] pool = new int[51];    // Possible moves of all likeliehoods are gathered in a pool
        int hit = 0 , choice , num , multipleHits = 0;
        int minShip = shipLeft();

        for (int i = 1 ; i < 11 ; i++){     // Calculate the posibilities of each square
            for (int j = 1 ; j < 11 ; j++){
                coordinate[i][j][1] = find0Up(i , j);
                coordinate[i][j][2] = find0Down(i , j);
                coordinate[i][j][3] = (coordinate[i][j][1] + coordinate[i][j][2]) - Math.abs((coordinate[i][j][1] - coordinate[i][j][2]));
                coordinate[i][j][4] = find0Left(i , j);
                coordinate[i][j][5] = find0Right(i , j);
                coordinate[i][j][6] = (coordinate[i][j][4] + coordinate[i][j][5]) - Math.abs((coordinate[i][j][4] - coordinate[i][j][5]));
                coordinate[i][j][0] = coordinate[i][j][3] * coordinate[i][j][6] * coordinate[i][j][7];     // calculate the possibility if the grid is remain unclicked
            }
        }

        for (int i = 1 ; i < 11 ; i++){
            for (int j = 1 ; j < 11 ; j++){
                if (coordinate[i][j][8] == 0){
                    hit++;     // find how many unsunk hits there are on the map, in order to determine stragegy
                }
            }
        }

        for (int i = 1 ; i < 6 ; i++){
            if (AIhitNum[i] > 1 && AIhitNum[i] < maxNum[i]){
                multipleHits = i;     // find the greatest unsunk multiple hits there are on the map, in order to determine stragegy
                break;
            }
        }

        if (hit == 0){     // no hits found (or all hits are sunk so far) HUNT MODE
            for (int i = 1 ; i < 11 ; i++){
                for (int j = 1 ; j < 11 ; j++){
                    if (((i + j) % minShip == 1) && (coordinate[i][j][0] != 0)){     // only hit half(or 1 / the minimum number of the unhit ship) of the map when guessing
                        pool[nullCell(pool)] = 10 * i + j - 11;
                    }
                }
            }

            pool = bubbleSortPossibilities(pool);     // sort the possibilities in the pool

            num = nullCell(pool) / minShip + 1;     // only top limit numbers of the ship will be considered for draw , the number decrease as the game proceeds
            choice = draw(pool , num);

            return choice;

        } else if (multipleHits != 0){     // more than one hits found (in a row or column as a part of the same ship) LINE MODE
            int[] hitRow = new int[6];
            for (int i = 1 ; i < 11 ; i++){
                for (int j = 1 ; j < 11 ; j++){
                    if (coordinate[i][j][9] == multipleHits){
                        hitRow[nullCell(hitRow)] = 10 * i + j - 11;     // determine in which cell this paticular ship has been hit
                    }
                }
            }

            if (hitRow[1] - hitRow[0] == 10){     // if the trajectory of the hits is lateral
                if (hitRow[0] / 10 != 0){
                    if (hitRow[0] == 10 && coordinate[1][1][0] != 0){
                        pool[nullCell(pool)] = 100;     // grid(1,1) is remodified to the code 100 instead of 0 to eliminate bugs
                    } else if (coordinate[(hitRow[0] - 10) / 10 + 1][(hitRow[0] - 10) % 10 + 1][0] != 0) {
                        pool[nullCell(pool)] = hitRow[0] - 10;     // find the grid upwards, if its calculated possibilities remain positive, consider it prospective hit
                    }
                }
                if (hitRow[nullCell(hitRow) - 1] / 10 != 9 && coordinate[(hitRow[nullCell(hitRow) - 1] + 10) / 10 + 1][(hitRow[nullCell(hitRow) - 1] + 10) % 10 + 1][0] != 0){
                    pool[nullCell(pool)] = hitRow[nullCell(hitRow) - 1] + 10;     // find the grid downwards, if its calculated possibilities remain positive, consider it prospective hit
                }
            } else if (hitRow[1] - hitRow[0] == 1) {     // if the trajectory of the hits is vertical
                if (hitRow[0] % 10 != 0){
                    if (hitRow[0] == 1 && coordinate[1][1][0] != 0){
                        pool[nullCell(pool)] = 100;
                    } else if (coordinate[(hitRow[0] - 1) / 10 + 1][(hitRow[0] - 1) % 10 + 1][0] != 0) {
                        pool[nullCell(pool)] = hitRow[0] - 1;     // find the grid to the left, if its calculated possibilities remain positive, consider it prospective hit
                    }
                }
                if (hitRow[nullCell(hitRow) - 1] % 10 != 9 && coordinate[(hitRow[nullCell(hitRow) - 1] + 1) / 10 + 1][(hitRow[nullCell(hitRow) - 1] + 1) % 10 + 1][0] != 0){
                    pool[nullCell(pool)] = hitRow[nullCell(hitRow) - 1] + 1;     // find the grid to the right, if its calculated possibilities remain positive, consider it prospective hit
                }
            }

            pool = bubbleSortPossibilities(pool);
            choice = pool[0];     // directly choose the most possible grid without drawing

            if (choice == 0){     // debug use
                System.out.println("Hey we are in trouble."); //this was originally supposed to display if anything went wrong during program testing
                // now that program is 100% ready to go, it will not show up but we will preserve the originallity of the program!
                for (int i = 1 ; i < 11 ; i++){
                    for (int j = 1 ; j < 11 ; j++){
                        if (coordinate[i][j][0] != 0){
                            pool[nullCell(pool)] = 10 * i + j - 11;
                        }
                    }
                }
                pool = bubbleSortPossibilities(pool);
                choice = draw(pool , nullCell(pool) / 2 + 2);
                return choice;
            }

            return choice;

        } else {     // only no more than one hit is found per ship (choose one of the 4 grids next to the hit) TARGET MODE
            for (int i = 1 ; i < 11 ; i++){
                for (int j = 1 ; j < 11 ; j++){
                    if (coordinate[i][j][8] == 0){
                        if (coordinate[i + 1][j][0] != 0) {
                            pool[nullCell(pool)] = 10 * (i + 1) + j - 11;     // right
                        }
                        if (coordinate[i - 1][j][0] != 0) {
                            if (i == 2 && j == 1){
                                pool[nullCell(pool)] = 100;
                            } else {
                                pool[nullCell(pool)] = 10 * (i - 1) + j - 11;     // left
                            }
                        }
                        if (coordinate[i][j + 1][0] != 0) {
                            pool[nullCell(pool)] = 10 * (i) + j + 1 - 11;     // down
                        }
                        if (coordinate[i][j - 1][0] != 0) {
                            if (i == 1 && j == 2){
                                pool[nullCell(pool)] = 100;
                            } else {
                                pool[nullCell(pool)] = 10 * (i) + j - 1 - 11;     // up
                            }
                        }
                    }
                }
            }

            pool = bubbleSortPossibilities(pool);

            num = Math.max(Math.min(nullCell(pool) / 3 , nullCell(pool)) , 1);     // to limit only top possibilities is drawn but ensure at least one
            choice = draw(pool , num);

            if (choice == 0){     // debug use
                System.out.println("Hey we are in trouble.");

                for (int i = 1 ; i < 11 ; i++){
                    for (int j = 1 ; j < 11 ; j++){
                        if (coordinate[i][j][0] != 0){
                            pool[nullCell(pool)] = 10 * i + j - 11;
                        }
                    }
                }
                pool = bubbleSortPossibilities(pool);
                choice = draw(pool , nullCell(pool) / 2 + 2);
                return choice;
            }

            return choice;

        }

    }
}