import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Simulator extends JFrame implements Runnable, ChangeListener {
    
    static JLabel timeText = new JLabel();
    static JLabel trafficAtext = new JLabel();
    static JLabel trafficBtext = new JLabel();
    static JLabel trafficCtext = new JLabel();
    //JButtons to start, pause, and stop
    private JButton start = new JButton("Start");
    private JButton pause = new JButton("Pause");
    private JButton stop = new JButton("Stop");
    //JSliders for showing car progress
    static JSlider car1Slider = new JSlider(0, 3000);
    static JSlider car2Slider = new JSlider(0, 3000);
    static JSlider car3Slider = new JSlider(0, 3000);
    static JSlider car4Slider = new JSlider(0, 3000);
    static JSlider car5Slider = new JSlider(0, 3000);
    static JSlider car6Slider = new JSlider(0, 3000);
    
    private static boolean isRunning;
    private static final AtomicBoolean simIsRunning = new AtomicBoolean(false);
    
    //Create 3 runnable intersection objects, each on their own thread
    Intersection A = new Intersection("aThread", trafficAtext);
    Intersection B = new Intersection("bThread", trafficBtext);
    Intersection C = new Intersection("cThread", trafficCtext);
    //Create 4 runnable Car objects and a thread for each one
    Car car1 = new Car("Car1Thread", 300, 0);
    Car car2 = new Car("Car2Thread", 1000, 0);
    Car car3 = new Car("Car3Thread", 2000, 1000);
    Car car4 = new Car("Car4Thread", 2000, 1000);
    Car car5 = new Car("Car5Thread", 2000, 1000);
    Car car6 = new Car("Car6Thread", 2000, 1000);
    
    //Array of cars to loop through later
    Car[] carArray = {car1, car2, car3, car4, car5, car6};
    Intersection[] intersectionArray = {A, B, C};
    static Thread gui;
    
    Object[][] trafficData = {
        {"Car 1", car1.getPosition(), 0, 0},
        {"Car 2", car2.getPosition(), 0, 0},
        {"Car 3", car3.getPosition(), 0, 0},
        {"Car 4", car4.getPosition(), 0, 0},
        {"Car 5", car5.getPosition(), 0, 0},
        {"Car 6", car6.getPosition(), 0, 0}
    };
    //Table for displaying data
    String[] columnNames = {"Car", "X-Pos", "Y-Pos", "Speed km/h"};
    JTable dataTable = new JTable(trafficData, columnNames);
    
    
    public Simulator() {
        super("Traffic Tracker Simulator");
        isRunning = Thread.currentThread().isAlive();
        buildGUI();
        setButtons();
    }
    
    private void display() {
        setSize(700,500);
        setVisible(true);
        //Centers the frame on the screen
        setLocationRelativeTo(null);
        //Sets the window to be closeable
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void buildGUI() {
        
        JLabel welcome = new JLabel("Welcome to the Traffic Tracker Simulator!");
        JLabel welcome2 = new JLabel("Click the Start button to begin simulation");
        
        JLabel time = new JLabel("Current time: ");
        JLabel trafficLightA = new JLabel("Intersection A: ");
        JLabel trafficLightB = new JLabel("Intersection B: ");
        JLabel trafficLightC = new JLabel("Intersection C: ");
        
        //Add changeListeners to car sliders
        car1Slider.addChangeListener(this);
        car2Slider.addChangeListener(this);
        car3Slider.addChangeListener(this);
        car4Slider.addChangeListener(this);
        car5Slider.addChangeListener(this);
        car6Slider.addChangeListener(this);
        
        car1Slider.setValue(car1.getPosition());
        car2Slider.setValue(car2.getPosition());
        car3Slider.setValue(car3.getPosition());
        car4Slider.setValue(car4.getPosition());
        car5Slider.setValue(car5.getPosition());
        car6Slider.setValue(car6.getPosition());
        
        car1Slider.setMajorTickSpacing(1000);
        car1Slider.setPaintTicks(true);
        
        car2Slider.setMajorTickSpacing(1000);
        car2Slider.setPaintTicks(true);
    
        dataTable.setPreferredScrollableViewportSize(new Dimension(400, 100));
        dataTable.setFillsViewportHeight(true);
        
        JPanel dataPanel = new JPanel();  
        
        //Create the scroll pane and add the table to it
        JScrollPane scrollPane = new JScrollPane(dataTable);
        dataPanel.add(scrollPane);
        
    
        //GUI Layout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addContainerGap(30, 30) //Container gap on left side     
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)    
                .addComponent(welcome)
                .addComponent(welcome2)    
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(time)
                    .addComponent(timeText)))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)    
                .addGroup(layout.createSequentialGroup()    
                    .addComponent(start)
                    .addComponent(pause)
                    .addComponent(stop)))       
                    .addComponent(car1Slider)
                    .addComponent(car2Slider) 
                    .addComponent(car3Slider)
                    .addComponent(car4Slider)
                    .addComponent(car5Slider)
                    .addComponent(car6Slider)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)   
                .addGroup(layout.createSequentialGroup()   
                    .addComponent(trafficLightA)
                    .addComponent(trafficAtext)
                        .addContainerGap(20, 20)
                    .addComponent(trafficLightB)
                    .addComponent(trafficBtext)
                        .addContainerGap(20, 20)
                    .addComponent(trafficLightC)
                    .addComponent(trafficCtext))
                    .addComponent(dataPanel)))
                        
            .addContainerGap(30, 30) //Container gap on right side
                
        );
        
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(welcome)
                    .addComponent(welcome2))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(time)
                    .addComponent(timeText))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(start)
                    .addComponent(pause)
                    .addComponent(stop))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)    
                    .addComponent(car1Slider)
                    .addComponent(car2Slider)
                    .addComponent(car3Slider)
                    .addComponent(car4Slider)
                    .addComponent(car5Slider)
                    .addComponent(car6Slider))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(trafficLightA)
                    .addComponent(trafficAtext)
                    .addComponent(trafficLightB)
                    .addComponent(trafficBtext)
                    .addComponent(trafficLightC)
                    .addComponent(trafficCtext))
                .addComponent(dataPanel)

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addGap(20, 20, 20))
                .addGap(20, 20, 20)    
        );
        
        pack();
    }
    
    private void setButtons() {
        //Start car and intersection threads with start button
        start.addActionListener((ActionEvent e) -> {
            if(!simIsRunning.get()) {
                System.out.println(Thread.currentThread().getName() + " calling start");
                A.start();
                B.start();
                C.start();
                car1.start();
                car2.start();
                car3.start();
                car4.start();
                car5.start();
                car6.start();
                
                gui.start();
                
            }
            //Set simIsRunning to true
            simIsRunning.set(true);   
        });
        
        pause.addActionListener((ActionEvent e) -> {
            if(simIsRunning.get()) {
                //Loop through cars and intersections to call suspend()
                for(Car i: carArray) {
                    i.suspend();
                    System.out.println(Thread.currentThread().getName() + " calling suspend");
                }
                for(Intersection i: intersectionArray) {
                    //Call interrupt for sleeping intersection threads
                    i.interrupt();
                    i.suspend();
                }
                
                pause.setText("Continue");
                simIsRunning.set(false);
            } else {
                for(Car i:carArray) {
                    if(i.suspended.get()) {
                        i.resume();
                        System.out.println(Thread.currentThread().getName() + " calling resume");
                    }
                }
                for(Intersection i: intersectionArray) {
                    i.resume();
                }
                pause.setText("Pause");
                simIsRunning.set(true);
            }
        });
        
        stop.addActionListener((ActionEvent e) -> {
            if(simIsRunning.get()) {
                System.out.println(Thread.currentThread().getName() + " calling stop");
                for(Car i: carArray) {
                    i.stop();
                }
                for(Intersection i: intersectionArray) {
                    i.stop();
                }
                simIsRunning.set(false);
            }
        });
    }
    
        @Override
    public void stateChanged(ChangeEvent e) {
        //When car sliders change, update data in table
        trafficData[0][1] = car1Slider.getValue();
        trafficData[1][1] = car2Slider.getValue();
        trafficData[2][1] = car3Slider.getValue();
        trafficData[3][1] = car4Slider.getValue();
        trafficData[4][1] = car5Slider.getValue();
        trafficData[5][1] = car6Slider.getValue();
        //Update speed
        trafficData[0][3] = car1.getSpeed() + " km/h";
        trafficData[1][3] = car2.getSpeed() + " km/h";
        trafficData[2][3] = car3.getSpeed() + " km/h";
        trafficData[3][3] = car4.getSpeed() + " km/h";
        trafficData[4][3] = car5.getSpeed() + " km/h";
        trafficData[5][3] = car6.getSpeed() + " km/h";
        //Update table
        dataTable.repaint();
    }
    
    private void getData() {
        if(simIsRunning.get()) {
        //Get colors for intersections, if Red check xPosition
        switch(A.getColor()) {
            case "Red":
                for(Car i: carArray) {
                    //If car xPosition is within 500 meters and light is red, set suspend to true for car to wait
                    if(i.getPosition()>500 && i.getPosition()<1000) {
                        i.atLight.set(true);
                    }
                }
                break;
            case "Green":
                for(Car i:carArray) {
                    if(i.atLight.get()) {
                        i.resume();
                    }
                }
                break;
        }
        
        switch(B.getColor()) {
            case "Red":
                for(Car i: carArray) {
                    //If car xPosition is within 500 meters and light is red, set suspend to true for car to wait
                    if(i.getPosition()>1500 && i.getPosition()<2000) {
                        i.atLight.set(true);
                    }
                }
                break;
            case "Green":
                for(Car i:carArray) {
                    if(i.atLight.get()) {
                        i.resume();
                    }
                }
                break;
        }
        
        switch(C.getColor()) {
            case "Red":
                for(Car i: carArray) {
                    //If car xPosition is within 500 meters and light is red, set suspend to true for car to wait
                    if(i.getPosition()>2500 && i.getPosition()<3000) {
                        i.atLight.set(true);
                    }
                }
                break;
            case "Green":
                for(Car i:carArray) {
                    if(i.atLight.get()) {
                        i.resume();
                    }
                }
                break;
        }
        }
        
    }
    
    @Override
    public void run() {
        while(isRunning) {
            //While running, if simulation is running, set car sliders to car xPosition and get data
            if(simIsRunning.get()) {
            car1Slider.setValue(car1.getPosition());
            car2Slider.setValue(car2.getPosition());
            car3Slider.setValue(car3.getPosition());
            car4Slider.setValue(car4.getPosition());
            car5Slider.setValue(car5.getPosition());
            car6Slider.setValue(car6.getPosition());

            getData();
            }
        }
    }
   
    public static void main(String[] args) {
    	Simulator test = new Simulator();
        test.display();
        gui = new Thread(test);
        
        Thread time = new Thread(new Time());
        time.start();
    }   
}
