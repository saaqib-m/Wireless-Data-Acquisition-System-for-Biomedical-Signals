//This program takes ASCII-encoded strings
//from the serial port at 9600 baud and graphs them. It expects values in the
//range 0 to 1023, followed by a newline, or newline and carriage return
import processing.serial.*;

Serial myPort;                           // The serial port - object from serial class
String[] list = new String[3];           // For serial port
boolean newData = false;                 // Sets incoming data to false at the beginning
float inByte;                            // For incoming serial data
int xPos = 0;                            // Horizontal position of the graph (from bottom of screen) 
String inString;                         // Data recieved from serial port
int lastxPos=0;                          // X position from (0,0) of graph 
int lastheight=700;                      // Y position from (0,0) of graph 
                                         // - (Also Y length of graph)
                                         // Note: This number should be the Y value of the size() function in void setup()
int X = 1500;                            // X length of graph                     
                                         // Note: This number should be the X value of the size() function in void setup()
int halflength = X/2;       
String Title = "DAQ System";                 // Sets Title of Graph 
                                         // Note: Please change this to suit experiment
String Timer = "Time Elapsed: ";
int boxLength = 700;                     
int rectSize = 30;
Button save;

int min;
int sec;
int millisec;
int msec = millis()/1000;
int Sfreq;

Table table;

Grid grid;
float Q = 45.0;
int qSave = 0;
boolean outputOpen = false;
PrintWriter output;

String txt;
float Vout;

String inBuffer;


void setup () {
  size(1500, 700);                               // Set the window size:   
  smooth();
  myPort = new Serial(this,Serial.list()[0], 9600);    // A serialEvent() is generated when a newline character is received
  myPort.bufferUntil('\n');                     // Sets a specific byte to buffer until before calling serialEvent()
  background(250);                              // Set inital background colour
    
  grid=new Grid(0, 0, 1500,710);
  save=new Button(width-width/12,boxLength/10,"Save data \n in data.csv");
  
  table = new Table();
  table.addColumn("Time Elapsed: ");
  table.addColumn("Signal ADC Value: ");
  table.addColumn("Voltage (V): ");
  table.addColumn("Sampled Count: ");
  
  frameRate(60);
  
}

void serialEvent (Serial myPort) {
  // get the ASCII string:
  inString = myPort.readStringUntil('\n');
  //println(inString);
  String [] splitstr = splitTokens(inString);
  if (splitstr[0] != null) {
    //inString = trim(inString);                          // trim off whitespaces.
    inByte = float(trim(splitstr[0]));                    // convert to a number.
    float inScreen = map(inByte, 0, 1023, 0, height);     // map to the screen height.
    println(inByte);
    newData = true; 
  }

  Vout = 5*inByte/1024;
  
  TableRow newRow = table.addRow();
  newRow.setString("Time Elapsed: ", txt);
  newRow.setFloat("Signal ADC Value: ", inByte);
  newRow.setFloat("Voltage (V): ",Vout);
  if (splitstr[0] != null && splitstr[2] != null){
    newRow.setInt("Sampled Count: ",int(splitstr[2]));
  }
  if (splitstr[1] != null){
    Sfreq = int(splitstr[1]);}
}

void draw () {
  //grid.display();
  stroke(230);
  strokeWeight(1);
  line(width - width/10,0,width - width/10,boxLength);
  fill(230);                                            //Main block at the end 
  rect(width-width/10,0,width/10,boxLength);
  
  fill(0);                                         
  textSize(20); 
  textAlign(CENTER);                                 //Centre's Title
  text(Title, halflength, 30);
  
  fill(255); stroke(255);                          //For time box
  rect(width-width/10.5,boxLength/3.2,width/11,boxLength/10);
  stroke(0);
  fill(0);                                  
  textSize(13);                              
  text(Timer,width-width/20,boxLength/3);
  fill(0);  
  
  fill(255); stroke(255);                          //For Sfreq box
  rect(width-width/10.5,boxLength/1.7,width/11,boxLength/10);
  stroke(0);
  fill(0);                                  
  textSize(13); 
  String Title_sfreq = "Sampling Frequency:";
  text(Title_sfreq,width-width/20,boxLength/1.6);
  text(Sfreq,width-width/20,boxLength/1.5);
  fill(0); 
  
  
  int m = millis(), sec = floor(millis()/1000) % 60, min = floor(floor(millis()/1000)/60) % 60, hr = floor(floor(floor(millis()/1000)/60)/60) % 24, d = floor(floor(floor(floor(millis()/1000)/60)/60/24)),y = floor(floor(floor(floor(millis()/1000)/60)/60/24)/365);
  //println("Days:",nf(d,2),("(Y:"+y+",D:"+d%365+")"),"Hr:Min:Sec",nf(hr,2)+":"+nf(min,2)+":"+nf(sec,2));
  millisec = m/(10*10) - (m/(10*10)/ 10)*10;
  textSize(13);            
  txt = nf(hr,2)+":"+nf(min,2)+":"+nf(sec,2)+":"+millisec;
  text(txt,width-width/20,boxLength/2.5);
  
  
  save.display();
  
  if (newData) {                                        // If there is new input data:
    stroke(100,100,200);                                        // Stroke color
    strokeWeight(2);                                    // Stroke wider
    line(lastxPos, lastheight, xPos, height-inByte);    // Draw line
    lastxPos = xPos;                                    // Allows for continuous signals
    lastheight= int(height-inByte);

    if (xPos >= width-width/10) {                       // At the edge of the window, go back to the beginning
      xPos = 0;
      lastxPos= 0;
      background(250);                                  // Clear the screen.
    } 
    else {
      xPos++;                                           // Increment the horizontal position
    }    
    newData = false;                                     // Again sets new data to false
  }
}

void mouseClicked(){{
        if (save.click){
          if (outputOpen==false){ // if is not recording then start recording
            String fileName ="dataf"+"_"+nf(year(),4)+"_"+nf(month(),2)+"_"+nf(day(),2)+"_"+nf(hour(),2)+"_"+nf(minute(),2)+"_"+nf(second(),2)+".csv";
            output=createWriter(fileName);
            outputOpen=true;
            save.title="saving";
     
            saveTable(table,fileName);
       
            qSave=0;
            // when entering each data in the stream write to output.print ()
            // write to the entry routine
          } else { // save is already recording, so stop recording
            output.close();
            outputOpen=false;
            qSave=1;
            //if (qSave>10) {qSave=1;}
            save.title="Save data \n in data.csv" + "-"+qSave;
            save.click=false;
          }
        } else {
          String fileName ="data"+"_"+nf(year(),4)+"_"+nf(month(),2)+"_"+nf(day(),2)+"_"+nf(hour(),2)+"_"+nf(minute(),2)+"_"+nf(second(),2)+".csv";
          output=createWriter(fileName);
          
          saveTable(table,fileName);

          output.close();
          qSave+=1;
          //if (qSave>10) {qSave=1;}
          save.title="Save data \n in data.csv" + "-"+qSave;
          save.click=false;
        }
  }
}