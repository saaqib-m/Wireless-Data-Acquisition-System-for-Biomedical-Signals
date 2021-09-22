class Button{
    color cor_ativo=color(0,255,0);
    color cor_fio=color(255);
    
    float x,y;
    String title;
    boolean mouseClick = false;
    boolean click = false;
    
    Button(float x, float y, String title){
      this.x = x;
      this.y = y;
      this.title = title;
    } 
    
    void display(){
      if (mouseClick){
         fill(0,100,0);
      } else if (click){
          fill(cor_ativo); //fill(0,255,0); 
       } else{
         fill(255);
       }
       stroke(255); 
       strokeWeight(1);
       fill(255);
       rect(width-width/12, boxLength/10, width/15, rectSize);
       textAlign(CENTER);
       textSize(10);
       fill(0);
       text(title,width-width/20,boxLength/8.5);
    }
    
    void mouseClick(){
      if (mouseButton==LEFT) {
        if (mouseX>(width - width/12) && mouseX<(width - width/12 + width/15) && mouseY>(boxLength/10) && mouseY<(boxLength/10+rectSize)){
           mouseClick=true;
        }
      }
    }
   
    boolean mouseClickd(){ // returns if it is clicked or not
      boolean ret=false;
      if (mouseButton==LEFT){
        if (mouseX>(width - width/12) && mouseX<(width - width/12 + width/15) && mouseY>(boxLength/10) && mouseY<(boxLength/10+rectSize)){
           click=!click;
           ret=true;
           mouseClick=false;
        }
      }
      return ret;
    }
 }