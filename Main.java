/**

Author: Jalp 

**/

import chariot.Client;
import chariot.api.Games;
import chariot.model.*;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.MoveList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import chariot.ClientAuth;
import chariot.Client;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.*;
import java.lang.Record;
import java.util.List;


public class Main extends ListenerAdapter {

    private static JDABuilder jdaBuilder;
    private static JDA jda;

    public static void main(String[] args) {

        jdaBuilder = JDABuilder.createDefault("ODk2NDQxMTk1MDYzMDI5ODYw.YWHJ6w.Uqhjk9pLzwYpxWWj5ID198X-WWs");// string toke

        jdaBuilder.setStatus(OnlineStatus.ONLINE);
        jdaBuilder.setActivity(Activity.watching("Playing Lichess"));
        jdaBuilder.addEventListeners(new Main());


        try {
            jda = jdaBuilder.build();

        } catch (LoginException exception) {
            exception.printStackTrace();
        }


    }


    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {




        Client client = Client.basic(); // get the lichess client


        String command = event.getMessage().getContentRaw();

        if (command.equals(",help")) { // help method

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.white);
            embedBuilder.setTitle("Commands for LiSEBot");
            embedBuilder.setDescription("**,help** \n to see command information for the LiSEBot" + "\n\n **,profile <Lichess username>** \n to see lichess profiles for given username" + "\n\n **,top10 <Lichess variant>** \n see the top 10 players list in the variant provided, includes blitz, rapid, classical, bullet, ultrabullet, horde, racingkings, koh, atomic, chess960" + "\n\n **,streaming? <Lichess username>** Check if your favorite streamer is streaming!" + "\n\n**,team <Team name>** See team information based on team named provided. **Note: if the team has spaces include - instead of space, so the house discord server will be the-house-discord-server**" + "\n\n**,daily** see the daily Lichess puzzle and try to solve it!" + "\n\n **,arena <Lichess arena URL>** see the standings and tournament information for given tournament link" + "\n\n **,invite** invite LiSEBot to your servers");
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }


        String inv = event.getMessage().getContentRaw();

        if(inv.equals(",invite")){
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.white);
            embedBuilder.setTitle("invite me!");
            embedBuilder.setDescription("\n [Click Here to invite LiSEBot](" + "https://discord.com/api/oauth2/authorize?client_id=896441195063029860&permissions=0&scope=bot%20applications.commands" +")");
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }


        /**
         * ,profile command to see people's lichess profiles
         * input: Lichess username
         * output the whole Lichess  profile
         */

        String[] commandtwo = event.getMessage().getContentRaw().split(" ");

        if(commandtwo[0].equals(",profile")){

            Result<User> userResult = client.users().byId(commandtwo[1]);

            boolean userPresent = userResult.isPresent();


            if(userPresent== true) {  // checking if the user is present in the lichess
                User user = userResult.get();

                boolean cheater = user.tosViolation();

                boolean closedaccount = user.closed();

                if (cheater == true) { // check if the user is cheater
                    event.getChannel().sendMessage("This user has violated Lichess Terms of Service").queue();

                }
                if(closedaccount == true){ // check if user is clossed account
                    event.getChannel().sendMessage("This account is closed").queue();
                }


                if(cheater == false && closedaccount == false) {


                    // List of variables


                    String name = user.username();

                    String bio = user.profile().bio();

                    int wins = user.count().win();

                    int lose = user.count().loss();

                    int all = user.count().all();

                    int draw = user.count().draw();

                    int playing = user.count().playing();

                    String userUrl = user.url();

                    boolean pat = user.patron();






                    String sayTitle = "";

                    String titledPlayer = user.title();

                    Boolean hasTitle = false;



                    String patr = "";

                    if (pat == true) {  // check if the user is patron
                        patr += "✅";
                    } else {
                        patr += "❌";
                    }

                    if (titledPlayer != null) { // check if the user is titled, I wish I was titled player :))
                        hasTitle = true;
                    } else {
                        hasTitle = false;
                    }

                    if (hasTitle == true) {
                        sayTitle += titledPlayer;
                    } else {
                        sayTitle += "";
                    }

                    // creating the Lichess style profile with embeds


                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.white);
                    embedBuilder.setTitle("Lichess Profile for: " + name);
                    embedBuilder.setDescription("**Username:** " + "" + sayTitle + "  " + name + "\n \n **User bio:** " + bio + "\n \n **Games** \n \n" + "**All Games**: " + all + "\n" + "**wins:** " + wins + "\n **Loses:** " + lose + "\n **draws:** " + draw + "\n **Playing:** " + playing + "\n \n **Patron Status: ** " + " " + patr + " \n \n  [See Stats on Lichess](" + userUrl + ")");

                    event.getChannel().sendMessage(embedBuilder.build()).queue();

                }
            } if(userPresent == false){
                event.getChannel().sendMessage("User Not Present, Please try again").queue();
            }

        }


        /**
         * ,top10 command to see the top 10 players in the given variant
         *
         * input: Lichess variant
         *
         * output: The top 10 list with titled plus username and the rating
         */


        String[] toplist = event.getMessage().getContentRaw().split(" ");

        if(toplist[0].equals(",top10")) {

            if (toplist[1].equals("blitz")) { // top 10 list for blitz some guys have 2900 lichess rating GODz

                Result<UserTopAll> top10 = client.users().top10();

                UserTopAll top = top10.get();


                List<UserPerformance> blitzPer = top.blitz();

                String output = "";

                for (int i = 0; i < 10; i++) {
                    UserPerformance userPerformance = blitzPer.get(i);

                    Result<User> topPlayer = client.users().byId(userPerformance.username());

                    int rating = topPlayer.get().perfs().blitz().maybe().get().rating();

                    String url = topPlayer.get().url();

                    int j = i + 1;


                    output += j + " " + userPerformance.title() + " "  + "[" +userPerformance.username()+ "]" + "("+ url + ")" + " " + rating + "\n";
                }


                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.WHITE);
                embedBuilder.setTitle("Top 10 for Blitz");
                embedBuilder.setDescription(output);
                event.getChannel().sendMessage(embedBuilder.build()).queue();


            }

            // the code is duplicate of the first variant blitz but it works well :/

            if(toplist[1].equals("classical")){  // top10 list for classical

                Result<UserTopAll> top10 = client.users().top10();

                UserTopAll top = top10.get();


                List<UserPerformance> classicalPer = top.classical();

                String output = "";

                for (int i = 0; i < 10; i++) {
                    UserPerformance userPerformance = classicalPer.get(i);

                    Result<User> topPlayer = client.users().byId(userPerformance.username());

                    int rating = topPlayer.get().perfs().classical().maybe().get().rating();

                    String url = topPlayer.get().url();

                    int j = i + 1;


                    output += j + " " + userPerformance.title() + " " +"[" + userPerformance.username() +"]" + "(" + url + ")" + " " + rating + "\n";
                }


                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.WHITE);
                embedBuilder.setTitle("Top 10 for Classical");
                embedBuilder.setDescription(output);
                event.getChannel().sendMessage(embedBuilder.build()).queue();


            }


            if(toplist[1].equals("rapid")){ // top10 list for rapid you get it

                Result<UserTopAll> top10 = client.users().top10();

                UserTopAll top = top10.get();


                List<UserPerformance> rapidPer = top.rapid();

                String output = "";

                for (int i = 0; i < 10; i++) {
                    UserPerformance userPerformance = rapidPer.get(i);

                    Result<User> topPlayer = client.users().byId(userPerformance.username());

                    int rating = topPlayer.get().perfs().rapid().maybe().get().rating();

                    String url = topPlayer.get().url();

                    int j = i + 1;


                    output += j + " " + userPerformance.title() + " " +"[" + userPerformance.username() +"]" + "(" + url + ")" + " " + rating + "\n";
                }


                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.WHITE);
                embedBuilder.setTitle("Top 10 for Rapid");
                embedBuilder.setDescription(output);
                event.getChannel().sendMessage(embedBuilder.build()).queue();


            }

            if(toplist[1].equals("bullet")){

                Result<UserTopAll> top10 = client.users().top10();

                UserTopAll top = top10.get();


                List<UserPerformance> bulletPer = top.bullet();

                String output = "";

                for (int i = 0; i < 10; i++) {
                    UserPerformance userPerformance = bulletPer.get(i);

                    Result<User> topPlayer = client.users().byId(userPerformance.username());

                    int rating = topPlayer.get().perfs().bullet().maybe().get().rating();

                    String url = topPlayer.get().url();

                    int j = i + 1;


                    output += j + " " + userPerformance.title() + " " +"[" + userPerformance.username() +"]" + "(" + url + ")" + " " + rating + "\n";
                }


                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.WHITE);
                embedBuilder.setTitle("Top 10 for Bullet");
                embedBuilder.setDescription(output);
                event.getChannel().sendMessage(embedBuilder.build()).queue();


            }

            if(toplist[1].equals("ultrabullet")){

                Result<UserTopAll> top10 = client.users().top10();

                UserTopAll top = top10.get();


                List<UserPerformance> bulletPer = top.ultraBullet();

                String output = "";

                for (int i = 0; i < 10; i++) {
                    UserPerformance userPerformance = bulletPer.get(i);

                    Result<User> topPlayer = client.users().byId(userPerformance.username());

                    int rating = topPlayer.get().perfs().ultraBullet().maybe().get().rating();

                    String url = topPlayer.get().url();

                    int j = i + 1;


                    output += j + " " + userPerformance.title() + " " +"[" + userPerformance.username() +"]" + "(" + url + ")" + " " + rating + "\n";
                }


                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.WHITE);
                embedBuilder.setTitle("Top 10 for Ultrabullet");
                embedBuilder.setDescription(output);
                event.getChannel().sendMessage(embedBuilder.build()).queue();


            }

            if(toplist[1].equals("koh")){

                Result<UserTopAll> top10 = client.users().top10();

                UserTopAll top = top10.get();


                List<UserPerformance> bulletPer = top.kingOfTheHill();

                String output = "";

                for (int i = 0; i < 10; i++) {
                    UserPerformance userPerformance = bulletPer.get(i);

                    Result<User> topPlayer = client.users().byId(userPerformance.username());

                    int rating = topPlayer.get().perfs().kingOfTheHill().maybe().get().rating();

                    String url = topPlayer.get().url();

                    int j = i + 1;


                    output += j + " " + userPerformance.title() + " " +"[" + userPerformance.username() +"]" + "(" + url + ")" + " " + rating + "\n";
                }


                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.WHITE);
                embedBuilder.setTitle("Top 10 for KOH");
                embedBuilder.setDescription(output);
                event.getChannel().sendMessage(embedBuilder.build()).queue();


            }


            if(toplist[1].equals("racingkings")){

                Result<UserTopAll> top10 = client.users().top10();

                UserTopAll top = top10.get();


                List<UserPerformance> bulletPer = top.racingKings();

                String output = "";

                for (int i = 0; i < 10; i++) {
                    UserPerformance userPerformance = bulletPer.get(i);

                    Result<User> topPlayer = client.users().byId(userPerformance.username());

                    int rating = topPlayer.get().perfs().racingKings().maybe().get().rating();

                    String url = topPlayer.get().url();

                    int j = i + 1;


                    output += j + " " + userPerformance.title() + " " +"[" + userPerformance.username() +"]" + "(" + url + ")" + " " + rating + "\n";
                }


                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.WHITE);
                embedBuilder.setTitle("Top 10 for RacingKings");
                embedBuilder.setDescription(output);
                event.getChannel().sendMessage(embedBuilder.build()).queue();


            }

            if(toplist[1].equals("chess960")){

                Result<UserTopAll> top10 = client.users().top10();

                UserTopAll top = top10.get();


                List<UserPerformance> bulletPer = top.chess960();

                String output = "";

                for (int i = 0; i < 10; i++) {
                    UserPerformance userPerformance = bulletPer.get(i);

                    Result<User> topPlayer = client.users().byId(userPerformance.username());

                    int rating = topPlayer.get().perfs().chess960().maybe().get().rating();

                    String url = topPlayer.get().url();

                    int j = i + 1;


                    output += j + " " + userPerformance.title() + " " +"[" + userPerformance.username() +"]" + "(" + url + ")" + " " + rating + "\n";
                }


                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.WHITE);
                embedBuilder.setTitle("Top 10 for chess960");
                embedBuilder.setDescription(output);
                event.getChannel().sendMessage(embedBuilder.build()).queue();


            }

            if(toplist[1].equals("atomic")){

                Result<UserTopAll> top10 = client.users().top10();

                UserTopAll top = top10.get();


                List<UserPerformance> bulletPer = top.atomic();

                String output = "";

                for (int i = 0; i < 10; i++) {
                    UserPerformance userPerformance = bulletPer.get(i);

                    Result<User> topPlayer = client.users().byId(userPerformance.username());

                    int rating = topPlayer.get().perfs().atomic().maybe().get().rating();

                    String url = topPlayer.get().url();

                    int j = i + 1;


                    output += j + " " + userPerformance.title() + " " +"[" + userPerformance.username() +"]" + "(" + url + ")" + " " + rating + "\n";
                }


                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.WHITE);
                embedBuilder.setTitle("Top 10 for atomic");
                embedBuilder.setDescription(output);
                event.getChannel().sendMessage(embedBuilder.build()).queue();


            }

            if(toplist[1].equals("horde")){

                Result<UserTopAll> top10 = client.users().top10();

                UserTopAll top = top10.get();


                List<UserPerformance> bulletPer = top.horde();

                String output = "";

                for (int i = 0; i < 10; i++) {
                    UserPerformance userPerformance = bulletPer.get(i);

                    Result<User> topPlayer = client.users().byId(userPerformance.username());

                    int rating = topPlayer.get().perfs().horde().maybe().get().rating();

                    String url = topPlayer.get().url();

                    int j = i + 1;


                    output += j + " " + userPerformance.title() + " " +"[" + userPerformance.username() +"]" + "(" + url + ")" + " " + rating + "\n";
                }


                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.WHITE);
                embedBuilder.setTitle("Top 10 for horde");
                embedBuilder.setDescription(output);
                event.getChannel().sendMessage(embedBuilder.build()).queue();


            }



        }


        /**
         *
         * ,streaming? command to see the given Lichess user is streaming
         *
         * input: Lichess username
         *
         * output: the streaming status of that player
         *
         *
         */


        String[] checkStatus = event.getMessage().getContentRaw().split(" ");

        if(checkStatus[0].equals(",streaming?")){
            Result<User> result = client.users().byId(checkStatus[1]);

            if(result.isPresent()) { // checking if the user is present

                User user = result.get();


                if (user.tosViolation() == true) { // check to see if the user is a cheater
                    event.getChannel().sendMessage(checkStatus[1] + "  has violated Lichess terms of service ").queue();
                }

                if (user.closed() == true) { // check to see if the user is a closed account
                    event.getChannel().sendMessage(checkStatus[1] + " is a closed account");
                }

                if (user.streaming() == true && user.tosViolation() == false && user.closed() == false) { // displaying the streaming status
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.green);
                    String stream = user.url();

                    embedBuilder.setDescription(user.title() + " " + checkStatus[1] + " Is Streaming! " + "✅" + " View the Stream [Here in the Lichess profile](" + stream + ")");
                    event.getChannel().sendMessage(embedBuilder.build()).queue();


                } if(user.streaming() == false && user.tosViolation() == false && user.closed() == false) { // displaying the non-stream status
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.red);
                    embedBuilder.setDescription(user.title() + " " + checkStatus[1] + " Is not Streaming " + "❌" + " Check back later!");
                    event.getChannel().sendMessage(embedBuilder.build()).queue();

                }


            }else{
                event.getChannel().sendMessage("User not present, Please try again").queue();
            }


            /**
             *
             * ,team command to see the team pages of lichess team
             *
             * input: the team name
             * note: if the team has spaces it must be included with - so Lichess swiss will be Lichess-swiss
             *
             * output: the team page of the given Lichess team (you can join teams also)
             *
             */



        }


        String[] teams = event.getMessage().getContentRaw().split(" ");

        String lowerCase = teams[1].toLowerCase();


        if(teams[0].equals(",team")){
            Result<Team> result = client.teams().byTeamId(lowerCase);



            if(result.isPresent()){ // check if the team is present
                Team team = result.get();

                List<LightUser> leader = team.leaders();

                String leadernames = "";


                for(int i = 0; i < leader.size(); i++){
                    leadernames += leader.get(i).title() + " " + leader.get(i).name() + " \n";
                }

                EmbedBuilder embedBuilder = new EmbedBuilder();

                String url = "https://lichess.org/team/" + team.id();

                String tournaurl = url + "/tournaments";

                embedBuilder.setTitle(team.name() + " " + "Information");
                embedBuilder.setColor(Color.white);
                embedBuilder.setDescription("**Team name:** \n " + team.name() + "\n \n **Team Leaders:** \n" + leadernames + "\n\n **Team Members:** \n" + team.nbMembers()  + "\n\n [Join team](" + url + ")" + "\n\n [Tournaments](" + tournaurl + ")");
                event.getChannel().sendMessage(embedBuilder.build()).queue();


            }else{
                event.getChannel().sendMessage("Team not found, Please try again!");
            }



        }


        /**
         *
         * ,daily command to see the daily puzzles of lichess [I LOVE THEM]
         *
         * input: None
         *
         * output: the puzzle rating and the themes the puzzle is about, you can try the puzzle on lichess also
         *
         *
         *
         */


        String puzzle = event.getMessage().getContentRaw();

        if(puzzle.equals(",daily")){

            Result<Puzzle> dailypuzzle = client.puzzles().dailyPuzzle();

            if(dailypuzzle.isPresent()){ // check if the puzzle is present
                Puzzle puzzle1 = dailypuzzle.get();

                Puzzle.PuzzleInfo puzzleInfo = puzzle1.puzzle();

                String url = "https://lichess.org/training/daily";

                int puzzleRating = puzzleInfo.rating();
                List<String> themes = puzzleInfo.themes();

                String the = "";

                for(int i = 0; i < themes.size(); i++){
                    the += themes.get(i) + " \n ";
                }

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.white);
                embedBuilder.setTitle("Daily Puzzle");
                embedBuilder.setDescription("**Puzzle rating:** \n" + puzzleRating + "\n\n **Puzzle Themes:** \n " + the + "\n [Try the Puzzle](" + url + ")");
                event.getChannel().sendMessage(embedBuilder.build()).queue();

            }



        }





        /**
         *
         * ,arena command to see arena page of given link
         *
         * input: Lichess link for that arena
         *
         * output: The whole arena page including, the tournament name, time duration, variant, number of players,
         * and of course the player standings, also provide the team name if the tournament is a team battle
         *
         *
         *
         */




        String[] arenaResult = event.getMessage().getContentRaw().split(" ");


        if(arenaResult[0].equals(",arena")){

            String url = arenaResult[1];

            String[] spliturl = url.split("tournament/");

            String touryID = "";

            for(String a: spliturl){

                touryID = a;

            }




            Result<Arena> arenaResult1 = client.tournaments().arenaById(touryID);

            if(arenaResult1.isPresent()){

                Arena arena = arenaResult1.get();

                String name = arena.fullName();

                int numPlayers = arena.nbPlayers();

                int timeLeft = arena.minutes();

                Arena.Perf perf = arena.perf();

                String perfname = perf.name();

                String stand = "";


                Arena.Standing standing = arena.standing();


                List<Arena.Standing.Player> players=standing.players();

                for(int i = 0; i < players.size(); i++){

                    stand += players.get(i).rank() + " " + players.get(i).name() + "  " + players.get(i).rating() + "  " + players.get(i).score() + " " + players.get(i).team() + "\n ------------------------------------------------------------------- \n ";


                }


                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.white);
                embedBuilder.setTitle(name);
                embedBuilder.setDescription("**Tournament Name:** "+ name + "\n\n**Variant:** " + perfname + "\n" + "\n\n **Time Duration :** " + timeLeft + " mins"   +"\n **Total Players:** " + numPlayers + "\n\n **Standings:**" + "\n \n **Rank:**  **Username**  **Rating:**  **Score** \n \n " + stand + "\n\n" + "[View on Lichess](" + arenaResult[1] + ")");

                event.getChannel().sendMessage(embedBuilder.build()).queue();








            }


        }













    }






















































}











