package com.example;

import javax.swing.*;
import org.json.JSONException;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;

public class App implements ActionListener{

    final int TEAM_AMOUNT = 32; // Have to change this if a new NHL team is born

    //Part of the teams page
    JButton teamNameButton;
    JButton[] teamNameButtons = new JButton[TEAM_AMOUNT];
    JTextArea[] infoArray = new JTextArea[TEAM_AMOUNT];

    // Part of the more stats page
    JLabel[] dataPoints;
    JTextPane seasonPerformance = new JTextPane();
    JTextArea moreStatsTeamName = new JTextArea();
    JLabel moreStatsTeamLogo = new JLabel();
    JTextArea dataPointsBackground = new JTextArea();
    JTextPane rosterTitle = new JTextPane();
    JTextPane roster = new JTextPane();
    JTextArea rosterBackground = new JTextArea();
    JTextField rosterSearch = new JTextField();
    JButton rosterSearchButton = new JButton("");

    // General variables
    static JFrame frame;
    JLabel topBar;
    JButton topBarTeams, moreStatsButton;
    Team[] teams = new Team[TEAM_AMOUNT];
    JLabel[] images = new JLabel[TEAM_AMOUNT];
    int currentSelectedTeamIndex;
    int currentPage = 0;
    
    // TODO: add a font
    Font myFont = new Font(null, Font.BOLD, 15);
    Font myFontBigger = new Font(null, Font.BOLD, 20);
    Font myFontLighter = new Font(null, Font.PLAIN, 15);
    Font myFontLighterBigger = new Font(null, Font.PLAIN, 20);
    Color myOrange = new Color(248,158,124);

    /** Defining visible elements of the app */
    App() throws IOException, URISyntaxException{

        APIStuff.populateArrays();
        teams = APIStuff.getTeams();

        frame = new JFrame("NHL Stats");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(650, 850);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.DARK_GRAY);

        ////////////////////////////

        topBarTeams = new JButton("Teams");
        topBarTeams.setBounds(20, 10, 80, 30);
        topBarTeams.setForeground(Color.black);
        topBarTeams.setBackground(myOrange);
        topBarTeams.addActionListener(this);
        frame.add(topBarTeams);
        topBarTeams.setVisible(true);

        moreStatsButton = new JButton("More stats");
        moreStatsButton.setBounds(300, 615, 120, 30);
        moreStatsButton.setForeground(Color.black);
        moreStatsButton.setBackground(Color.white);
        moreStatsButton.addActionListener(this);
        frame.add(moreStatsButton);
        moreStatsButton.setVisible(true);

        ImageIcon topBarImg = new ImageIcon("images/topBar.png");
        topBar = new JLabel(topBarImg);
        topBar.setBounds(0, 0, 650, 50);
        topBar.setBackground(new Color(30, 30, 30));
        frame.add(topBar);
        topBar.setVisible(true);

        ////////////////////////////

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){ // Loads the images from a local folder

            ImageIcon img = new ImageIcon("images/" + teams[teamIndex].ab + ".png");
            images[teamIndex] = new JLabel();
            images[teamIndex].setIcon(img);
            images[teamIndex].setBounds(275, 330, 270, 200);
            frame.add(images[teamIndex]);
            images[teamIndex].setVisible(false);
        }

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){ // Loads the team name buttons

            teamNameButton = new JButton(teams[teamIndex].name + " (" + teams[teamIndex].points + ")");
            teamNameButton.setBounds(30, teamIndex*21+90, 200, 20);
            teamNameButton.setForeground(Color.white);
            teamNameButton.setBackground(null);
            teamNameButton.setFont(myFont);
            teamNameButton.setBorder(null);
            teamNameButton.addActionListener(this);
            teamNameButtons[teamIndex] = teamNameButton;
            frame.add(teamNameButton);
        }

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){

            infoArray[teamIndex] = new JTextArea();
            infoArray[teamIndex].setBounds(300, 95, 250, 500);
            infoArray[teamIndex].setBackground(new Color(30, 30, 30));
            infoArray[teamIndex].setForeground(Color.white);
            infoArray[teamIndex].setFont(myFontLighter);
            infoArray[teamIndex].setEditable(false);
            infoArray[teamIndex].setBorder(BorderFactory.createLineBorder(myOrange));
            frame.add(infoArray[teamIndex]);
            infoArray[teamIndex].setVisible(false);
        }

        showTeamInfo(0);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        ImageIcon topLeftIcon = new ImageIcon("images/nhlstatslogo.png");
        frame.setIconImage(topLeftIcon.getImage());

    }


    /** Starts an instance of the app */
    public static void main(String[] args) throws IOException, URISyntaxException{ 

        @SuppressWarnings("unused")
        App e = new App();
    }

    /** Handle clicks */
    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == moreStatsButton && currentPage != 1){
            currentPage = 1;

            try {

                showMoreStatsPage(currentSelectedTeamIndex);

            } catch (JSONException | IOException | URISyntaxException e1) {
                System.out.println("Error showing more stats page");
                e1.printStackTrace();
            }
        }

        if(e.getSource() == topBarTeams && currentPage != 0){
            currentPage = 0;
            showTeamsPage();
        }

        if(e.getSource() == rosterSearchButton){

            String searched = rosterSearch.getText();

            System.out.println("Searching for " + searched);

            for(Player player : teams[currentSelectedTeamIndex].roster){

                if(player.name.equals(searched)){

                    try {

                        showPlayerInfo(player.playerId);

                    } catch (JSONException | IOException | URISyntaxException e1) {
                        System.out.println("Error showing player info");
                        e1.printStackTrace();
                    }
                }
            }
        }

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){ // Doing this with a loop to avoid code bloat
            if(e.getSource() == teamNameButtons[teamIndex]){

                try {

                    showTeamInfo(teamIndex);
                    currentSelectedTeamIndex = teamIndex;

                } catch (IOException | URISyntaxException e1) {
                    System.out.println("Error showing team info");
                    e1.printStackTrace();
                }
            } else {
                teamNameButtons[teamIndex].setForeground(Color.white);
            }
        }
    }

    /** Shows the team info for a specified index */
    public void showTeamInfo(int index) throws IOException, URISyntaxException{

        System.out.println(teams[index].ab);

        String bonusMsg = "";

        if(teams[index].points == teams[0].points){
            bonusMsg = " (Leading the NHL)";
        }

        teamNameButtons[index].setForeground(myOrange);

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){

            infoArray[teamIndex].setVisible(false);
            images[teamIndex].setVisible(false);
        }

        if(teams[index].nextMatch == null){
            APIStuff.populateNextMatch(index);
        }

        if(teams[index].last5 == null){
            APIStuff.populatePrevMatches(index);
        }
        
        infoArray[index].setVisible(true);
        infoArray[index].setText(
                "\n  "
                + teams[index].name 
                + "\n\n  Current team points:\n   " 
                + teams[index].points + bonusMsg
                + "\n\n  Upcoming match:\n   " 
                + teams[index].nextMatch 
                + "\n\n  Last 5 matches:\n   " 
                + teams[index].last5
            );
        images[index].setVisible(true);
    }

    /** Shows more stats for chosen team */
    public void showMoreStatsPage(int index) throws JSONException, IOException, URISyntaxException{

        if(teams[index].roster == null){
            APIStuff.populateTeamRoster(index);
        }

        moreStatsButton.setBackground(myOrange);
        moreStatsButton.setVisible(false);
        topBarTeams.setBackground(Color.white);

        // Hiding things from previous page
        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){

            infoArray[teamIndex].setVisible(false);
            teamNameButtons[teamIndex].setVisible(false);
            images[teamIndex].setVisible(false);

        }
        ///////////////////////////////////
        
        ImageIcon resizedLogo = new ImageIcon("images/" + teams[index].ab + ".png");
        resizedLogo = new ImageIcon(resizedLogo.getImage().getScaledInstance(70, 50, Image.SCALE_SMOOTH));
        moreStatsTeamLogo.setIcon(resizedLogo);
        moreStatsTeamLogo.setBounds((teams[index].name.length()*11)+8, 55, 70, 70);
        frame.add(moreStatsTeamLogo);
        moreStatsTeamLogo.setVisible(true);

        moreStatsTeamName.setText(teams[index].name);
        moreStatsTeamName.setBounds(20, 75, 230, 30);
        moreStatsTeamName.setFont(myFontBigger);
        moreStatsTeamName.setBackground(Color.darkGray);
        moreStatsTeamName.setForeground(Color.white);
        moreStatsTeamName.setEditable(false);
        frame.add(moreStatsTeamName);
        moreStatsTeamName.setVisible(true);

        seasonPerformance.setBounds(70, 170, 200, 200);
        seasonPerformance.setFont(myFontLighterBigger);
        seasonPerformance.setBackground(new Color(30, 30, 30));
        seasonPerformance.setForeground(Color.white);
        seasonPerformance.setEditable(false);
        frame.add(seasonPerformance);
        seasonPerformance.setVisible(true);

        dataPoints = new JLabel[teams[index].allSeasonMatches.length];
        ImageIcon greenPoint = new ImageIcon("images/greenpoint.png");
        ImageIcon redPoint = new ImageIcon("images/redpoint.png");
        int wins = 0;
        int losses = 0;
        double winPct = 0.0;

        // Draw data for all games in the season for this team. Green point means win, red means loss. Win increases Y by 6 pixels and loss decreases by 6
        for(int i = 0; i < teams[index].allSeasonMatches.length; i++){

            dataPoints[i] = new JLabel();

            if(teams[index].allSeasonMatches[i].equals("W")){
    
                wins++;
                
                dataPoints[i].setIcon(greenPoint);

                if(i == 0){
                    dataPoints[i].setBounds(270, 280, 5, 5);
                } else {
                    dataPoints[i].setBounds(dataPoints[i-1].getX()+9, dataPoints[i-1].getY()-6, 5, 5);
                }
                frame.add(dataPoints[i]);
                dataPoints[i].setVisible(true);

            } else {

                losses++;

                dataPoints[i].setIcon(redPoint);

                if(i == 0){
                    dataPoints[i].setBounds(270, 280, 5, 5);
                } else {
                    dataPoints[i].setBounds(dataPoints[i-1].getX()+9, dataPoints[i-1].getY()+6, 5, 5);
                }
                frame.add(dataPoints[i]);
                dataPoints[i].setVisible(true);

            }
        }


        winPct = (double)wins/(double)teams[index].allSeasonMatches.length*100;
        DecimalFormat df = new DecimalFormat("0.00");

        seasonPerformance.setText("Season performance:" + "\n\nWins: " + wins + "\n\nLosses: " + losses + "\n\nWin %: " + df.format(winPct));
  
        dataPointsBackground.setBounds(40, 145, 500, 300);
        dataPointsBackground.setBackground(new Color(30, 30, 30));
        dataPointsBackground.setEditable(false);
        dataPointsBackground.setBorder(BorderFactory.createLineBorder(myOrange));
        frame.add(dataPointsBackground);
        dataPointsBackground.setVisible(true);

        // Roster info //

        rosterTitle.setText(teams[index].name + " roster:");
        rosterTitle.setBounds(70, 480, 440, 50);
        rosterTitle.setFont(myFontLighterBigger);
        rosterTitle.setBackground(new Color(30, 30, 30));
        rosterTitle.setForeground(Color.white);
        rosterTitle.setEditable(false);
        frame.add(rosterTitle);
        rosterTitle.setVisible(true);

        roster.setText("");
        for(int i = 0; i < teams[index].roster.length; i++){

            roster.setText(roster.getText() + teams[index].roster[i].name);

            if(i < teams[index].roster.length-1){
                roster.setText(roster.getText() + ", ");
            }
        }
        roster.setBounds(70, 530, 440, 170);
        roster.setFont(myFontLighter);
        roster.setBackground(new Color(30, 30, 30));
        roster.setForeground(Color.white);
        roster.setEditable(false);
        frame.add(roster);
        roster.setVisible(true);

        rosterSearch.setBounds(70, 700, 200, 30);
        rosterSearch.setFont(myFontLighter);
        rosterSearch.setBackground(new Color(30, 30, 30));
        rosterSearch.setForeground(Color.white);
        rosterSearch.setBorder(BorderFactory.createLineBorder(myOrange));
        rosterSearch.setEditable(true);
        rosterSearch.setText("Insert player name");
        frame.add(rosterSearch);
        rosterSearch.setVisible(true);

        ImageIcon searchIcon = new ImageIcon("images/search.png");
        searchIcon = new ImageIcon(searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        rosterSearchButton.setIcon(searchIcon);
        rosterSearchButton.setBorder(null);
        rosterSearchButton.setBackground(new Color(30, 30, 30));
        rosterSearchButton.setBounds(275, 700, 30, 30);
        rosterSearchButton.addActionListener(this);
        frame.add(rosterSearchButton);
        rosterSearchButton.setVisible(true);

        rosterBackground.setBounds(40, dataPointsBackground.getY()+310, 500, 300);
        rosterBackground.setBackground(new Color(30, 30, 30));
        rosterBackground.setEditable(false);
        rosterBackground.setBorder(BorderFactory.createLineBorder(myOrange));
        frame.add(rosterBackground);
        rosterBackground.setVisible(true);

    }

    public void showPlayerInfo(String playerId) throws JSONException, IOException, URISyntaxException{

        APIStuff.populatePlayerInfo(playerId, currentSelectedTeamIndex);

        for(Player player : teams[currentSelectedTeamIndex].roster){

            if(player.playerId == playerId) {
                System.out.println("Found " + player.name);
                System.out.println("Points: " + player.points);
                System.out.println("Goals: " + player.goals);
            }
        }
    }

    /** Shows the teams page */
    public void showTeamsPage(){

        moreStatsButton.setBackground(Color.white);
        moreStatsButton.setVisible(true);
        topBarTeams.setBackground(myOrange);

        // Hiding things from previous page
        for(int i = 0; i < dataPoints.length; i++){

            dataPoints[i].setVisible(false);
        }

        for(int teamIndex = 0; teamIndex < TEAM_AMOUNT; teamIndex++){

            infoArray[teamIndex].setVisible(false);
            teamNameButtons[teamIndex].setVisible(true);
            images[teamIndex].setVisible(false);
        }

        moreStatsTeamLogo.setVisible(false);
        moreStatsTeamName.setVisible(false);
        seasonPerformance.setVisible(false);
        dataPointsBackground.setVisible(false);
        rosterTitle.setVisible(false);
        roster.setVisible(false);
        rosterBackground.setVisible(false);
        rosterSearch.setVisible(false);
        rosterSearchButton.setVisible(false);
        ///////////////////////////////////

        infoArray[currentSelectedTeamIndex].setVisible(true);
        images[currentSelectedTeamIndex].setVisible(true);
    }
}
