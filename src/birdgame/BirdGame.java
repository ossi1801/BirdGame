/*
    BirdGame v.0.1
    -Peli jossa kokeillaan javaFX:n perusominaisuuksia
    -Hiiren klikkauseen reagointi
    -animoinnit
    -pelikellon käsittely
    -äänten lisääminen eri pelitilanteisiin
4.1.2016, OH
 */
package birdgame;

import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransitionBuilder;
import static javafx.animation.SequentialTransitionBuilder.create;
import javafx.animation.TimelineBuilder;

import javafx.animation.TranslateTransitionBuilder;

import static javafx.scene.paint.Color.RED;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.TranslateBuilder;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author 58622 (Ossi H)
 */
public class BirdGame extends Application {
    //2.määritellään pelin ikkunan koko
    final static int WIDTH = 790;
    final static int HEIGHT = 590;
    
    
    //5. taustakuvan määrittely
    final static Image BACKGROUND_IMAGE = new Image(birdgame.BirdGame.class.getResource("background.jpg").toString());
    
    //8.puiden määrittely
    final static Image TREE1_IMAGE = new Image(birdgame.BirdGame.class.getResource("puu2.png").toString());
    final static Image TREE2_IMAGE = new Image(birdgame.BirdGame.class.getResource("puu3.png").toString());
    final static Image TREE3_IMAGE = new Image(birdgame.BirdGame.class.getResource("puu4.png").toString());
    //15 lintujen määrittely
    // Kerrotaan mistö tiedostosta linnut tulevat
    final static Image BIRD1_IMAGE = new Image(birdgame.BirdGame.class.getResource("lintu1.png").toString());
    final static Image BIRD2_IMAGE = new Image(birdgame.BirdGame.class.getResource("lintu2.png").toString());
    //tehdään linnuista oma komponenttiryhmä
    private Group birds;
    // 21. määritellään random
    ///--> käytetään linnun y-sijainnin arvontaan
    private final static Random RANDOM = new Random();
    
    private Animation current;
   
    private Object TranlateTransitionBuilder;
    //32. pistelaskuun liittyvät muuttujat
    int shots = 0;
    int hits = 0;
    //34.onko lintuun osuttu 
    //..pelin eka lintu tulee ei ole vielä
    private boolean bird_is_hitted = false;
    //45pistelaskuri
    private int points = 0;
    private Text points_float;
    private Text all_points;
    private Animation points_float_animation;
    
    int missed = 0;
    
    //38. määritetään taustamusiikin polku
    Media music = new Media(getClass().getResource("music.mp3").toString()); //“Music from https://www.zapsplat.com“
    Media shot_sound = new Media(getClass().getResource("shotgun.wav").toString());
    Media hit_sound = new Media(getClass().getResource("birddeath.wav").toString());
    
    
    //44. muutetaan hiirenosoiten tähtäimeksi kuvatiedostoksi
    final static Image crosshair = new Image (birdgame.BirdGame.class.getResource("crosshair.png").toString());
        
    @Override
    public void start(Stage primaryStage) {
      //6. taustakuvan näkyville laitto jatkuu
        
          final ImageView background = new ImageView(BACKGROUND_IMAGE);
          
          //13. lisää syvyysvaikutelmaa --> taustakuvalle blur
          background.setEffect(new BoxBlur(5, 5, 1));
          background.setOpacity(1);
          
          
          //9. puiden näkyville laitto jatkuu
          
                    final ImageView puu2 = new ImageView(TREE1_IMAGE);
                    final ImageView puu3 = new ImageView(TREE2_IMAGE);
                    final ImageView puu4 = new ImageView(TREE3_IMAGE);
                    //12. sijoitellaan puut järkevästi
                    //puun koon mukaan
                    puu2.setX(200);
                    puu2.setY(325);
                    
                    puu3.setX(500);
                    puu3.setY(325);
                    
                    puu4.setX(100);
                    puu4.setY(325);
                   
                    
              //  10. määritellään puista oma "etujoukko"
                    // ryhmä hahmoja joiden takana lintuun ei voi osua
                    final Group foreground = new Group(puu2,puu3,puu4);
                    
                     //13. asetetaan puille, eli foregroundeille varjostus
                    foreground.setEffect(new DropShadow());
                    foreground.setEffect(new Glow(1));
                    //16tehdään linnuista scenelle sijoitettavia
                    
                    final ImageView lintu1 = new ImageView(BIRD1_IMAGE);
                     final ImageView lintu2 = new ImageView(BIRD2_IMAGE);
                     //16 sijoitetaan linnut ryhmään
                     birds = new Group(lintu1, lintu2);
                     
           //4. luodaan ns. root-ryhmä: mitä
        //komponenttejä ikkunaan sijoittuu
        //7. lisätään background kuvaan komponenttiryhmään
          //17 lisätää lintu ryhmä muiden tavaroiden väliin
        final Group root = new Group(background, birds,foreground);
        
     
      
       //sijoitetaan ikkunakoon vakiot
        //scenen luovaan toimintoon
        Scene scene = new Scene(root, WIDTH, HEIGHT );
        
        scene.setCursor(new ImageCursor(crosshair,10,10));
        
       //3 ei saa surennettua kuvaa
        primaryStage.setResizable(false);
        
        //18 lintuanimointi
        //Animointi tapahtuu siten että lintukuvia 
        //vaihdellaan tietyssä tahdissa
        
        TimelineBuilder.create()
                
                //kuinka monta kertaa lintuja vaihdetaan
                .cycleCount(Animation.INDEFINITE)
                
        //mitä kuvaa vaihdellaan ja missä aikataulussa
                .keyFrames(
                //millis ---> kuinka pitkää kuva on näytössä
                //1000 == 1 sekunti
                //jos molemmilla kuvilla juuri sama aika 
                //niin kuva ei näytä vaihtuvan lainkaan
                        new KeyFrame(Duration.millis(400), (ActionEvent t)
                        ->{birds.getChildren().setAll(lintu1);
                        }
                        ),
                        new KeyFrame(Duration.millis(600), (ActionEvent t)
                        ->{birds.getChildren().setAll(lintu2);
                        }
                        )
                )
                .build().play();
                
                //26 hit animation
        //linnun siirtoanimaatio
        final Animation hitAnimation = create()
                .node(birds)
                .children(
                RotateTransitionBuilder.create()
                        .fromAngle(0)
                        .toAngle(450)
                        //kuinka pitkään pyöritetään
                        .duration(Duration.seconds(1))
                        .build(),
                        //28 lintu tippuu aöas
                        TranslateTransitionBuilder.create()
                        .toY(650)
                        .duration(Duration.seconds(1))
                        .build()
                        
                        
                        
                )
                //29
                .onFinished((ActionEvent t)->{
            startAnimation();
        
    }).build();
             //31 teksti muuttuja shottien määrän näyttöön
    //mukana x ja y sijainnit
    Text shotText = new Text(25, 25, "Shots: 0");
    shotText.setFont (Font.font("Verdana", 30));   
    shotText.setFill(WHITE);
    shotText.setStroke(BLACK);
    root.getChildren().add(shotText);
    
    
    
    Text missedText = new Text(500, 25, "Missed: 0");
    missedText.setFont (Font.font("Verdana", 30));   
    missedText.setFill(WHITE);
    missedText.setStroke(BLACK);
    root.getChildren().add(missedText);
    
    
    //43 pistemääränmuotoilut
    points_float = new Text();
    points_float.setFont (Font.font("Verdana", 30));   
    points_float.setFill(WHITE);
   points_float.setStroke(BLACK);
    root.getChildren().add(points_float);
    
    all_points = new Text(25,100, "Points: 0");
    all_points.setFont (Font.font("Verdana", 30));   
    all_points.setFill(WHITE);
   all_points.setStroke(BLACK);
    root.getChildren().add(all_points);
    
    //32 shot laskurin kasvattaminen
    //kasvaa jokaisella klikillä
    scene.setOnMousePressed((MouseEvent t)->{
        playShot_sound();
        shots++;
        
        shotText.setText("Shots: "+ shots);
        
        //36
        if(!birds.isPressed()){
            missed++;
            missedText.setText("Missed: "+missed);
        }
        
    });
    Text hitText = new Text(200, 25, "Hits: 0");
    hitText.setFont (Font.font("Verdana", 30));   
    hitText.setFill(WHITE);
    hitText.setStroke(BLACK);
    root.getChildren().add(hitText);
                
         //1. Annettiin pelille nimi
        primaryStage.setTitle("BirdGame v.0.1");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //25 hiirelle tapahtumakuuntelija
        birds.setOnMousePressed((MouseEvent me)-> {
            
            //43 shot ääni
            playHit_sound();
            
        //28.linnun liike oikealle päättyy
            current.stop();
        hitAnimation.play();
        //lisätään hittejä jos lintuun ei ollut jo osuttu
        if(bird_is_hitted == false){
        hits++;
        hitText.setText("Hits: "+hits);
        bird_is_hitted= true;
        
        
        birdPoints((int)me.getSceneX(),(int)me.getSceneY());
        
        }});
        
        //40. taustamusiikki soimaan
        playMusic();
        //19. animoinnin pakko käynnistys
        startAnimation();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
//19 luo automaattisesti tämän toiminnon
    //20 linnun siirto vasemmalta oikealle
    private void startAnimation() {
        bird_is_hitted =false;
        birds.setRotate(0);
        //22 randomin määrittelyn jälkeen linulle
        // voidaan arpoa y-sijainti
        final int y1 = RANDOM.nextInt(HEIGHT / 2  )+ HEIGHT / 4;
        final int y2 = RANDOM.nextInt(HEIGHT / 2  )+ HEIGHT / 4;
        //23. linnun siirto vasemmalta oikealle
        //lintu ensin piilossa alueella -x
        current = TranslateTransitionBuilder.create()
                //mitä siirretään
                .node(birds)
                //mistä lintu lähtee liikkeelle vasemalla
                .fromX(-100)
                //minne lentää
                //Scenen oikeassa seinässä
                .toX(WIDTH)
                //mistä lintu lähtee liikkeelle vasemalla
                .fromY(y1)
                  //minne lentää
                .toY(y2)
                //kunika kauan lintu lentää
                .duration(Duration.seconds(5))
                //kun lintu pääsee elävänä ruudun halki
                .onFinished((ActionEvent t)-> {
        startAnimation();
    }).build();
        //24. käynnistetään animointi
        current.play();
    }
    
    //Äänet alkaa
    //sama player soittaa kaikki äänet
    private void playMedia(Media m, double volume){
        //jos äänitiedosto on olemassa
        if (m != null){
            MediaPlayer mp = new MediaPlayer(m);
            mp.play();
            mp.setVolume(volume);
            
        }
        
    }
    //39.esitellään taustamusiikki
    private void playMusic(){
        try{
            playMedia(music,0.5);
        }
        catch (Exception e){
            System.out.println("Musiikissa vikaa...");
        }
    }
     private void playShot_sound(){
        try{
            playMedia(shot_sound,0.3);
        }
        catch (Exception e){
            System.out.println("Musiikissa vikaa...");
        }
    }
     private void playHit_sound(){
        try{ 
            playMedia(hit_sound,1);
        }
        catch (Exception e){
            System.out.println("Musiikissa vikaa...");
        }
    }
     //47
     private void birdPoints(int x, int y){
         
         //testitulostus konsoliin
         System.out.println(x + ", "+ y);
         //lisätää Points-Muuttujaan uusi pistemäärä
         points += 10;
         
         pointsAnimation(x, y);
     }
     //48
     private void pointsAnimation(int x, int y){
     
     
     points_float.setText("10");
     points_float.setX(x);
     points_float.setY(y);
     
     
     points_float_animation = create()
             .node(points_float)
             .children(
             TranslateTransitionBuilder.create()
             .fromY(0)
                     
             .toY(-(y - all_points.getY()))
                     .fromX(0)
                     .toX(-(x -(all_points.getX()+120)))
                     
                     .duration(Duration.millis(1000))
                     
                     
                     
     
              .build())       
             .build();
     //point float fade animation
     //häivyttää pistemäärän
     FadeTransition ft = new FadeTransition(Duration.millis(1000), points_float);
      points_float.setOpacity(1);
     //pistemäärä alkaa häipyä pienellä viiveellä
     ft.setDelay(Duration.millis(500));
     ft.setToValue(0);
     ft.setOnFinished((ActionEvent t) ->{
      
              ft.setToValue(1);
              //konaispisteet päivittyy näyttöön
             all_points.setText("Points: " + points);

     });
     
             points_float_animation.play();
             ft.play();
     }
     
}
